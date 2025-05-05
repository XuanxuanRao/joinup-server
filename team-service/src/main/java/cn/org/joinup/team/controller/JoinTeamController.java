package cn.org.joinup.team.controller;

import cn.org.joinup.common.result.Result;
import cn.org.joinup.team.domain.dto.JoinReviewAction;
import cn.org.joinup.team.domain.dto.JoinTeamDTO;
import cn.org.joinup.team.domain.vo.TeamJoinApplicationVO;
import cn.org.joinup.team.enums.TeamJoinApplicationStatus;
import cn.org.joinup.team.serivice.ITeamJoinApplicationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "队伍成员管理接口")
public class JoinTeamController {
    private final ITeamJoinApplicationService teamJoinApplicationService;


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
                    log.error("添加成员失败", e);
                    return Result.error("添加成员失败，请稍后重试");
                }
            case 1:
                try {
                    return teamJoinApplicationService.rejectJoinApplication(teamId, applicationId);
                } catch (RuntimeException e) {
                    log.error("拒绝申请失败", e);
                    return Result.error("拒绝申请失败，请稍后重试");
                }
        }
        return Result.error("Invalid Parameter: joinReviewAction.action");
    }

    @GetMapping("/{teamId}/join/list")
    @ApiOperation("获取加入队伍申请列表")
    public Result<List<TeamJoinApplicationVO>> getJoinApplicationList(@PathVariable Long teamId, @RequestParam(required = false)TeamJoinApplicationStatus status) {
        return teamJoinApplicationService.getJoinApplications(teamId, status);
    }

}
