package cn.org.joinup.user.domain.dto;

import cn.org.joinup.common.util.RegexUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
@ApiModel(description = "用户注册账号表单")
public class RegisterFormDTO {
    @ApiModelProperty(value = "用户名", required = true)
    @Pattern(regexp = RegexUtil.USERNAME_REGEX, message = "用户名格式错误")
    private String username;
    @ApiModelProperty(value = "邮箱", required = true)
    @Pattern(regexp = RegexUtil.EMAIL_REGEX, message = "邮箱格式错误")
    private String email;
    @ApiModelProperty(value = "密码", required = true)
    @Pattern(regexp = RegexUtil.PASSWORD_REGEX, message = "密码格式错误")
    private String password;
    @ApiModelProperty(value = "验证码", required = true)
    private String verifyCode;
}
