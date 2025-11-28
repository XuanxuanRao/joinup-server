package cn.org.joinup.team.controller.integration;


import cn.org.joinup.common.result.Result;
import cn.org.joinup.team.domain.vo.TeamVO;
import cn.org.joinup.team.serivice.ITeamService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/integration/team")
@RequiredArgsConstructor
public class IntegrationTeamController {

    private final ITeamService teamService;

    @GetMapping("/{teamId}")
    @ApiOperation("根据id获取队伍信息")
    public Result<TeamVO> queryTeam(@PathVariable Long teamId) {
        return teamService.getTeamInfo(teamId);
    }

}
