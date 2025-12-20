package cn.org.joinup.user.service;

import cn.hutool.core.util.StrUtil;
import cn.org.joinup.common.exception.BadRequestException;
import cn.org.joinup.user.domain.dto.RegisterThirdPartyUserDTO;
import cn.org.joinup.user.domain.dto.request.ThirdPartyAuthRequestDTO;
import cn.org.joinup.user.domain.po.APPInfo;
import cn.org.joinup.user.domain.po.User;
import cn.org.joinup.user.domain.vo.ThirdPartyAuthResponseVO;
import cn.org.joinup.user.util.JwtTool;
import cn.org.joinup.user.util.SignUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

/**
 *
 * @author chenxuanrao06@gmail.com
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private static final long NONCE_CACHE_EXPIRY = 5 * 60 * 1000; // 5 minutes

    private final IAPPInfoService appInfoService;

    private final IUserService userService;

    private final JwtTool jwtTool;

    public ThirdPartyAuthResponseVO thirdPartyAuth(ThirdPartyAuthRequestDTO authRequestDTO) {
        // 1. 根据 timestamp 防止重放攻击
        if (System.currentTimeMillis() - authRequestDTO.getTimestamp() > NONCE_CACHE_EXPIRY) {
            throw new BadRequestException("请求已过期");
        }

        // 2. 检查 app 状态并获取 app-secret
        APPInfo appInfo = appInfoService.getActiveAPPInfo(authRequestDTO.getAppKey())
                .orElseThrow(() -> new BadRequestException("AppKey不存在或已被禁用"));

        // 3. 根据 app-key 和 app-secret 计算签名，并与请求中的 signature 比较
        if (!StrUtil.equals(SignUtils.generateSignature(buildSignatureParams(authRequestDTO), appInfo.getAppSecret()),
                authRequestDTO.getSignature())) {
             throw new BadRequestException("签名验证失败");
        }

        // 4. 在数据库中根据 app-uuid 和 app-key 找到对应的用户信息
        User user = userService.lambdaQuery()
                .eq(User::getAppKey, authRequestDTO.getAppKey())
                .eq(User::getAppUUID, authRequestDTO.getAppUUID())
                .one();
        if (user == null) {
            log.info("第三方应用首次登录，自动注册用户，appKey={}, appUUID={}",
                    authRequestDTO.getAppKey(), authRequestDTO.getAppUUID());
            user = userService.registerThirdPartyUser(RegisterThirdPartyUserDTO.builder()
                            .appKey(appInfo.getAppKey())
                            .appUUID(authRequestDTO.getAppUUID())
                            .username(String.format("%s_%s", appInfo.getAppKey(), authRequestDTO.getAppUUID().substring(0, 8)))
                            .build());
            log.info("{} 用户注册成功，userId={}", authRequestDTO.getAppKey(), user.getId());
        }

        // 5. 验证通过，生成 token 并返回
        return ThirdPartyAuthResponseVO.builder()
                .token(jwtTool.createToken(user.getId(), user.getRole(), Duration.ofMinutes(appInfo.getTokenExpireSeconds())))
                .expireAt(LocalDateTime.now().plusMinutes(appInfo.getTokenExpireSeconds()))
                .build();
    }

    private Map<String, String> buildSignatureParams(ThirdPartyAuthRequestDTO authRequestDTO) {
        return Map.of(
                "appKey", authRequestDTO.getAppKey(),
                "appUUID", authRequestDTO.getAppUUID(),
                "timestamp", String.valueOf(authRequestDTO.getTimestamp())
        );
    }

}
