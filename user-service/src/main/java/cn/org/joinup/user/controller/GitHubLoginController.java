package cn.org.joinup.user.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

import cn.org.joinup.common.result.Result;
import cn.org.joinup.user.config.FrontendRedirectConfig;
import cn.org.joinup.user.domain.vo.ThirdPartyAuthResponseVO;
import cn.org.joinup.user.service.IGitHubOAuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * GitHub登录控制器
 */
@RestController
@RequestMapping("/user/auth/github")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "GitHub登录")
public class GitHubLoginController {

    private final IGitHubOAuthService gitHubOAuthService;
    private final FrontendRedirectConfig frontendRedirectConfig;

    @ApiOperation("获取GitHub授权URL")
    @GetMapping("/authorize")
    public Result<String> getGitHubAuthorizeUrl(
            @RequestParam(value = "frontend_redirect_uri", required = false) String frontendRedirectUri,
            HttpServletRequest request) {
        // 生成随机state参数，用于防止CSRF攻击
        String state = IdUtil.simpleUUID();
        // 构建GitHub授权URL
        String authorizeUrl = gitHubOAuthService.getAuthorizationUrl(state, frontendRedirectUri);
        log.info("生成GitHub授权URL: {}", authorizeUrl);
        return Result.success(authorizeUrl);
    }

    @ApiOperation("GitHub授权回调")
    @GetMapping("/callback")
    public void githubCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            HttpServletResponse response) throws IOException {
        log.info("GitHub授权回调，code={}, state={}", code, state);

        // 解析state参数，获取原始state值和前端回调地址
        String originalState = state;
        String frontendRedirectUri = null;

        if (state.contains("|")) {
            String[] parts = state.split("\\|", 2);
            originalState = parts[0];
            if (parts.length > 1) {
                try {
                    frontendRedirectUri = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
                } catch (Exception e) {
                    log.error("解析前端回调地址失败", e);
                }
            }
        }

        // 对前端回调地址进行校验，防止开放重定向
        String safeFrontendRedirectUri = sanitizeFrontendRedirectUri(frontendRedirectUri);

        try {
            // 处理GitHub回调，获取认证响应（使用原始state值）
            ThirdPartyAuthResponseVO authResponse = gitHubOAuthService.handleCallback(code, originalState);
            log.info("GitHub登录成功");

            if (StrUtil.isNotBlank(safeFrontendRedirectUri)) {
                // 重定向到前端回调页面，携带token参数
                String redirectUrl = safeFrontendRedirectUri + "?token=" + authResponse.getToken() +
                        "&expireAt=" + authResponse.getExpireAt();
                log.info("重定向到前端回调页面: {}", redirectUrl);
                response.sendRedirect(redirectUrl);
            } else {
                // 返回JSON响应
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JSONUtil.toJsonStr(Result.success(authResponse)));
            }
        } catch (Exception e) {
            log.error("GitHub登录失败: {}", e.getMessage(), e);
            if (StrUtil.isNotBlank(safeFrontendRedirectUri)) {
                // 重定向到前端回调页面，携带错误信息
                String errorMsg = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
                String redirectUrl = safeFrontendRedirectUri + "?error=" + errorMsg;
                log.info("GitHub登录失败，重定向到前端回调页面: {}", redirectUrl);
                response.sendRedirect(redirectUrl);
            } else {
                // 返回错误响应
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JSONUtil.toJsonStr(Result.error("GitHub登录失败: " + e.getMessage())));
            }
        }
    }

    /**
     * 校验并清洗前端回调地址，防止开放重定向漏洞
     * 
     * @param frontendRedirectUri 原始前端回调地址
     * @return 安全的回调地址；如果不合法则返回 null
     */
    private String sanitizeFrontendRedirectUri(String frontendRedirectUri) {
        if (StrUtil.isBlank(frontendRedirectUri)) {
            return null;
        }

        try {
            URI uri = new URI(frontendRedirectUri);

            // 处理相对路径
            if (!uri.isAbsolute()) {
                if (frontendRedirectConfig.isAllowRelativePaths()) {
                    String path = uri.getPath();
                    // 仅允许以单个 "/" 开头的相对路径，拒绝协议相对（"//"）或异常路径
                    if (StrUtil.isBlank(path) || !path.startsWith("/") || path.startsWith("//")) {
                        log.warn("相对回调路径不合法: {}", frontendRedirectUri);
                        return null;
                    }
                    // 拒绝包含可疑片段的路径
                    if (frontendRedirectUri.contains("://") || frontendRedirectUri.contains("\\")) {
                        log.warn("相对回调路径包含非法字符: {}", frontendRedirectUri);
                        return null;
                    }
                    // 返回解析后的规范化路径
                    return uri.toString();
                } else {
                    return null;
                }
            }

            // 处理绝对路径，校验协议和域名
            String scheme = uri.getScheme();
            if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
                log.warn("回调地址使用了不允许的协议: {}", frontendRedirectUri);
                return null;
            }

            String host = uri.getHost();
            if (host == null) {
                log.warn("回调地址无主机名: {}", frontendRedirectUri);
                return null;
            }

            // 检查是否在白名单中
            if (frontendRedirectConfig.getAllowedDomains() == null || frontendRedirectConfig.getAllowedDomains().isEmpty()) {
                log.warn("未配置回调地址白名单，拒绝所有绝对路径: {}", frontendRedirectUri);
                return null;
            }

            boolean isAllowed = frontendRedirectConfig.getAllowedDomains().stream()
                    .anyMatch(domain -> domain != null && domain.equalsIgnoreCase(host));

            if (isAllowed) {
                // 返回解析后的规范化 URL，避免直接回传原始输入
                return uri.toString();
            } else {
                log.warn("回调地址域名不在白名单中: {}", frontendRedirectUri);
                return null;
            }

        } catch (URISyntaxException e) {
            log.warn("前端回调地址格式非法: {}", frontendRedirectUri, e);
            return null;
        }
    }
}