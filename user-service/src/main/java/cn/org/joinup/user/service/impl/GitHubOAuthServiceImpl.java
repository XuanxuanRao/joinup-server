package cn.org.joinup.user.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.org.joinup.common.exception.BadRequestException;
import cn.org.joinup.user.config.GitHubOAuthConfig;
import cn.org.joinup.user.domain.po.User;
import cn.org.joinup.user.domain.vo.ThirdPartyAuthResponseVO;
import cn.org.joinup.user.enums.UserType;
import cn.org.joinup.user.service.IGitHubOAuthService;
import cn.org.joinup.user.service.IUserService;
import cn.org.joinup.user.util.JwtTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHException;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * GitHub OAuth服务实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubOAuthServiceImpl implements IGitHubOAuthService {
    
    private final GitHubOAuthConfig gitHubOAuthConfig;
    private final IUserService userService;
    private final JwtTool jwtTool;

    @Override
    public String getAuthorizationUrl(String state, String frontendRedirectUri) {
        try {
            // 构建完整的state参数，包含随机值和前端回调地址
            String fullState = state;
            if (StrUtil.isNotBlank(frontendRedirectUri)) {
                fullState = state + "|" + URLEncoder.encode(frontendRedirectUri, StandardCharsets.UTF_8);
            }

            return gitHubOAuthConfig.getAuthorizationUrl() +
                    "?client_id=" + gitHubOAuthConfig.getClientId() +
                    "&redirect_uri=" + URLEncoder.encode(gitHubOAuthConfig.getRedirectUri(), StandardCharsets.UTF_8) +
                    "&scope=" + gitHubOAuthConfig.getScope() +
                    "&state=" + fullState;
        } catch (Exception e) {
            log.error("编码URL失败", e);
            throw new BadRequestException("生成授权URL失败");
        }
    }
    
    @Override
    public ThirdPartyAuthResponseVO handleCallback(String code, String state) {
        // 1. 验证状态参数，防止CSRF攻击
        if (StrUtil.isBlank(state)) {
            throw new BadRequestException("无效的请求");
        }
        
        // 2. 通过授权码获取access token
        String accessToken = getAccessToken(code);
        
        // 3. 使用access token获取GitHub用户信息
        GHUser userInfo = getUserInfo(accessToken);
        
        // 4. 处理用户信息，创建或更新用户
        User user = processUser(userInfo);
        
        // 5. 生成token并返回
        return generateAuthResponse(user);
    }
    
    /**
     * 获取GitHub access token
     */
    private String getAccessToken(String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("client_id", gitHubOAuthConfig.getClientId());
        params.put("client_secret", gitHubOAuthConfig.getClientSecret());
        params.put("code", code);
        params.put("redirect_uri", gitHubOAuthConfig.getRedirectUri());
        
        HttpResponse response = HttpRequest.post(gitHubOAuthConfig.getTokenUrl())
                .header("Accept", "application/json")
                .form(params)
                .timeout((int) Duration.ofSeconds(10).toMillis())
                .execute();
        
        if (!response.isOk()) {
            log.error("获取GitHub access token失败: {}", response.body());
            throw new BadRequestException("获取GitHub授权失败");
        }
        
        JSONObject result = JSONUtil.parseObj(response.body());
        if (result.containsKey("error")) {
            log.error("GitHub授权错误: {}", result.getStr("error_description"));
            throw new BadRequestException("GitHub授权失败: " + result.getStr("error_description"));
        }

        String accessToken = result.getStr("access_token");
        if (StrUtil.isBlank(accessToken)) {
            log.error("GitHub响应中缺少access_token字段: {}", response.body());
            throw new BadRequestException("GitHub授权失败：未返回有效的access_token");
        }
        return accessToken;
    }
    
    /**
     * 获取GitHub用户信息
     */
    private GHUser getUserInfo(String accessToken) {
        try {
            GitHub github = new GitHubBuilder()
                    .withOAuthToken(accessToken)
                    .build();
            
            return github.getMyself();
        } catch (IOException e) {
            log.error("网络错误：获取GitHub用户信息失败", e);
            throw new BadRequestException("网络错误，请检查网络连接");
        } catch (GHException e) {
            log.error("GitHub API错误：获取用户信息失败", e);
            if (e.getMessage().contains("Bad credentials")) {
                throw new BadRequestException("无效的访问令牌");
            } else {
                throw new BadRequestException("获取GitHub用户信息失败: " + e.getMessage());
            }
        } catch (Exception e) {
            log.error("未知错误：获取GitHub用户信息失败", e);
            throw new BadRequestException("获取GitHub用户信息失败");
        }
    }
    
    /**
     * 处理GitHub用户信息，创建或更新用户
     */
    private User processUser(GHUser userInfo) {
        String githubId = String.valueOf(userInfo.getId());
        String username = userInfo.getLogin();
        String email = null;
        try {
            email = userInfo.getEmail();
        } catch (IOException e) {
            log.warn("无法获取GitHub用户的邮箱地址，可能是因为权限不足或用户未公开邮箱");
        }
        String avatar = userInfo.getAvatarUrl();
        
        // 查找现有用户
        User user = userService.lambdaQuery()
                .eq(User::getGithubId, githubId)
                .one();
        
        if (user == null) {
            // 新用户，自动注册
            log.info("GitHub用户首次登录，自动注册: {}", username);
            user = User.builder()
                    .avatar(avatar)
                    .email(email)
                    .githubId(githubId)
                    .username("github_" + username.substring(0, Math.min(username.length(), 20)))
                    .userType(UserType.INTERNAL)
                    .role("USER")
                    .build();
            userService.save(user);
            log.info("GitHub用户注册成功，userId={}", user.getId());
        } else {
            // 现有用户，更新信息
            boolean updated = false;
            if (email != null && !email.equals(user.getEmail())) {
                user.setEmail(email);
                updated = true;
            }
            if (avatar != null && !avatar.equals(user.getAvatar())) {
                user.setAvatar(avatar);
                updated = true;
            }
            if (updated) {
                userService.updateById(user);
            }
        }
        
        return user;
    }
    
    /**
     * 生成认证响应
     */
    private ThirdPartyAuthResponseVO generateAuthResponse(User user) {
        // 生成token，过期时间设置为30分钟
        String token = jwtTool.createToken(
                user.getId(),
                user.getRole(),
                user.getAppKey(),
                UserType.INTERNAL,
                Duration.ofMinutes(30)
        );

        return ThirdPartyAuthResponseVO.builder()
                .token(token)
                .expireAt(LocalDateTime.now().plusMinutes(30))
                .build();
    }
}
