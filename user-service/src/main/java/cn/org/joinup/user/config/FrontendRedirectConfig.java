package cn.org.joinup.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 前端回调地址配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "frontend.redirect")
public class FrontendRedirectConfig {
    /**
     * 允许的前端回调地址域名白名单
     */
    private List<String> allowedDomains;
    
    /**
     * 是否允许相对路径
     */
    private boolean allowRelativePaths = true;
}
