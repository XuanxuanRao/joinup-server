package cn.org.joinup.message.sender;

import cn.org.joinup.message.domain.po.MessageRecord;
import cn.org.joinup.message.enums.PushChannel;


public interface MessageChannelSender<T extends MessageContext> {
    /**
     * 指定消息发送方式
     * @return 消息发送方式
     */
    PushChannel getChannel();

    /**
     * 完成消息发送给用户的过程
     * @param messageRecord 消息记录
     */
    void send(T messageContext);
}
