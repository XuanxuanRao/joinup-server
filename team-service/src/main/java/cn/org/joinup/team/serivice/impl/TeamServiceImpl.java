package cn.org.joinup.team.serivice.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.team.domain.dto.CreateTeamDTO;
import cn.org.joinup.team.domain.dto.UpdateTeamInfoDTO;
import cn.org.joinup.team.domain.po.Team;
import cn.org.joinup.team.domain.po.TeamMember;
import cn.org.joinup.team.domain.po.TeamTagRelation;
import cn.org.joinup.team.domain.vo.TeamVO;
import cn.org.joinup.team.enums.TeamMemberRole;
import cn.org.joinup.team.enums.TeamStatus;
import cn.org.joinup.team.mapper.TeamMapper;
import cn.org.joinup.team.serivice.ITeamMemberService;
import cn.org.joinup.team.serivice.ITeamService;
import cn.org.joinup.team.serivice.ITeamTagRelationService;
import cn.org.joinup.team.serivice.IThemeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
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

        if (Objects.equals(team.getCreatorUserId(), UserContext.getUser())) {
            return Result.success(convertToTeamVO(team));
        }

        if (teamMemberService.isTeamMember(teamId, UserContext.getUser())) {
            return Result.success(convertToTeamVO(team));
        }

        return Result.error("非法访问");
    }

    @Override
    public Result<Void> updateTeamInfo(Long teamId, UpdateTeamInfoDTO updateTeamInfoDTO) {
        Team team = getById(teamId);
        if (team == null) {
            return Result.error("队伍不存在");
        } else if (team.getStatus() == TeamStatus.BANNED) {
            return Result.error("队伍状态异常，请联系管理员");
        } else if (team.getStatus() == TeamStatus.DISBANDED) {
            return Result.error("队伍已解散");
        }

        if (!Objects.equals(team.getCreatorUserId(), UserContext.getUser())) {
            return Result.error("只有队伍创建者才能修改队伍信息");
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
        // todo: 敏感词检查

        Team team = BeanUtil.copyProperties(createTeamDTO, Team.class);
        team.setCreatorUserId(UserContext.getUser());
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
                .userId(UserContext.getUser())
                .createTime(LocalDateTime.now())
                .build());

        // 添加队伍标签
        teamTagRelationService.saveBatch(createTeamDTO.getTagIds().stream()
                .map(tagId -> new TeamTagRelation(team.getId(), tagId))
                .collect(Collectors.toList())
        );

        return Result.success(team);
    }

    private TeamVO convertToTeamVO(Team team) {
        TeamVO teamVO = new TeamVO();
        BeanUtil.copyProperties(team, teamVO);
        teamVO.setTags(teamTagRelationService.getTagsByTeamId(team.getId()));
        teamVO.setMembers(teamMemberService.getTeamMembersByTeamId(team.getId()));
        teamVO.setThemeName(themeService.getById(team.getThemeId()).getName());
        return teamVO;
    }

}
