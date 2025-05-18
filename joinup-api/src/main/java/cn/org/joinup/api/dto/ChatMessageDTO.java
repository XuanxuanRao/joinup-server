package cn.org.joinup.api.dto;

import cn.org.joinup.common.enums.ChatMessageType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
public class ChatMessageDTO {
    /**
     * 发送用户ID
     */
    private Long senderId;
    /**
     * 消息所属会话ID
     */
    private String conversationId;
    private Map<String, Object> content;
    /**
     * 消息类型：文本，图片，文件，队伍分享
     */
    private ChatMessageType type;
    /**
     * 时间戳
     */
    private LocalDateTime createTime;
}
