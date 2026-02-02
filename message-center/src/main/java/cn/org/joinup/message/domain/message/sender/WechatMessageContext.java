package cn.org.joinup.message.domain.message.sender;

import cn.org.joinup.message.infrastructure.enums.PushChannel;
import lombok.Data;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
public class WechatMessageContext implements MessageContext {
    @Override
    public PushChannel getChannel() {
        return PushChannel.WECHAT;
    }
}
