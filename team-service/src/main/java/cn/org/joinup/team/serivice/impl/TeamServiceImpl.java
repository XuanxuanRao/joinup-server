package cn.org.joinup.team.serivice.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.api.client.UserClient;
import cn.org.joinup.api.dto.TeamDTO;
import cn.org.joinup.api.dto.UserDTO;
import cn.org.joinup.api.dto.UserQuitTeamDTO;
import cn.org.joinup.api.dto.UserTeamStatisticDTO;
import cn.org.joinup.common.result.PageQuery;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.team.constants.MQConstant;
import cn.org.joinup.team.constants.RedisConstant;
import cn.org.joinup.team.domain.dto.CreateTeamDTO;
import cn.org.joinup.team.domain.dto.UpdateTeamInfoDTO;
import cn.org.joinup.team.domain.po.Team;
import cn.org.joinup.team.domain.po.TeamMember;
import cn.org.joinup.team.domain.po.TeamTagRelation;
import cn.org.joinup.team.domain.vo.BriefTeamVO;
import cn.org.joinup.team.domain.vo.TeamVO;
import cn.org.joinup.team.enums.TeamMemberRole;
import cn.org.joinup.team.enums.TeamStatus;
import cn.org.joinup.team.mapper.TeamMapper;
import cn.org.joinup.team.serivice.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
@RequiredArgsConstructor
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements ITeamService {

    private final ITeamMemberService teamMemberService;
    private final ITeamTagRelationService teamTagRelationService;
    private final IThemeService themeService;
    private final ITagService tagService;
    private final UserClient userClient;
    private final StringRedisTemplate stringRedisTemplate;
    private final SensitiveWordBs sensitiveWordBs;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public Result<TeamVO> getTeamInfo(Long teamId) {
        Team team = getById(teamId);
        if (team == null) {
            return Result.error("队伍不存在");
        }
        return Result.success(convertToTeamVO(team));
    }

    @Override
    public Result<TeamVO> userGetTeam(Long teamId) {
        Team team = getById(teamId);
        if (team == null) {
            return Result.error("队伍不存在");
        } else if (team.getStatus() == TeamStatus.BANNED) {
            return Result.error("队伍状态异常，请联系管理员");
        } else if (team.getStatus() == TeamStatus.DISBANDED) {
            return Result.error("队伍已解散");
        }

        if (team.getOpen()) {
            return Result.success(convertToTeamVO(team));
        }

        if (Objects.equals(team.getCreatorUserId(), UserContext.getUserId())) {
            return Result.success(convertToTeamVO(team));
        }

        if (teamMemberService.isTeamMember(teamId, UserContext.getUserId())) {
            return Result.success(convertToTeamVO(team));
        }

        return Result.error("非法访问");
    }

    @Override
    public Result<Void> updateTeamInfo(Long teamId, UpdateTeamInfoDTO updateTeamInfoDTO) {
        if (sensitiveWordBs.contains(updateTeamInfoDTO.getName()) || sensitiveWordBs.contains(updateTeamInfoDTO.getDescription())) {
            return Result.error("队伍名称或描述中包含敏感词，请遵守法律法规");
        }

        Result<Team> validateResult = validateCreatorOperation(teamId);
        if (Objects.equals(validateResult.getCode(), Result.ERROR)) {
            return Result.error(validateResult.getMsg());
        }

        Team updatTeam = BeanUtil.copyProperties(updateTeamInfoDTO, Team.class);
        updatTeam.setId(teamId);
        if (!updateById(updatTeam)) {
            return Result.error("更新队伍信息失败");
        }

        return Result.success();
    }

    @Override
    @Transactional
    public Result<Team> createTeam(CreateTeamDTO createTeamDTO) {
        if (sensitiveWordBs.contains(createTeamDTO.getName()) || sensitiveWordBs.contains(createTeamDTO.getDescription())) {
            return Result.error("队伍名称或描述中包含敏感词，请遵守法律法规");
        }

        Team team = BeanUtil.copyProperties(createTeamDTO, Team.class);
        team.setCreatorUserId(UserContext.getUserId());
        team.setCurrentMembersCount(1);
        team.setStatus(TeamStatus.NORMAL);
        team.setCreateTime(LocalDateTime.now());
        team.setUpdateTime(LocalDateTime.now());
        if (!save(team)) {
            return Result.error("创建队伍失败，请稍后重试");
        }

        // 添加队伍成员
        teamMemberService.save(TeamMember.builder()
                .role(TeamMemberRole.CREATOR)
                .teamId(team.getId())
                .userId(UserContext.getUserId())
                .createTime(LocalDateTime.now())
                .build());

        // 添加队伍标签
        teamTagRelationService.saveBatch(createTeamDTO.getTagIds().stream()
                .map(tagId -> new TeamTagRelation(team.getId(), tagId))
                .collect(Collectors.toList())
        );

        // 缓存
        String key = RedisConstant.CREATE_TEAM_KEY_PREFIX + UserContext.getUserId();
        stringRedisTemplate.opsForSet().add(key, String.valueOf(team.getId()));

        // 发送消息
        rabbitTemplate.convertAndSend(
                MQConstant.TEAM_EXCHANGE,
                MQConstant.TEAM_ESTABLISH_KEY,
                BeanUtil.copyProperties(team, TeamDTO.class));

        return Result.success(team);
    }

    /**
     * 添加队伍成员
     * @param teamId 队伍id
     * @param userId 用户id
     * @return 是否添加成功
     */
    public boolean addMember(Long teamId, Long userId) {
        Team team = getById(teamId);
        if (team == null || team.getStatus() != TeamStatus.NORMAL) {
            return false;
        } else if (team.getCurrentMembersCount() >= team.getMaxMembers()) {
            return false;
        }

        if (teamMemberService.isTeamMember(teamId, userId)) {
            return false;
        }

        TeamMember teamMember = new TeamMember();
        teamMember.setTeamId(teamId);
        teamMember.setUserId(userId);
        teamMember.setCreateTime(LocalDateTime.now());
        teamMember.setRole(TeamMemberRole.MEMBER);
        if (!teamMemberService.save(teamMember)) {
            return false;
        }
        return update()
                .setSql("current_members_count = current_members_count + 1")
                .eq("id", teamId)
                .update();
    }

    @Override
    public Result<List<Team>> getParticipatedTeam(TeamMemberRole role) {
        Long userId = UserContext.getUserId();
        List<Long> teamIds = teamMemberService.list(new LambdaQueryWrapper<TeamMember>()
                        .eq(TeamMember::getUserId, userId)
                        .eq(role!=null, TeamMember::getRole, role))
                .stream()
                .map(TeamMember::getTeamId)
                .collect(Collectors.toList());

        if (teamIds.isEmpty()) {
            return Result.success(List.of());
        }
        List<Team> teams = listByIds(teamIds);
        return Result.success(teams);
    }

    @Override
    public Result<UserTeamStatisticDTO> getMyTeamCount() {
        return Result.success(
                UserTeamStatisticDTO.builder()
                        .joinedTeamCount(teamMemberService.getJoinedTeamIds(UserContext.getUserId()).size())
                        .createdTeamCount(teamMemberService.getCreatedTeamIds(UserContext.getUserId()).size())
                        .build()
        );
    }

    @Override
    @Transactional
    public Result<Void> leaveTeam(Long teamId) {
        Team team = getById(teamId);
        if (team == null || team.getStatus() != TeamStatus.NORMAL) {
            return Result.error("队伍不存在");
        } else if (team.getCreatorUserId().equals(UserContext.getUserId())) {
            return Result.error("队伍创建者不能退出队伍");
        }

        if (!teamMemberService.isTeamMember(teamId, UserContext.getUserId())) {
            return Result.error("你不在该队伍中");
        }

        if (!teamMemberService.remove(new LambdaQueryWrapper<TeamMember>()
                .eq(TeamMember::getTeamId, teamId)
                .eq(TeamMember::getUserId, UserContext.getUserId()))) {
            return Result.error("退出队伍失败，请稍后重试");
        }

        // 更新队伍成员数
        update()
                .setSql("current_members_count = current_members_count - 1")
                .eq("id", teamId)
                .update();

        // 删除缓存
        String key = RedisConstant.JOIN_TEAM_KEY_PREFIX + UserContext.getUserId();
        stringRedisTemplate.opsForSet().remove(key, String.valueOf(teamId));

        // 发送消息通知 conversation
        rabbitTemplate.convertAndSend(MQConstant.TEAM_EXCHANGE, MQConstant.USER_QUIT_TEAM_KEY, UserQuitTeamDTO.builder()
                .userId(UserContext.getUserId())
                .teamId(teamId)
                .createTime(LocalDateTime.now())
                .build());

        return Result.success();
    }

    @Override
    public List<BriefTeamVO> searchTeam(String keyword) {
        // 首先根据 队伍名称模糊查询
        List<Team> teams = lambdaQuery()
                .like(Team::getName, keyword)
                .eq(Team::getStatus, TeamStatus.NORMAL)
                .eq(Team::getOpen, true)
                .list();
        System.out.println(teams);

        // 新增按照课程名搜索
        teams.addAll(lambdaQuery()
                .like(Team::getCourseName, keyword)
                .eq(Team::getStatus, TeamStatus.NORMAL)
                .eq(Team::getOpen, true)
                .list());

        // 如果没有结果，则根据队伍标签模糊查询
        Set<Long> ids = new HashSet<>();
        tagService.getTagByName(keyword).forEach(tag -> ids.addAll(teamTagRelationService.getTeamIdsByTagId(tag.getId())));
        if (ids.isEmpty()) {
            return teams.stream().map(this::convertToBriefTeamVO).collect(Collectors.toList());
        }
        teams.addAll(lambdaQuery()
                .in(Team::getId, ids)
                .eq(Team::getStatus, TeamStatus.NORMAL)
                .eq(Team::getOpen, true)
                .list());

        return teams.stream().map(this::convertToBriefTeamVO).collect(Collectors.toList());
    }

    @Override
    public Result<Void> disbandTeam(Long teamId) {
        Result<Team> validateResult = validateCreatorOperation(teamId);
        if (Objects.equals(validateResult.getCode(), Result.ERROR)) {
            return Result.error(validateResult.getMsg());
        }

        Team team = validateResult.getData();
        team.setStatus(TeamStatus.DISBANDED);
        team.setUpdateTime(LocalDateTime.now());
        if (!updateById(team)) {
            return Result.error("解散队伍失败，请稍后重试");
        }
        return Result.success();
    }

    @Override
    public Page<BriefTeamVO> pageQuery(PageQuery pageQuery, Long themeId) {
        Page<Team> page = page(
                pageQuery.toMpPage("create_time", true),
                new LambdaQueryWrapper<Team>()
                        .eq(Team::getThemeId, themeId)
                        .eq(Team::getStatus, TeamStatus.NORMAL)
                        .eq(Team::getOpen, true)
        );

        List<BriefTeamVO> collect = page.getRecords().stream().map(this::convertToBriefTeamVO).collect(Collectors.toList());
        return new Page<BriefTeamVO>()
                .setRecords(collect)
                .setTotal(page.getTotal())
                .setSize(page.getSize())
                .setCurrent(page.getCurrent());
    }

    @Override
    public Result<Void> kickOutTeam(Long teamId, Long userId) {
        Result<Team> validateResult = validateCreatorOperation(teamId);
        if (Objects.equals(validateResult.getCode(), Result.ERROR)) {
            return Result.error(validateResult.getMsg());
        }

        if (!teamMemberService.isTeamMember(teamId, userId)) {
            return Result.error("该用户不在队伍中");
        } else if (userId.equals(UserContext.getUserId())) {
            return Result.error("队伍创建者不能踢出自己");
        }

        if (!teamMemberService.remove(new LambdaQueryWrapper<TeamMember>()
                .eq(TeamMember::getTeamId, teamId)
                .eq(TeamMember::getUserId, userId))) {
            return Result.error("踢出队伍失败，请稍后重试");
        }

        // 更新队伍成员数
        update()
                .setSql("current_members_count = current_members_count - 1")
                .eq("id", teamId)
                .update();

        // 删除缓存
        String key = RedisConstant.JOIN_TEAM_KEY_PREFIX + userId;
        stringRedisTemplate.opsForSet().remove(key, String.valueOf(teamId));

        // 发送消息通知 conversation
        rabbitTemplate.convertAndSend(MQConstant.TEAM_EXCHANGE, MQConstant.USER_QUIT_TEAM_KEY, UserQuitTeamDTO.builder()
                .userId(userId)
                .teamId(teamId)
                .createTime(LocalDateTime.now())
                .build());

        return Result.success();
    }

    Result<Team> validateCreatorOperation(Long teamId) {
        Team team = getById(teamId);
        if (team == null) {
            return Result.error("队伍不存在");
        } else if (team.getStatus() == TeamStatus.BANNED) {
            return Result.error("队伍状态异常，请联系管理员");
        } else if (team.getStatus() == TeamStatus.DISBANDED) {
            return Result.error("队伍已解散");
        }

        if (!Objects.equals(team.getCreatorUserId(), UserContext.getUserId())) {
            return Result.error("非法操作");
        }

        return Result.success(team);
    }

    @Override
    public BriefTeamVO convertToBriefTeamVO(Team team) {
        BriefTeamVO briefTeamVO = new BriefTeamVO();
        BeanUtil.copyProperties(team, briefTeamVO);
        UserDTO userInfo = userClient.queryUser(team.getCreatorUserId()).getData();
        briefTeamVO.setCreatorUserName(userInfo.getUsername());
        briefTeamVO.setCreatorAvatar(userInfo.getAvatar());
        return briefTeamVO;
    }

    @Override
    public TeamVO convertToTeamVO(Team team) {
        TeamVO teamVO = new TeamVO();
        BeanUtil.copyProperties(team, teamVO);
        teamVO.setTags(teamTagRelationService.getTagsByTeamId(team.getId()));
        teamVO.setMembers(teamMemberService.getTeamMembersByTeamId(team.getId()));
        teamVO.setThemeName(themeService.getById(team.getThemeId()).getName());
        return teamVO;
    }

}
