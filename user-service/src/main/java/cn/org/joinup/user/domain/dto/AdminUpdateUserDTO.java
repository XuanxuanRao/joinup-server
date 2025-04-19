package cn.org.joinup.user.domain.dto;

import cn.org.joinup.common.enums.Gender;
import cn.org.joinup.common.util.RegexUtil;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Pattern;

public class AdminUpdateUserDTO {
    @ApiModelProperty("新的用户名")
    @Pattern(regexp = RegexUtil.USERNAME_REGEX, message = "用户名格式错误")
    private String username;
    @ApiModelProperty("新的密码")
    @Pattern(regexp = RegexUtil.PASSWORD_REGEX, message = "密码格式错误")
    private String password;
    @ApiModelProperty("新的性别，可选值：男，女，未知")
    private Gender gender;
    @ApiModelProperty("新的邮箱")
    @Pattern(regexp = RegexUtil.EMAIL_REGEX, message = "邮箱格式错误")
    private String email;
    @ApiModelProperty("新的认证信息")
    private Boolean verified;
    @ApiModelProperty("新的学号")
    private String studentId;


}
