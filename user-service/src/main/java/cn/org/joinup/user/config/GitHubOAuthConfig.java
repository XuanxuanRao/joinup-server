package cn.org.joinup.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * GitHub OAuth配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "github.oauth")
public class GitHubOAuthConfig {
    /**
     * GitHub OAuth Client ID
     */
    private String clientId;
    
    /**
     * GitHub OAuth Client Secret
     */
    private String clientSecret;
    
    /**
     * GitHub OAuth授权回调地址
     */
    private String redirectUri;
    
    /**
     * GitHub OAuth授权范围
     */
    private String scope = "user:email";
    
    /**
     * GitHub授权URL
     */
    private String authorizationUrl = "https://github.com/login/oauth/authorize";
    
    /**
     * GitHub token获取URL
     */
    private String tokenUrl = "https://github.com/login/oauth/access_token";
    
    /**
     * GitHub用户信息URL
     */
    private String userInfoUrl = "https://api.github.com/user";
}
