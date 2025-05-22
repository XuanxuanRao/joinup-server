package cn.org.joinup.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
public class BriefConversationDTO {
    @ApiModelProperty(value = "会话ID", required = true)
    private String id;
    @ApiModelProperty(value = "类型", required = true)
    private String type;
    @ApiModelProperty(value = "会话名称", required = true)
    private String name;
    @ApiModelProperty(value = "聊天封面", required = true)
    private String cover;
}