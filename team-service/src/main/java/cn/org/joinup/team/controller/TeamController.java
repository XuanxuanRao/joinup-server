package cn.org.joinup.team.controller;

import cn.org.joinup.common.result.PageQuery;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.team.domain.dto.CreateTeamDTO;
import cn.org.joinup.team.domain.dto.UpdateTeamInfoDTO;
import cn.org.joinup.team.domain.po.Team;
import cn.org.joinup.team.domain.vo.TeamVO;
import cn.org.joinup.team.serivice.ITeamService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
@Api(tags = "队伍接口")
public class TeamController {
    private final ITeamService teamService;

    @GetMapping("/list")
    @ApiOperation("根据主题获取队伍列表")
    public Result<PageResult<Team>> pageQuery(@RequestParam Integer themeId, @RequestBody PageQuery pageQuery) {
        Page<Team> page = teamService.page(
                pageQuery.toMpPage("create_time", true),
                new QueryWrapper<Team>().eq("theme_id", themeId)
        );

        return Result.success(PageResult.of(page, Team.class));
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

    @PostMapping("/add")
    public Result<Team> createTeam(@RequestBody @Validated CreateTeamDTO createTeamDTO) {
        return teamService.createTeam(createTeamDTO);
    }

}
