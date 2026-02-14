package cn.org.joinup.message.interfaces.controller.user;

import cn.org.joinup.common.result.Result;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.message.application.feature.service.IFeatureDiscoveryService;
import cn.org.joinup.message.application.feature.service.IFeatureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/message/feature")
@RequiredArgsConstructor
@Slf4j
public class FeatureController {

    private final IFeatureDiscoveryService featureDiscoveryService;
    private final IFeatureService featureService;

    @GetMapping
    public Result<Set<String>> getAccessibleFeatures() {
        try {
            Set<String> all = featureDiscoveryService.discoverFeatures();
            return Result.success(all.stream()
                    .filter(feature -> featureService.canAccess(feature, UserContext.getUserId()))
                    .collect(Collectors.toSet()));
        } catch (Exception e) {
            log.error("Failed to get accessible features for userId={}", UserContext.getUserId(), e);
            return Result.success(Set.of());
        }
    }

}
