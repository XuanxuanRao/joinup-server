package cn.org.joinup.message.sender;

import cn.org.joinup.message.enums.PushChannel;

public interface MessageContext {
    PushChannel getChannel();
}
