package cn.org.joinup.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
@ApiModel(description = "聊天概要")
public class ConversationDTO {
    @ApiModelProperty(value = "会话ID", required = true)
    private String id;
    @ApiModelProperty(value = "类型", required = true)
    private String type;
    @ApiModelProperty(value = "最近一条消息", required = false)
    private ChatMessageVO lastMessage;
    @ApiModelProperty(value = "聊天的未读消息数", required = true)
    private Integer unreadMessageCount;
    @ApiModelProperty(value = "会话名称", required = true)
    private String name;
    @ApiModelProperty(value = "聊天封面", required = true)
    private String cover;
    @ApiModelProperty(value = "队伍ID", required = false)
    private String teamId;
}