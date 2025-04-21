package cn.org.joinup.team.controller;

import cn.org.joinup.api.dto.UserTeamStatisticDTO;
import cn.org.joinup.common.result.PageQuery;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.team.domain.dto.CreateTeamDTO;
import cn.org.joinup.team.domain.dto.UpdateTeamInfoDTO;
import cn.org.joinup.team.domain.po.Team;
import cn.org.joinup.team.domain.vo.BriefTeamVO;
import cn.org.joinup.team.domain.vo.TeamVO;
import cn.org.joinup.team.enums.TeamMemberRole;
import cn.org.joinup.team.serivice.ITeamMemberService;
import cn.org.joinup.team.serivice.ITeamService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
@Api(tags = "队伍接口")
public class TeamController {
    private final ITeamService teamService;
    private final ITeamMemberService teamMemberService;

    @PostMapping("/list")
    @ApiOperation("根据主题获取队伍列表")
    public Result<PageResult<BriefTeamVO>> pageQuery(@RequestParam Long themeId, @RequestBody PageQuery pageQuery) {
        Page<BriefTeamVO> page = teamService.pageQuery(pageQuery, themeId);
        return Result.success(PageResult.of(page, BriefTeamVO.class));
    }

    @GetMapping("/{teamId}")
    @ApiOperation("根据id获取队伍信息")
    public Result<TeamVO> queryTeam(@PathVariable Long teamId) {
        return teamService.userGetTeam(teamId);
    }

    @PutMapping("/{teamId}")
    @ApiOperation("更新队伍信息")
    public Result<Void> updateTeam(@PathVariable Long teamId, @RequestBody @Validated UpdateTeamInfoDTO updateTeamInfoDTO) {
        return teamService.updateTeamInfo(teamId, updateTeamInfoDTO);
    }

    @DeleteMapping("/{teamId}")
    @ApiOperation("解散队伍")
    public Result<Void> disbandTeam(@PathVariable Long teamId) {
        return teamService.disbandTeam(teamId);
    }

    @PostMapping("/add")
    @ApiOperation("创建队伍")
    public Result<Team> createTeam(@RequestBody @Validated CreateTeamDTO createTeamDTO) {
        return teamService.createTeam(createTeamDTO);
    }

    @GetMapping("/my/list")
    @ApiOperation("获取我的队伍")
    public Result<List<Team>> getMyTeam(@RequestParam(required = false) TeamMemberRole role) {
        return teamService.getParticipatedTeam(role);
    }

    @GetMapping("/my/count")
    @ApiOperation("获取我的队伍数量信息")
    public Result<UserTeamStatisticDTO> getMyTeamCount() {
        return teamService.getMyTeamCount();
    }

    @PostMapping("/{teamId}/leave")
    @ApiOperation("退出队伍")
    public Result<Void> leaveTeam(@PathVariable Long teamId) {
        return teamService.leaveTeam(teamId);
    }

    @GetMapping("/search")
    @ApiOperation("搜索队伍")
    public Result<List<BriefTeamVO>> searchTeam(@RequestParam String keyword) {
        return Result.success(teamService.searchTeam(keyword));
    }

    @GetMapping("/{teamId}/role")
    @ApiOperation("获取用户在队伍中的角色")
    public Result<TeamMemberRole> getUserRole(@PathVariable Long teamId) {
        return Result.success(teamMemberService.getUserRole(teamId, UserContext.getUser()));
    }


}
