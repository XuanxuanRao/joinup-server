package cn.org.joinup.team.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.team.domain.dto.AddThemeDTO;
import cn.org.joinup.team.domain.po.Theme;
import cn.org.joinup.team.serivice.IThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/theme")
@RequiredArgsConstructor
public class ThemeController {

    private final IThemeService themeService;

    @GetMapping("/list")
    public Result<List<Theme>> list() {
        return Result.success(themeService.list());
    }


}
