package cn.org.joinup.user.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

import cn.org.joinup.common.result.Result;
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

        try {
            // 处理GitHub回调，获取认证响应（使用原始state值）
            ThirdPartyAuthResponseVO authResponse = gitHubOAuthService.handleCallback(code, originalState);
            log.info("GitHub登录成功");

            if (StrUtil.isNotBlank(frontendRedirectUri)) {
                // 重定向到前端回调页面，携带token参数
                String redirectUrl = frontendRedirectUri + "?token=" + authResponse.getToken() +
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
            if (StrUtil.isNotBlank(frontendRedirectUri)) {
                // 重定向到前端回调页面，携带错误信息
                String errorMsg = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
                String redirectUrl = frontendRedirectUri + "?error=" + errorMsg;
                response.sendRedirect(redirectUrl);
            } else {
                // 返回错误响应
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JSONUtil.toJsonStr(Result.error("GitHub登录失败: " + e.getMessage())));
            }
        }
    }
}