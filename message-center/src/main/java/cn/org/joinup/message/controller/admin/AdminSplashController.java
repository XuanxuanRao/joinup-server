package cn.org.joinup.message.controller.admin;

import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.message.domain.dto.request.splash.SplashResourceCreateDTO;
import cn.org.joinup.message.domain.dto.request.splash.SplashStrategyCreateDTO;
import cn.org.joinup.message.domain.dto.request.splash.SplashResourceUpdateDTO;
import cn.org.joinup.message.domain.dto.request.splash.SplashStrategyUpdateDTO;
import cn.org.joinup.message.domain.po.splash.SplashResource;
import cn.org.joinup.message.domain.po.splash.SplashStrategy;
import cn.org.joinup.message.domain.vo.SplashStatisticsVO;
import cn.org.joinup.message.service.ISplashResourceService;
import cn.org.joinup.message.service.ISplashStrategyService;
import cn.org.joinup.message.service.impl.splash.SplashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
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
    public Result<SplashResource> updateSplashResource(@PathVariable Long resourceId, @RequestBody @Validated SplashResourceUpdateDTO updateDTO) {
        log.info("update splash resource {}, data: {}", resourceId, updateDTO);
        try {
            return Result.success(splashResourceService.updateSplashResource(resourceId, updateDTO));
        } catch (Exception e) {
            log.error("update splash resource error", e);
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/strategy/{strategyId}")
    public Result<SplashStrategy> updateSplashStrategy(@PathVariable Long strategyId, @RequestBody @Validated SplashStrategyUpdateDTO updateDTO) {
        log.info("update splash strategy {}, data: {}", strategyId, updateDTO);
        try {
            return Result.success(splashStrategyService.updateSplashStrategy(strategyId, updateDTO));
        } catch (Exception e) {
            log.error("update splash strategy error", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/resource")
    public Result<SplashResource> createSplashResource(@RequestBody @Validated SplashResourceCreateDTO resourceCreateDTO) {
        log.info("create splash resource {}", resourceCreateDTO);
        try {
            return Result.success(splashResourceService.createSplashResource(resourceCreateDTO));
        } catch (Exception e) {
            log.error("create splash resource error", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/strategy")
    public Result<SplashStrategy> createSplashStrategy(@RequestBody @Validated SplashStrategyCreateDTO createStrategyDTO) {
        log.info("create splash strategy {}", createStrategyDTO);
        try {
            return Result.success(splashStrategyService.createSplashStrategy(createStrategyDTO));
        } catch (Exception e) {
            log.error("create splash strategy error", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/resource")
    public Result<PageResult<SplashResource>> listSplashResource(
            @RequestParam Integer pageNum, @RequestParam Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Boolean enabled) {
        return Result.success(splashResourceService.listSplashResource(pageNum, pageSize, title, enabled));
    }

}
