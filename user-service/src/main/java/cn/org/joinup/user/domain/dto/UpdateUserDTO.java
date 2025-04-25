package cn.org.joinup.user.domain.dto;

import cn.org.joinup.common.enums.Gender;
import cn.org.joinup.common.util.RegexUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * UpdateUserDTO is a Data Transfer Object for updating user information.
 * @author chenxuanrao06@gmail.com
 */
@ApiModel("用户更新信息表单")
@Data
public class UpdateUserDTO {
    @ApiModelProperty("新的用户名")
    @Pattern(regexp = RegexUtil.USERNAME_REGEX, message = "用户名格式错误")
    private String username;
    @ApiModelProperty("新的头像")
    private String avatar;
    @ApiModelProperty("新的性别，可选值：男，女，未知")
    private Gender gender;
    @ApiModelProperty("sso")
    private String ssoPassword;
}
