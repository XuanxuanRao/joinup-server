package cn.org.joinup.websocket.domain;

import cn.org.joinup.common.enums.ChatMessageType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @author chenxuanrao06@gmail.com
 */
@ApiModel("客户端发送的聊天消息")
@Data
public class ClientChatMessage {
    @NotNull
    @ApiModelProperty("消息类型，枚举值：TEXT,IMAGE,FILE,TEAM_SHARE")
    private ChatMessageType type;
    @NotNull
    @ApiModelProperty("消息内容，json格式")
    private Map<String, Object> content;
    @NotNull
    @ApiModelProperty("会话ID")
    private String conversationId;
}
