package cn.org.joinup.user.domain.dto;

import cn.org.joinup.common.util.RegexUtil;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 进行北航身份验证表单
 * @author chenxuanrao06@gmail.com
 */
@Data
public class VerifyIdentityDTO {
    @Pattern(regexp = RegexUtil.BUAA_EMAIL_REGEX, message = "北航邮箱格式错误")
    private String email;
    @NotNull(message = "验证码不能为空")
    private String verifyCode;
}
