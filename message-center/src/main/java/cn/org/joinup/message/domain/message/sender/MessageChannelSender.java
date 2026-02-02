package cn.org.joinup.message.domain.message.sender;

import cn.org.joinup.message.infrastructure.enums.PushChannel;


public interface MessageChannelSender<T extends MessageContext> {
    /**
     * 指定消息发送方式
     * @return 消息发送方式
     */
    PushChannel getChannel();

    /**
     * 完成消息发送给用户的过程
     * @param messageContext 消息上下文，包含了消息的内容和接收者的信息
     */
    void send(T messageContext);
}
