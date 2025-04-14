package cn.org.joinup.message.sender;

import cn.org.joinup.message.enums.PushChannel;
import org.springframework.stereotype.Component;

/**
 * @author chenxuanrao06@gmail.com
 */
@Component
public class WechatMessageSender implements MessageChannelSender<WechatMessageContext> {
    @Override
    public PushChannel getChannel() {
        return PushChannel.WECHAT;
    }

    @Override
    public void send(WechatMessageContext messageContext) {
        System.out.println("send wechat message: " + messageContext);
    }
}
