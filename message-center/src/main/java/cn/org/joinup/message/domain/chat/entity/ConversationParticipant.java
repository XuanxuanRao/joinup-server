package cn.org.joinup.message.domain.chat.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("conversation_participants")
public class ConversationParticipant implements Serializable {
    private static final long serialVersionUID = 1L;

    private String conversationId;
    private Long userId;
    private LocalDateTime createTime;
}