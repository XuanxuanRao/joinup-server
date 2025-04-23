package cn.org.joinup.team.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.team.domain.dto.CreateTeamDTO;
import cn.org.joinup.team.domain.po.Team;
import cn.org.joinup.team.serivice.IAdminTeamService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/team")
@RequiredArgsConstructor
@Api(tags = "管理员队伍接口")
public class AdminTeamController {
    private final IAdminTeamService iAdminTeamService;

    @GetMapping("/count")
    public Result<Long> count() {
        Long count = iAdminTeamService.count();
        return Result.success(count);
    }

    // 分页加载数据
    @GetMapping("/list")
    public IPage<Team> list(Pageable pageable) {
        return iAdminTeamService.getPageTeams(pageable);
    }

    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestBody Map<String, Long> body) {
        Long id = body.get("id");
        iAdminTeamService.removeById(id);
        return Result.success();
    }

    @DeleteMapping("/delete/batch")
    public Result<Void> deleteBatch(@RequestBody Map<String, List<Long>> body) {
        List<Long> ids = body.get("ids");
        iAdminTeamService.removeByIds(ids);
        return Result.success();
    }

    // 新增队伍
    @PostMapping("/add")
    public Result<Void> add(@RequestBody CreateTeamDTO addTeamDTO) {
        Team tag = BeanUtil.copyProperties(addTeamDTO, Team.class);
        tag.setCreateTime(LocalDateTime.now());
        iAdminTeamService.save(tag);
        return Result.success();
    }

    // 更新队伍
    @PutMapping("/update/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody CreateTeamDTO addTeamDTO) {
        Team team = BeanUtil.copyProperties(addTeamDTO, Team.class);
        team.setId(id);
        UpdateWrapper<Team> uw = new UpdateWrapper<>();
        uw.eq("id", id)
                .set("name", addTeamDTO.getName())
                .set("description", addTeamDTO.getDescription())
                .set("open", addTeamDTO.getOpen())
                .set("theme_id", addTeamDTO.getThemeId())
                .set("tag_ids", addTeamDTO.getTagIds())
                .set("max_members", addTeamDTO.getMaxMembers())
                .set("update_time", LocalDateTime.now());
        iAdminTeamService.updateById(team);
        return Result.success();
    }

    // 根据队伍名称模糊查询
    @GetMapping("/querySearch")
    public Result<List<Team>> querySearch(@RequestParam String name) {
        List<Team> tags = iAdminTeamService.list();
        List<Team> result = new ArrayList<>();
        for (Team tag : tags) {
            if (tag.getName().contains(name)) {
                result.add(tag);
            }
        }

        return Result.success(result);
    }

    // 返回模糊查询数量
    @GetMapping("/searchCount")
    public Result<Long> searchCount(@RequestParam String name) {
        List<Team> tags = iAdminTeamService.list();
        Long count = 0L;
        for (Team tag : tags) {
            if (tag.getName().contains(name)) {
                count++;
            }
        }
        return Result.success(count);
    }

    // 分页显示模糊查询结果
    @GetMapping("/search")
    public IPage<Team> search(@RequestParam String name,
                             @RequestParam("page") int page, @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return iAdminTeamService.getPageTeamsSearch(name, pageable);
    }

}
