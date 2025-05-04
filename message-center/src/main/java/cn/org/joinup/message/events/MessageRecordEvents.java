package cn.org.joinup.message.events;

import cn.org.joinup.message.domain.po.MessageRecord;
import cn.org.joinup.message.sender.MessageContext;
import lombok.Value;

public interface MessageRecordEvents {
    @Value
    class MessageRecordCreateEvent {
        MessageRecord messageRecord;
        MessageContext messageContext;
    }
}
