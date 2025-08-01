package cn.org.joinup.file.config;

import cn.org.joinup.file.interceptors.ExternalInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author chenxuanrao06@gmail.com
 */
@Component
@RequiredArgsConstructor
public class MvcConfig implements WebMvcConfigurer {

    private final ExternalInterceptor externalInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(externalInterceptor)
                .addPathPatterns("/external/**");
    }
}
