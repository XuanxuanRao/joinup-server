package cn.org.joinup.message.interfaces.controller.admin;

import cn.org.joinup.common.result.Result;
import cn.org.joinup.message.application.feature.dto.FeaturePublicDTO;
import cn.org.joinup.message.application.feature.dto.FeatureWhitelistDTO;
import cn.org.joinup.message.interfaces.vo.FeatureVO;
import cn.org.joinup.message.application.feature.service.IFeatureService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/message/feature")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "管理端-功能权限管理")
public class AdminFeatureController {

    private final IFeatureService featureService;

    @ApiOperation("获取所有可用的功能列表")
    @GetMapping("/list")
    public Result<List<FeatureVO>> listFeatures() {
        return Result.success(featureService.listFeatures());
    }

    @ApiOperation("获取功能白名单")
    @GetMapping("/{featureName}/whitelist")
    public Result<Set<Long>> getWhitelist(@PathVariable String featureName) {
        return Result.success(featureService.getWhitelist(featureName));
    }

    @ApiOperation("添加用户到功能白名单")
    @PostMapping("/{featureName}/whitelist")
    public Result<Void> addUsersToWhitelist(@PathVariable String featureName,
                                            @RequestBody @Validated FeatureWhitelistDTO whitelistDTO) {
        log.info("Adding users {} to whitelist for feature {}", whitelistDTO.getUserIds(), featureName);
        try {
            featureService.addUsersToWhitelist(featureName, whitelistDTO.getUserIds());
            return Result.success();
        } catch (IllegalArgumentException e) {
            log.error("Error adding users {} to whitelist for feature {}", whitelistDTO.getUserIds(), featureName, e);
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("从功能白名单移除用户")
    @DeleteMapping("/{featureName}/whitelist")
    public Result<Void> removeUsersFromWhitelist(@PathVariable String featureName,
                                                 @RequestBody @Validated FeatureWhitelistDTO whitelistDTO) {
        log.info("Removing users {} from whitelist for feature {}", whitelistDTO.getUserIds(), featureName);
        try {
            featureService.removeUsersFromWhitelist(featureName, whitelistDTO.getUserIds());
            return Result.success();
        } catch (IllegalArgumentException e) {
            log.error("Error removing users {} from whitelist for feature {}", whitelistDTO.getUserIds(), featureName, e);
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("设置功能是否全量开放")
    @PutMapping("/{featureName}/public")
    public Result<Void> setFeaturePublic(@PathVariable String featureName,
                                         @RequestBody @Validated FeaturePublicDTO publicDTO) {
        log.info("Setting feature {} to public: {}", featureName, publicDTO.getIsPublic());
        try {
            featureService.setFeaturePublic(featureName, publicDTO.getIsPublic());
            return Result.success();
        } catch (IllegalArgumentException e) {
            log.error("Error setting feature {} to public: {}", featureName, publicDTO.getIsPublic(), e);
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("查询功能是否全量开放")
    @GetMapping("/{featureName}/public")
    public Result<Boolean> isFeaturePublic(@PathVariable String featureName) {
        return Result.success(featureService.isFeaturePublic(featureName));
    }
}
