package cn.org.joinup.team.serivice.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.api.client.MessageClient;
import cn.org.joinup.api.client.UserClient;
import cn.org.joinup.api.dto.SendSiteMessageDTO;
import cn.org.joinup.api.enums.MessageType;
import cn.org.joinup.api.enums.NotifyType;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.team.constants.RedisConstant;
import cn.org.joinup.team.domain.dto.JoinTeamDTO;
import cn.org.joinup.team.domain.po.Team;
import cn.org.joinup.team.domain.po.TeamJoinApplication;
import cn.org.joinup.team.enums.TeamJoinApplicationStatus;
import cn.org.joinup.team.enums.TeamStatus;
import cn.org.joinup.team.mapper.TeamJoinApplicationMapper;
import cn.org.joinup.team.serivice.ITeamJoinApplicationService;
import cn.org.joinup.team.serivice.ITeamMemberService;
import cn.org.joinup.team.serivice.ITeamService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TeamJoinApplicationServiceImpl extends ServiceImpl<TeamJoinApplicationMapper, TeamJoinApplication> implements ITeamJoinApplicationService {

    private final ITeamService teamService;
    private final ITeamMemberService teamMemberService;
    private final StringRedisTemplate stringRedisTemplate;
    private final MessageClient messageClient;
    private final UserClient userClient;

    @Override
    public Result<Void> addJoinApplication(Long teamId, JoinTeamDTO joinTeamDTO) {
        Team team = teamService.getById(teamId);
        if (team == null || team.getStatus() != TeamStatus.NORMAL) {
            return Result.error("队伍不存在");
        } else if (team.getCurrentMembersCount() >= team.getMaxMembers()) {
            return Result.error("队伍人数已满");
        } else if (teamMemberService.isTeamMember(teamId, UserContext.getUser())) {
            return Result.error("已在队伍中");
        }

        TeamJoinApplication teamJoinApplication = BeanUtil.copyProperties(joinTeamDTO, TeamJoinApplication.class);
        teamJoinApplication.setTeamId(teamId);
        teamJoinApplication.setUserId(UserContext.getUser());
        teamJoinApplication.setStatus(TeamJoinApplicationStatus.PENDING);
        if (!save(teamJoinApplication)) {
            return Result.error("发送申请失败，请稍后重试");
        }

        messageClient.sendSite(SendSiteMessageDTO.builder()
                    .templateCode("team-join")
                    .receiverUserId(team.getCreatorUserId())
                    .messageType(MessageType.NOTICE)
                    .notifyType(NotifyType.TEAM)
                    .params(Map.of(
                            "username", userClient.getUserInfo().getData().getUsername(),
                            "teamName", team.getName()
                    ))
                    .build());

        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> approveJoinApplication(Long teamId, Long applicationId) {
        Result<TeamJoinApplication> validateResult = validateJoinApplication(teamId, applicationId);
        if (Objects.equals(validateResult.getCode(), Result.ERROR)) {
            return Result.error(validateResult.getMsg());
        }
        TeamJoinApplication application = validateResult.getData();

        application.setStatus(TeamJoinApplicationStatus.ACCEPTED);
        application.setFinishTime(LocalDateTime.now());
        if (!updateById(application)) {
            return Result.error("更新申请状态失败，请稍后重试");
        }

        boolean success = teamService.addMember(teamId, application.getUserId());
        if (!success) {
            throw new RuntimeException("添加成员失败");
        }

        log.info("用户 {} 申请加入队伍 {} 被批准", application.getUserId(), teamId);

        // 更新缓存
        String key = RedisConstant.JOIN_TEAM_KEY_PREFIX + application.getUserId();
        stringRedisTemplate.opsForSet().add(key, String.valueOf(teamId));

        return Result.success();
    }


    @Override
    @Transactional
    public Result<Void> rejectJoinApplication(Long teamId, Long applicationId) {
        Result<TeamJoinApplication> validateResult = validateJoinApplication(teamId, applicationId);
        if (Objects.equals(validateResult.getCode(), Result.ERROR)) {
            return Result.error(validateResult.getMsg());
        }
        TeamJoinApplication application = validateResult.getData();

        application.setStatus(TeamJoinApplicationStatus.REJECTED);
        application.setFinishTime(LocalDateTime.now());
        if (!updateById(application)) {
            return Result.error("更新申请状态失败，请稍后重试");
        }

        return Result.success();
    }



    private Result<TeamJoinApplication> validateJoinApplication(Long teamId, Long applicationId) {
        TeamJoinApplication application = getById(applicationId);
        if (application == null || application.getStatus() != TeamJoinApplicationStatus.PENDING || !Objects.equals(application.getTeamId(), teamId)) {
            return Result.error("申请不存在");
        }

        Team team = teamService.getById(teamId);
        if (team == null || team.getStatus() != TeamStatus.NORMAL) {
            return Result.error("队伍不存在");
        } else if (!Objects.equals(UserContext.getUser(), team.getCreatorUserId())) {
            return Result.error("非法操作");
        }

        return Result.success(application);
    }

}
