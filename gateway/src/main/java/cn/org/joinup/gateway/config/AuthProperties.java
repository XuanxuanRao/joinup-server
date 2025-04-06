package cn.org.joinup.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "joinup.auth")
public class AuthProperties {
    /**
     * 不需要认证的路径
     */
    private List<String> excludePaths;

    /**
     * 只有管理员可以访问的路径
     */
    private List<String> adminPaths;
}
