package cn.org.joinup.user.domain.dto;

import cn.org.joinup.common.util.RegexUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import cn.org.joinup.user.enums.SendCodeType;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.NotNull;

/**
 * 请求发送验证码表单
 * @author chenxuanrao06@gmail.com
 */
@Data
@ApiModel(description = "请求发送验证码表单")
public class SendCodeDTO {
    @ApiModelProperty(value = "邮箱", required = true)
    @Pattern(regexp = RegexUtil.EMAIL_REGEX, message = "邮箱格式错误")
    private String email;
    @ApiModelProperty(value = "验证码类型，1表示注册验证码，2表示登录验证码，3表示重置密码验证码", required = true)
    @NotNull(message = "验证码类型不能为空")
    private SendCodeType type;
}
