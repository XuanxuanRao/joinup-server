package cn.org.joinup.team.controller;

import cn.org.joinup.common.result.Result;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.team.domain.dto.JoinReviewAction;
import cn.org.joinup.team.domain.dto.JoinTeamDTO;
import cn.org.joinup.team.domain.po.Team;
import cn.org.joinup.team.domain.po.TeamJoinApplication;
import cn.org.joinup.team.enums.TeamJoinApplicationStatus;
import cn.org.joinup.team.enums.TeamStatus;
import cn.org.joinup.team.serivice.ITeamJoinApplicationService;
import cn.org.joinup.team.serivice.ITeamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
@Api(tags = "队伍成员管理接口")
public class JoinTeamController {
    private final ITeamJoinApplicationService teamJoinApplicationService;
    private final ITeamService teamService;


    @PostMapping("/{teamId}/join/apply")
    @ApiOperation("申请加入队伍")
    public Result<Void> addJoinApplication(@PathVariable Long teamId, @RequestBody JoinTeamDTO joinTeamDTO) {
        return teamJoinApplicationService.addJoinApplication(teamId, joinTeamDTO);
    }

    @PostMapping("/{teamId}/join/review/{applicationId}")
    @ApiOperation("审批加入队伍申请")
    public Result<Void> reviewJoinApplication(@PathVariable Long teamId, @PathVariable Long applicationId, @RequestBody JoinReviewAction joinReviewAction) {
        switch (joinReviewAction.getAction()) {
            case 0:
                try {
                    return teamJoinApplicationService.approveJoinApplication(teamId, applicationId);
                } catch (RuntimeException e) {
                    return Result.error("添加成员失败，请稍后重试");
                }
            case 1:
                try {
                    return teamJoinApplicationService.rejectJoinApplication(teamId, applicationId);
                } catch (RuntimeException e) {
                    return Result.error("拒绝申请失败，请稍后重试");
                }
        }
        return Result.error("Invalid Parameter: joinReviewAction.action");
    }

    @GetMapping("/{teamId}/join/list")
    @ApiOperation("获取加入队伍申请列表")
    public Result<List<TeamJoinApplication>> getJoinApplicationList(@PathVariable Long teamId, @RequestParam(required = false)TeamJoinApplicationStatus status) {
        Team team = teamService.getById(teamId);
        if (team == null || team.getStatus() != TeamStatus.NORMAL) {
            return Result.error("队伍不存在");
        } else if (!Objects.equals(team.getCreatorUserId(), UserContext.getUser())) {
            return Result.error("非法操作");
        }

        // 联合索引(teamId, status)，必须先查teamId再查status，否组索引失效
        return Result.success(teamJoinApplicationService.lambdaQuery()
                .eq(TeamJoinApplication::getTeamId, teamId)
                .eq(status != null, TeamJoinApplication::getStatus, status)
                .list());
    }

}
