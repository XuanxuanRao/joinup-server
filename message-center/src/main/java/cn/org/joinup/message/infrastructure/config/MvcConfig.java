package cn.org.joinup.message.infrastructure.config;

import cn.org.joinup.message.infrastructure.interceptor.FeatureAccessInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class MvcConfig implements WebMvcConfigurer {

    private final FeatureAccessInterceptor featureAccessInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Intercept all endpoints under /message/feature/**
        // Note: The actual path depends on context-path and controller mapping.
        // Assuming context-path is "/" or handled, and controller is @RequestMapping("/message/feature/rate-monitor")
        registry.addInterceptor(featureAccessInterceptor)
                .addPathPatterns("/message/feature/**")
                .order(10); // Ensure it runs after UserInfoInterceptor
    }
}
