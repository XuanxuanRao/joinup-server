package cn.org.joinup.team.serivice;

import cn.org.joinup.common.result.Result;
import cn.org.joinup.team.domain.dto.JoinTeamDTO;
import cn.org.joinup.team.domain.po.TeamJoinApplication;
import cn.org.joinup.team.domain.vo.TeamJoinApplicationVO;
import cn.org.joinup.team.enums.TeamJoinApplicationStatus;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ITeamJoinApplicationService extends IService<TeamJoinApplication> {
    Result<Void> addJoinApplication(Long teamId, JoinTeamDTO joinTeamDTO);

    /**
     * Approves a join application.
     * Rolls back the transaction if adding the member fails.
     *
     * @param teamId        The ID of the team.
     * @param applicationId The ID of the application to approve.
     * @return Result indicating success or failure.
     * @throws RuntimeException if adding the member fails, triggering rollback.
     */
    Result<Void> approveJoinApplication(Long teamId, Long applicationId);

    /**
     * Rejects a join application.
     * @param teamId id of the team
     * @param applicationId id of the application
     * @return Result indicating success or failure.
     */
    Result<Void> rejectJoinApplication(Long teamId, Long applicationId);

    Result<List<TeamJoinApplicationVO>> getJoinApplications(Long teamId, TeamJoinApplicationStatus status);
}
