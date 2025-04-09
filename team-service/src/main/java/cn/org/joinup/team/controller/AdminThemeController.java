package cn.org.joinup.team.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.team.domain.dto.AddThemeDTO;
import cn.org.joinup.team.domain.po.Theme;
import cn.org.joinup.team.serivice.IThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/admin/theme")
@RequiredArgsConstructor
public class AdminThemeController {

    private final IThemeService themeService;

    @PostMapping("/add")
    public Result<Void> add(@RequestBody AddThemeDTO addThemeDTO) {
        Theme theme = BeanUtil.copyProperties(addThemeDTO, Theme.class);
        theme.setCreateTime(LocalDateTime.now());
        themeService.save(theme);
        return Result.success();
    }

}
