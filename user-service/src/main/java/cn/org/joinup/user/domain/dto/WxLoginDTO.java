package cn.org.joinup.user.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(description = "微信登录表单")
public class WxLoginDTO {
    @ApiModelProperty(value = "微信授权码", required = true)
    @NotBlank(message = "授权码不能为空")
    private String code;
} 