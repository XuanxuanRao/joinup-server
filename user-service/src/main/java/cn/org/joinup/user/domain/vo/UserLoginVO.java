package cn.org.joinup.user.domain.vo;

import cn.org.joinup.common.enums.Gender;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "用户登录成成功返回信息")
public class UserLoginVO {
    @ApiModelProperty(value = "用于身份验证的token")
    private String token;
    @ApiModelProperty(value = "用户名")
    private String username;
    @ApiModelProperty(value = "用户头像")
    private String avatar;
    @ApiModelProperty(value = "用户性别")
    private Gender gender;
}
