package cn.org.joinup.user.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.org.joinup.user.constant.RedisConstant;
import cn.org.joinup.user.service.ICaptchaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现
 * @author chenxuanrao06@gmail.com
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CaptchaServiceImpl implements ICaptchaService {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void generateCaptcha(HttpServletRequest request, HttpServletResponse response) {
        // 生成验证码图片
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(120, 40, 4, 10);
        String code = captcha.getCode();

        // 生成唯一key
        String verifyKey = UUID.randomUUID().toString().replaceAll("-", "");
        String redisKey = RedisConstant.CAPTCHA_KEY_PREFIX + verifyKey;

        // 存储验证码到Redis
        stringRedisTemplate.opsForValue().set(redisKey, code, RedisConstant.CAPTCHA_TTL, TimeUnit.SECONDS);

        // 在响应头中设置verifyKey
        response.setHeader("Access-Control-Expose-Headers", "X-Captcha-Key");
        response.setHeader("X-Captcha-Key", verifyKey);

        // 直接将图片写入响应流
        try {
            // 设置响应头
            response.setContentType("image/png");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);

            // 写入图片
            captcha.write(response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            log.error("Failed to generate captcha image", e);
            try {
                if (!response.isCommitted()) {
                    response.reset();
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"message\":\"验证码生成失败\"}");
                    response.flushBuffer();
                }
            } catch (IOException ioException) {
                log.error("Failed to write error response for captcha generation failure", ioException);
            }
        }
    }

    @Override
    public boolean validateCaptcha(String verifyKey, String verifyCode) {
        if (verifyKey == null || verifyCode == null) {
            return false;
        }

        String redisKey = RedisConstant.CAPTCHA_KEY_PREFIX + verifyKey;

        // 先获取验证码
        String storedCode = stringRedisTemplate.opsForValue().get(redisKey);
        
        // 如果验证码不存在，返回false
        if (storedCode == null) {
            return false;
        }
        
        // 验证成功后删除验证码
        stringRedisTemplate.delete(redisKey);
        
        // 比较验证码
        return storedCode.equalsIgnoreCase(verifyCode);
    }

}
