package cn.org.joinup.message.domain.message.event;

import cn.org.joinup.message.domain.message.entity.MessageRecord;
import cn.org.joinup.message.domain.message.sender.MessageContext;
import lombok.Value;

public interface MessageRecordEvents {
    @Value
    class MessageRecordCreateEvent {
        MessageRecord messageRecord;
        MessageContext messageContext;
    }
}
