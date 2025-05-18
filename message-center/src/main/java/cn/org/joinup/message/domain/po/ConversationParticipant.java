package cn.org.joinup.message.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
@TableName("conversation_participants")
public class ConversationParticipant implements Serializable {
    private static final long serialVersionUID = 1L;

    private String conversationId;
    private Long userId;
    private LocalDateTime createTime;
}