package cn.org.joinup.message.domain.po;

import cn.org.joinup.common.enums.ChatMessageType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
@TableName("chat_messages")
public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属会话ID
     */
    private String conversationId;

    private ChatMessageType type;

    private Map<String, Object> content;

    private Long senderId;

    private LocalDateTime createTime;
}