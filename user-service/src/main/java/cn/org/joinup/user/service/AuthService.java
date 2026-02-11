package cn.org.joinup.user.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.org.joinup.common.exception.BadRequestException;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.user.constant.RedisConstant;
import cn.org.joinup.user.domain.dto.RegisterThirdPartyUserDTO;
import cn.org.joinup.user.domain.dto.request.ThirdPartyAuthRequestDTO;
import cn.org.joinup.user.domain.po.APPInfo;
import cn.org.joinup.user.domain.po.User;
import cn.org.joinup.user.domain.vo.QRCodeVO;
import cn.org.joinup.user.domain.vo.ScanLoginVO;
import cn.org.joinup.user.domain.vo.ThirdPartyAuthResponseVO;
import cn.org.joinup.user.enums.ScanLoginStatus;
import cn.org.joinup.user.enums.UserType;
import cn.org.joinup.user.util.JwtTool;
import cn.org.joinup.user.util.SignUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author chenxuanrao06@gmail.com
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    @Value("${user.auth.qrcode.url-prefix}")
    private String scanLoginUrlPrefix;

    private static final long NONCE_CACHE_EXPIRY = 5 * 60 * 1000; // 5 minutes

    private final IAPPInfoService appInfoService;

    private final IUserService userService;

    private final JwtTool jwtTool;

    private final StringRedisTemplate stringRedisTemplate;

    public ThirdPartyAuthResponseVO thirdPartyAuth(ThirdPartyAuthRequestDTO authRequestDTO) {
        // 1. 根据 timestamp 防止重放攻击
        if (Math.abs(System.currentTimeMillis() - authRequestDTO.getTimestamp()) > NONCE_CACHE_EXPIRY) {
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
        if (user.getUserType() == UserType.INTERNAL) {
            log.warn("内部用户 {} 尝试使用第三方app {} 授权", user.getId(),  authRequestDTO.getAppKey());
            throw new BadRequestException("非法的登录请求");
        }

        // 5. 验证通过，生成 token 并返回
        return ThirdPartyAuthResponseVO.builder()
                .token(jwtTool.createToken(
                        user.getId(),
                        user.getRole(),
                        user.getAppKey(),
                        UserType.EXTERNAL,
                        Duration.ofMinutes(appInfo.getTokenExpireMinutes())))
                .expireAt(LocalDateTime.now().plusMinutes(appInfo.getTokenExpireMinutes()))
                .build();
    }

    /**
     * 移动端扫码登录 web 端
     *
     * @param scanId 服务器生成的唯一扫码ID，移动端扫码后会携带该ID调用此接口进行登录
     */
    public ScanLoginVO scanLogin(String scanId) {
        // 1. 在 redis 中查找 scanId 对应的登录请求
        String key = RedisConstant.SCAN_LOGIN_KEY_PREFIX + scanId;
        String payloadJson = stringRedisTemplate.opsForValue().get(key);
        // 2.1 如果未找到，返回已过期
        if (StrUtil.isBlank(payloadJson)) {
            return ScanLoginVO.builder()
                    .status(ScanLoginStatus.EXPIRED)
                    .build();
        }
        // 2.2 如果找到
        ScanLoginPayload scanLoginPayload = JSONUtil.toBean(payloadJson, ScanLoginPayload.class);
        // 2.2.1 如果状态不为已扫码，直接返回当前状态（等待扫码或等待确认）
        if (scanLoginPayload.getStatus() != ScanLoginStatus.CONFIRM) {
            return ScanLoginVO.builder()
                    .status(scanLoginPayload.getStatus())
                    .build();
        }
        // 2.2.2 如果状态为已扫码，生成 token，返回登录成功的响应，并将状态更新为已登录
        User user = userService.getUserById(scanLoginPayload.getUserId());
        if (user == null) {
            throw new BadRequestException("用户不存在");
        }
        String token = jwtTool.createToken(
                user.getId(),
                user.getRole(),
                user.getAppKey(),
                user.getUserType(),
                Duration.ofMinutes(30));
        return ScanLoginVO.builder()
                .status(ScanLoginStatus.CONFIRM)
                .token(token)
                .expireAt(LocalDateTime.now().plusMinutes(30))
                .build();
    }

    private Map<String, String> buildSignatureParams(ThirdPartyAuthRequestDTO authRequestDTO) {
        return Map.of(
                "appKey", authRequestDTO.getAppKey(),
                "appUUID", authRequestDTO.getAppUUID(),
                "timestamp", String.valueOf(authRequestDTO.getTimestamp())
        );
    }

    public QRCodeVO createQRCode() {
        // 生成随机 ID
        String scanId = IdUtil.simpleUUID();
        // 构建扫码登录请求
        ScanLoginPayload scanLoginPayload = new ScanLoginPayload();
        scanLoginPayload.setStatus(ScanLoginStatus.WAITING);
        // 存储到 Redis 中，过期时间为 5 分钟
        stringRedisTemplate.opsForValue().set(
                RedisConstant.SCAN_LOGIN_KEY_PREFIX + scanId,
                JSONUtil.toJsonStr(scanLoginPayload), RedisConstant.SCAN_LOGIN_TTL, TimeUnit.SECONDS);
        // 返回扫码 ID 给移动端
        return QRCodeVO.builder()
                .scanId(scanId)
                .link(String.format(scanLoginUrlPrefix + "?size=200x200&scanId=%s", scanId))
                .build();
    }

    public void appConfirm(String scanId) {
        // 1. 在 redis 中查找 scanId 对应的登录请求
        String key = RedisConstant.SCAN_LOGIN_KEY_PREFIX + scanId;
        String payloadJson = stringRedisTemplate.opsForValue().get(key);
        // 2.1 如果未找到，返回已过期
        if (StrUtil.isBlank(payloadJson)) {
            throw new BadRequestException("二维码已过期");
        }
        // 2.2 如果找到
        ScanLoginPayload scanLoginPayload = JSONUtil.toBean(payloadJson, ScanLoginPayload.class);
        if (scanLoginPayload.getStatus() != ScanLoginStatus.WAITING) {
            throw new BadRequestException("二维码已使用");
        }
        scanLoginPayload.setUserId(UserContext.getUserId());
        scanLoginPayload.setStatus(ScanLoginStatus.CONFIRM);
        // 重新计算过期时间
        long ttl = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(scanLoginPayload), ttl, TimeUnit.SECONDS);
    }


    @Data
    public static class ScanLoginPayload {
        private ScanLoginStatus status;
        private Long userId;
    }

}
