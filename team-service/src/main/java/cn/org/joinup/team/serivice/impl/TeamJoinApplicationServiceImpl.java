package cn.org.joinup.team.serivice.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.common.util.UserContext;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
@RequiredArgsConstructor
public class TeamJoinApplicationServiceImpl extends ServiceImpl<TeamJoinApplicationMapper, TeamJoinApplication> implements ITeamJoinApplicationService {

    private final ITeamService teamService;
    private final ITeamMemberService teamMemberService;

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

        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> approveJoinApplication(Long teamId, Long applicationId) {
        TeamJoinApplication teamJoinApplication = getById(applicationId);
        if (teamJoinApplication == null || teamJoinApplication.getStatus() != TeamJoinApplicationStatus.PENDING || !Objects.equals(teamJoinApplication.getTeamId(), teamId)) {
            return Result.error("申请不存在");
        }

        Team team = teamService.getById(teamId);
        if (team == null || team.getStatus() != TeamStatus.NORMAL) {
            return Result.error("队伍不存在");
        } else if (!Objects.equals(UserContext.getUser(), team.getCreatorUserId())) {
            return Result.error("非法操作");
        }

        // --- Transactional operations start here ---
        teamJoinApplication.setStatus(TeamJoinApplicationStatus.ACCEPTED);
        teamJoinApplication.setFinishTime(LocalDateTime.now());
        if (!updateById(teamJoinApplication)) {
            return Result.error("更新申请状态失败，请稍后重试");
        }

        boolean success = teamService.addMember(teamId, teamJoinApplication.getUserId());
        if (!success) {
            throw new RuntimeException("添加成员失败");
        }

        return Result.success();
    }

}
