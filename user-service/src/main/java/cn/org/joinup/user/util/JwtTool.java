package cn.org.joinup.user.util;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import cn.org.joinup.common.constant.SystemConstant;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtTool {
    private final JWTSigner jwtSigner;

    public JwtTool(KeyPair keyPair) {
        this.jwtSigner = JWTSignerUtil.createSigner("rs256", keyPair);
    }

    /**
     * 创建 access-token
     *
     * @param userId 用户id
     * @param ttl    token有效期
     * @return access-token
     */
    public String createToken(Long userId, String role, String appKey, Duration ttl) {
        // 1.生成jws
        return JWT.create()
                .setPayload(SystemConstant.USER_ID_PAYLOAD_NAME, userId)
                .setPayload(SystemConstant.USER_ROLE_PAYLOAD_NAME, role)
                .setPayload(SystemConstant.APP_KEY_PAYLOAD_NAME, appKey ==  null ? "" : appKey)
                .setExpiresAt(new Date(System.currentTimeMillis() + ttl.toMillis()))
                .setSigner(jwtSigner)
                .sign();
    }
}