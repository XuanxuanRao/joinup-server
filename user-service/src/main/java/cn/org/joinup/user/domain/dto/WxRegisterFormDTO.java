package cn.org.joinup.user.domain.dto;

import cn.org.joinup.common.enums.Gender;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author chenxuanrao06@gmail.com
 */
@ApiModel("微信注册表单")
@Data
public class WxRegisterFormDTO {
    @ApiModelProperty(value = "微信授权码", required = true)
    @NotBlank(message = "授权码不能为空")
    private String code;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("头像url")
    private String avatar;

    @ApiModelProperty("性别")
    private Gender gender;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("绑定邮箱验证码")
    private String emailVerifyCode;

    @ApiModelProperty("邀请码")
    private String inviteCode;
}