package cn.org.joinup.user.domain.dto.request;

import cn.org.joinup.user.enums.UserType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @author chenxuanrao06@gmail.com
 */
@Data
@ApiModel("管理员获取用户列表")
public class QueryUserInfoDTO {
    private Integer pageNum = 1;

    private Integer pageSize = 10;

    @ApiModelProperty("模糊匹配用户名")
    private String username;

    @ApiModelProperty("模糊匹配邮箱")
    private String email;

    @ApiModelProperty("所属 APP")
    private String appKey;

    @ApiModelProperty("是否已认证")
    private Boolean verified;

    @ApiModelProperty("用户类型")
    private UserType userType;
}
