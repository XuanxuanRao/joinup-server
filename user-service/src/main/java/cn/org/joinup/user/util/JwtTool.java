package cn.org.joinup.user.util;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import cn.org.joinup.common.constant.SystemConstant;
import cn.org.joinup.user.enums.UserType;
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
     * @param role   用户角色
     * @param appKey 用户所属应用key
     * @param userType 用户类型
     * @param ttl    token有效期
     * @return access-token
     */
    public String createToken(Long userId, String role, String appKey, UserType userType, Duration ttl) {
        // 1.生成jws
        return JWT.create()
                .setPayload(SystemConstant.USER_ID_PAYLOAD_NAME, userId)
                .setPayload(SystemConstant.USER_ROLE_PAYLOAD_NAME, role)
                .setPayload(SystemConstant.APP_KEY_PAYLOAD_NAME, appKey == null ? "" : appKey)
                .setPayload(SystemConstant.USER_TYPE_PAYLOAD_NAME, userType.getDesc())
                .setExpiresAt(new Date(System.currentTimeMillis() + ttl.toMillis()))
                .setSigner(jwtSigner)
                .sign();
    }
}