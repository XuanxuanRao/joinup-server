package cn.org.joinup.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * @author chenxuanrao06@gmail.com
 */
@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        corsConfig.setAllowedOriginPatterns(List.of("*")); // 允许所有来源的请求

        // **配置允许的 HTTP 方法**
        // WebSocket 握手是 GET。SockJS 会用到 GET, POST, OPTIONS 等方法。
        // 允许常用的方法通常是安全的。
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        corsConfig.setAllowedHeaders(List.of("*"));

        // **是否允许发送认证信息 (Credentials)**
        // 如果前端需要发送 Cookie 或 HTTP 认证信息 (Authorization Header)，必须设置为 true。
        corsConfig.setAllowCredentials(true); // 允许发送 Cookies 或其他认证信息

        // **预检请求的缓存时间 (Max Age)**
        // 浏览器会缓存预检请求 (OPTIONS 请求) 的结果，减少 OPTIONS 请求次数。
        corsConfig.setMaxAge(3600L); // 缓存 1 小时

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // **关键：为你的 WebSocket 路径注册 CORS 配置**
        // 将上述 CORS 配置应用到所有以 /ws/ 开头的路径。
        // SockJS 会访问 /ws/info, /ws/<session>/websocket 等路径，所以使用 /ws/**
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
