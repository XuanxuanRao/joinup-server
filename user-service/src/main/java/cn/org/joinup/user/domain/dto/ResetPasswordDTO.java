package cn.org.joinup.user.domain.dto;

import cn.org.joinup.common.util.RegexUtil;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
@ApiModel(description = "用户重置密码表单")
public class ResetPasswordDTO {
    @Pattern(regexp = RegexUtil.EMAIL_REGEX)
    private String email;
    @Pattern(regexp = RegexUtil.PASSWORD_REGEX)
    private String password;
    private String verifyCode;
}
