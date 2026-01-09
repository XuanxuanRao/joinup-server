package cn.org.joinup.gateway.util;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import cn.org.joinup.common.constant.SystemConstant;
import cn.org.joinup.common.domain.JwtPayload;
import cn.org.joinup.common.exception.UnauthorizedException;
import org.springframework.stereotype.Component;

import java.security.KeyPair;

@Component
public class JwtTool {
    private final JWTSigner jwtSigner;

    public JwtTool(KeyPair keyPair) {
        this.jwtSigner = JWTSignerUtil.createSigner("rs256", keyPair);
    }

    /**
     * 解析token
     *
     * @param token token
     * @return 解析刷新token得到的用户信息，包含用户id和权限
     */
    public JwtPayload parseToken(String token) {
        // 1.校验token是否为空
        if (token == null) {
            throw new UnauthorizedException("未登录");
        }
        // 2.校验并解析jwt
        JWT jwt;
        try {
            jwt = JWT.of(token).setSigner(jwtSigner);
        } catch (Exception e) {
            throw new UnauthorizedException("无效的token", e);
        }
        // 2.校验jwt是否有效
        if (!jwt.verify()) {
            // 验证失败
            throw new UnauthorizedException("无效的token");
        }
        // 3.校验是否过期
        try {
            JWTValidator.of(jwt).validateDate();
        } catch (ValidateException e) {
            throw new UnauthorizedException("token已经过期");
        }

        // 4.数据格式校验
        Object userPayload = jwt.getPayload(SystemConstant.USER_ID_PAYLOAD_NAME);
        Object rolePayload = jwt.getPayload(SystemConstant.USER_ROLE_PAYLOAD_NAME);
        Object appKeyPayload = jwt.getPayload(SystemConstant.APP_KEY_PAYLOAD_NAME);
        Object userTypePayload = jwt.getPayload(SystemConstant.USER_TYPE_PAYLOAD_NAME);
        if (userPayload == null || rolePayload == null || userTypePayload == null) {
            // 数据为空
            throw new UnauthorizedException("无效的token");
        }

        // 5.数据解析
        try {
            Long userId = Long.valueOf(userPayload.toString());
            String role = rolePayload.toString();
            String appKey = appKeyPayload == null ? "" : appKeyPayload.toString();
            String userType = userTypePayload.toString();
            return new JwtPayload(userId, role, appKey, userType);
        } catch (RuntimeException e) {
            // 数据格式有误
            throw new UnauthorizedException("无效的token");
        }
    }
}