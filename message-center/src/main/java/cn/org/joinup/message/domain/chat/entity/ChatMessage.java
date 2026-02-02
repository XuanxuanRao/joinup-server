package cn.org.joinup.message.domain.chat.entity;

import cn.org.joinup.common.enums.ChatMessageType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
@TableName(value = "chat_messages", autoResultMap = true)
public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属会话ID
     */
    private String conversationId;

    private ChatMessageType type;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> content;

    private Long senderId;

    private LocalDateTime createTime;
}