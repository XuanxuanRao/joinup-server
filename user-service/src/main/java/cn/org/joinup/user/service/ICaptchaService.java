package cn.org.joinup.user.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 验证码服务接口
 * @author chenxuanrao06@gmail.com
 */
public interface ICaptchaService {

    /**
     * 生成验证码并直接写入响应流，在响应头中返回verifyKey
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    void generateCaptcha(HttpServletRequest request, HttpServletResponse response);

    /**
     * 验证验证码
     * @param verifyKey 验证码key
     * @param verifyCode 验证码
     * @return 是否验证成功
     */
    boolean validateCaptcha(String verifyKey, String verifyCode);

}
