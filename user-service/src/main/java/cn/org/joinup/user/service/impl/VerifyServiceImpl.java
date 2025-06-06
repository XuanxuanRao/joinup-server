package cn.org.joinup.user.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.org.joinup.api.client.MessageClient;
import cn.org.joinup.api.dto.SendEmailMessageDTO;
import cn.org.joinup.api.enums.MessageType;
import cn.org.joinup.common.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import cn.org.joinup.common.constant.RedisConstant;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.user.domain.po.User;
import cn.org.joinup.user.service.IUserService;
import cn.org.joinup.user.service.IVerifyService;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class VerifyServiceImpl implements IVerifyService {

    private final StringRedisTemplate stringRedisTemplate;

    private final RedissonClient redissonClient;

    private final IUserService userService;

    private final MessageClient messageClient;

    @Override
    public Result<Void> sendVerifyCodeForRegister(final String email) {
        return Result.error("该功能已下线");
//        // 正则
//        if (!RegexUtil.isEmailValid(email)) {
//            return Result.error("非法的邮箱地址");
//        }
//
//        User user = userService.lambdaQuery().eq(User::getEmail, email).one();
//        if (user != null) {
//            return Result.error("该邮箱已被注册");
//        }
//
//        // 生成验证码
//        String code = generateCode(RedisConstant.VERIFY_CODE_PREFIX + email);
//
//        // 生成邮件内容
//        final String body = loadTemplate("templates/email_register.html")
//                .replace("{{verification_code}}", code);
//
//        // 发送邮件
//        emailClient.sendEmail(SendEmailDTO.builder()
//                    .to(email)
//                    .subject("注册验证码")
//                    .body(body)
//                    .build());
//
//        return Result.success();
    }

    @Override
    public Result<Void> sendVerifyCodeForReset(final String email) {

        return Result.error("该功能已下线");

//        User user = userService.lambdaQuery().eq(User::getEmail, email).one();
//        if (user == null) {
//            return Result.error("该邮箱未注册");
//        }
//
//        String code = generateCode(RedisConstant.VERIFY_CODE_PREFIX + email);
//
//        // 生成邮件内容
//        final String body = loadTemplate("templates/email_reset.html")
//                .replace("{{verification_code}}", code)
//                .replace("{{username}}", user.getUsername());

        // 发送邮件
//        emailClient.sendEmail(SendEmailDTO.builder()
//                .to(email)
//                .subject("重置密码验证码")
//                .body(body)
//                .build());

        // return Result.success();
    }

    @Override
    public Result<Void> sendVerifyCodeForIdentity(String email) {
        User user = userService.getById(UserContext.getUser());
        if (user == null) {
            return Result.error("用户不存在");
        }

        RLock lock = redissonClient.getLock(RedisConstant.VERIFY_LOCK_PREFIX + email);

        boolean locked = false;

        try {
            locked = lock.tryLock(5, TimeUnit.SECONDS);
            if (!locked) {
                return Result.error("请求过于频繁，请稍后重试");
            }

            // 生成验证码并缓存到 Redis
            String code = generateCode(RedisConstant.VERIFY_CODE_PREFIX + email);

            // 发送验证码
            messageClient.sendEmail(SendEmailMessageDTO.builder()
                    .messageType(MessageType.VERIFY)
                    .templateCode("email-buaa")
                    .params(new HashMap<>() {{
                        put("verification_code", code);
                        put("username", user.getUsername());
                    }})
                    .email(email)
                    .build());

            return Result.success();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Result.error("验证码发送失败，请稍后重试");
        } finally {
            if (locked) {
                lock.unlock();
            }
        }
    }

    private String loadTemplate(final String fileName) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            log.error("Email template {} not found", fileName);
            throw new RuntimeException("Email template not found");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new RuntimeException("Error reading email template: " + fileName, e);
        }
    }

    /**
     * 生成验证码，并存入redis
     * @param redisKey redis key for storing the code
     * @return 验证码
     */
    @NotNull
    private String generateCode(String redisKey) {
        // 生成验证码
        String code = RandomUtil.randomNumbers(6);
        stringRedisTemplate.opsForValue().set(redisKey, code, RedisConstant.VERIFY_CODE_EXPIRE, TimeUnit.SECONDS);
        return code;
    }

}
