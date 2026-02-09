package cn.org.joinup.user.domain.dto;

import cn.org.joinup.common.util.RegexUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@ApiModel(description = "用户登录表单")
public class LoginFormDTO {
    @ApiModelProperty(value = "用户名", required = true)
    @Pattern(regexp = RegexUtil.USERNAME_REGEX, message = "用户名格式错误")
    private String username;

    @ApiModelProperty(value = "密码", required = true)
    @Pattern(regexp = RegexUtil.PASSWORD_REGEX, message = "密码格式错误")
    private String password;

    @ApiModelProperty(value = "验证码", required = true)
    @NotBlank
    private String verifyCode;

    @ApiModelProperty(value = "验证码key", required = true)
    @NotBlank
    private String verifyKey;
}
