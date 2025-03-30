package cn.org.joinup.user.controller;

import lombok.extern.slf4j.Slf4j;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.user.domain.dto.SendCodeDTO;
import cn.org.joinup.user.service.IVerifyService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author chenxuanrao06@gmail.com
 * @Description:
 */
@RestController
@Slf4j
@RequestMapping("/verify")
public class VerifyController {

    @Resource
    private IVerifyService verifyService;

    @PostMapping
    public Result<Void> sendVerifyCode(@RequestBody SendCodeDTO sendCodeDTO) {
        switch (sendCodeDTO.getType()) {
            case REGISTER:
                return verifyService.sendVerifyCodeForRegister(sendCodeDTO.getEmail());
            case RESET_PASSWORD:
                return verifyService.sendVerifyCodeForReset(sendCodeDTO.getEmail());
            default:
                return Result.error("参数错误");
        }
    }

}
