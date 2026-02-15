package cn.org.joinup.user.service;

import cn.org.joinup.user.domain.vo.ThirdPartyAuthResponseVO;

/**
 * GitHub OAuth服务接口
 */
public interface IGitHubOAuthService {

    /**
     * 获取GitHub授权URL
     * @param state 状态参数，用于防止CSRF攻击
     * @param frontendRedirectUri 前端回调页面URL
     * @return GitHub授权URL
     */
    String getAuthorizationUrl(String state, String frontendRedirectUri);

    /**
     * 处理GitHub OAuth回调
     * @param code GitHub返回的授权码
     * @param state 状态参数，用于验证
     * @return 认证响应，包含token和过期时间
     */
    ThirdPartyAuthResponseVO handleCallback(String code, String state);
}