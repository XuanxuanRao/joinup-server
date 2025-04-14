package cn.org.joinup.message.domain.po;

import cn.org.joinup.message.enums.MessageType;
import cn.org.joinup.message.enums.PushChannel;
import lombok.Data;

import java.util.Map;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
public class MessageRecord {
    private MessageType messageType;

    private PushChannel channel;

    private String templateCode;

    private Map<String, Object> params;

    private String content;

    private Long receiverId;
}
