package cn.org.joinup.team.controller;

import cn.org.joinup.common.result.PageQuery;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.team.domain.po.Team;
import cn.org.joinup.team.serivice.ITeamService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
public class TeamController {
    private final ITeamService teamService;

    @GetMapping("/list")
    public Result<PageResult<Team>> pageQuery(@RequestParam Integer themeId, @RequestBody PageQuery pageQuery) {
        Page<Team> page = teamService.page(
                pageQuery.toMpPage("create_time", true),
                new QueryWrapper<Team>().eq("theme_id", themeId)
        );

        return Result.success(PageResult.of(page, Team.class));
    }

}
