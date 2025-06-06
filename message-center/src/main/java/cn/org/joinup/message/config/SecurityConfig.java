package cn.org.joinup.message.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;


/**
 * @author chenxuanrao06@gmail.com
 */
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // once request is forbidden by @PreAuthorize,
                // disable request cache so that the next request will not be replaced by the forbidden request in cache
                // see RequestCacheAwareFilter for more details
                .requestCache().disable()
                .headers().disable()
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .sessionManagement().disable()
                .exceptionHandling() // 异常处理配置
                .authenticationEntryPoint(authenticationEntryPoint);
        return http.build();
    }
}