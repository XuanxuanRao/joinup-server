package cn.org.joinup.api.dto;

import cn.org.joinup.common.enums.Gender;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
@ApiModel(description = "用户信息")
public class UserDTO {
    @ApiModelProperty("用户ID")
    private Long id;
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("用户头像")
    private String avatar;
    @ApiModelProperty("账号创建时间")
    private String createTime;
    @ApiModelProperty("性别")
    private Gender gender;
    @ApiModelProperty("学号")
    private String studentId;
    @ApiModelProperty("是否完成了身份认证")
    private Boolean verified;
    @ApiModelProperty("加入队伍的数量")
    private Integer joinedTeamCount;
    @ApiModelProperty("创建队伍的数量")
    private Integer createdTeamCount;
}
