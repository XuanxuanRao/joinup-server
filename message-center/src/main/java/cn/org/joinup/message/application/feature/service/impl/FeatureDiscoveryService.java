package cn.org.joinup.message.application.feature.service.impl;

import cn.org.joinup.message.application.feature.service.IFeatureDiscoveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeatureDiscoveryService implements IFeatureDiscoveryService {

    private final ApplicationContext applicationContext;

    @Override
    public Set<String> discoverFeatures() {
        RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping) applicationContext.getBean("requestMappingHandlerMapping");
        Set<String> features = new HashSet<>();
        Map<RequestMappingInfo, ?> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();

        for (RequestMappingInfo info : handlerMethods.keySet()) {
            Set<String> patterns = info.getPatternsCondition() != null ? 
                    info.getPatternsCondition().getPatterns() : null;
            
            if (patterns == null) {
                continue;
            }

            for (String pattern : patterns) {
                // Check if pattern starts with /message/feature/
                // Note: The pattern might contain placeholders like {ruleId}
                // We want to extract the feature name which is the segment after /feature/
                
                // Examples:
                // /message/feature/rate-monitor/rules -> rate-monitor
                // /message/feature/new-module/test -> new-module
                if (pattern.startsWith("/message/feature")) {
                    String[] parts = pattern.split("/");
                    // Find index of "feature"
                    for (int i = 0; i < parts.length - 1; i++) {
                        if ("feature".equals(parts[i])) {
                            String featureName = parts[i + 1];
                            // Exclude if it's a path variable definition (though unlikely for feature name itself)
                            if (!featureName.startsWith("{")) {
                                features.add(featureName);
                            }
                            break;
                        }
                    }
                }
            }
        }
        
        log.info("Discovered features: {}", features);
        return features;
    }
}
