package cn.org.joinup.team.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.team.domain.dto.AddThemeDTO;
import cn.org.joinup.team.domain.po.Theme;
import cn.org.joinup.team.serivice.IAdminThemeService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/admin/theme")
@RequiredArgsConstructor
public class AdminThemeController {

    private final IAdminThemeService iAdminThemeService;

    // 计算数据数量
    @GetMapping("/count")
    public Result<Long> count() {
        Long count = iAdminThemeService.count();
        return Result.success(count);
    }

    // 分页加载数据
    @GetMapping("/list")
    public IPage<Theme> list(Pageable pageable) {
        return iAdminThemeService.getPageThemes(pageable);
    }

    @PostMapping("/delete")
    public Result<Void> delete(@RequestBody Map<String, Long> body) {
        Long id = body.get("id");
        iAdminThemeService.removeById(id);
        return Result.success();
    }

    @PostMapping("/delete/batch")
    public Result<Void> deleteBatch(@RequestBody Map<String, List<Long>> body) {
        List<Long> ids = body.get("ids");
        iAdminThemeService.removeByIds(ids);
        return Result.success();
    }

    // 新增主题
    @PostMapping("/add")
    public Result<Void> add(@RequestBody AddThemeDTO addThemeDTO) {
        Theme theme = BeanUtil.copyProperties(addThemeDTO, Theme.class);
        theme.setCreateTime(LocalDateTime.now());
        iAdminThemeService.save(theme);
        return Result.success();
    }

    // 更新主题
    @PutMapping("/update/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody AddThemeDTO addThemeDTO) {
        Theme theme = BeanUtil.copyProperties(addThemeDTO, Theme.class);
        theme.setId(id);
        iAdminThemeService.updateById(theme);
        return Result.success();
    }

    // 根据主题名称模糊查询
    @GetMapping("/querySearch")
    public Result<List<Theme>> querySearch(@RequestParam String name) {
        List<Theme> themes = iAdminThemeService.list();
        List<Theme> result = new ArrayList<>();
        for (Theme theme : themes) {
            if (theme.getName().contains(name)) {
                result.add(theme);
            }
        }

        return Result.success(result);
    }

    // 返回模糊查询数量
    @GetMapping("/searchCount")
    public Result<Long> searchCount(@RequestParam String name) {
        List<Theme> themes = iAdminThemeService.list();
        Long count = 0L;
        for (Theme theme : themes) {
            if (theme.getName().contains(name)) {
                count++;
            }
        }
        return Result.success(count);
    }

    // 分页显示模糊查询结果
    @GetMapping("/search")
    public IPage<Theme> search(@RequestParam String name,
                               @RequestParam("page") int page, @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return iAdminThemeService.getPageThemesSearch(name, pageable);
    }



}
