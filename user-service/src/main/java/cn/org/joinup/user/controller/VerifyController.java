package cn.org.joinup.user.controller;

import cn.org.joinup.common.ratelimit.annotation.RateLimit;
import cn.org.joinup.common.ratelimit.enums.LimitType;
import cn.org.joinup.user.service.ICaptchaService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.user.domain.dto.SendCodeDTO;
import cn.org.joinup.user.service.IVerifyService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@Slf4j
@RequestMapping("/verify")
@RequiredArgsConstructor
public class VerifyController {

    private final IVerifyService verifyService;
    private final ICaptchaService captchaService;

    @PostMapping
    public Result<Void> sendVerifyCode(@Validated @RequestBody SendCodeDTO sendCodeDTO) {
        try {
            switch (sendCodeDTO.getType()) {
                case REGISTER:
                    return verifyService.sendVerifyCodeForRegister(sendCodeDTO.getEmail());
                case RESET_PASSWORD:
                    return verifyService.sendVerifyCodeForReset(sendCodeDTO.getEmail());
                case IDENTITY:
                    return verifyService.sendVerifyCodeForIdentity(sendCodeDTO.getEmail());
                default:
                    return Result.error("参数错误");
            }
        } catch (Exception e) {
            log.error("发送验证码失败", e);
            return Result.error("发送验证码失败");
        }
    }

    @ApiOperation("获取验证码")
    @GetMapping("/captcha")
    @RateLimit(limitType = LimitType.IP, count = 10, time = 30)
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) {
        captchaService.generateCaptcha(request, response);
    }

}
