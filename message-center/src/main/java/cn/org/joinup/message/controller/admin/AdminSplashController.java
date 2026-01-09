package cn.org.joinup.message.controller.admin;

import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.message.domain.dto.request.SplashResourceUpdateDTO;
import cn.org.joinup.message.domain.dto.request.SplashStrategyUpdateDTO;
import cn.org.joinup.message.domain.po.splash.SplashResource;
import cn.org.joinup.message.domain.po.splash.SplashStrategy;
import cn.org.joinup.message.domain.vo.SplashStatisticsVO;
import cn.org.joinup.message.service.ISplashResourceService;
import cn.org.joinup.message.service.ISplashStrategyService;
import cn.org.joinup.message.service.impl.splash.SplashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admin/message/splash")
@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminSplashController {

    private final SplashService splashService;
    private final ISplashResourceService splashResourceService;
    private final ISplashStrategyService splashStrategyService;

    @GetMapping("/list")
    public Result<PageResult<SplashStatisticsVO>> listSplash(
            @RequestParam Integer pageNum, @RequestParam Integer pageSize,
            @RequestParam(required = false, defaultValue = "true") Boolean enabled,
            @RequestParam(required = false, defaultValue = "") String platform) {
        return Result.success(splashService.listSplashStatistics(pageNum, pageSize, enabled, platform));
    }

    @PutMapping("/resource/{resourceId}")
    public Result<SplashResource> updateSplashResource(@PathVariable Long resourceId, @RequestBody SplashResourceUpdateDTO updateDTO) {
        log.info("update splash resource {}, data: {}", resourceId, updateDTO);
        try {
            return Result.success(splashResourceService.updateSplashResource(resourceId, updateDTO));
        } catch (Exception e) {
            log.error("update splash resource error", e);
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/strategy/{strategyId}")
    public Result<SplashStrategy> updateSplashStrategy(@PathVariable Long strategyId, @RequestBody SplashStrategyUpdateDTO updateDTO) {
        log.info("update splash strategy {}, data: {}", strategyId, updateDTO);
        try {
            return Result.success(splashStrategyService.updateSplashStrategy(strategyId, updateDTO));
        } catch (Exception e) {
            log.error("update splash strategy error", e);
            return Result.error(e.getMessage());
        }
    }


}
