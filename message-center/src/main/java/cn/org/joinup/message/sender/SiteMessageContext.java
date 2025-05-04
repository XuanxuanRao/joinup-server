package cn.org.joinup.message.sender;

import cn.org.joinup.message.enums.NotifyType;
import cn.org.joinup.message.enums.PushChannel;
import lombok.Data;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
public class SiteMessageContext implements MessageContext {
    private String title;
    private String content;
    private NotifyType notifyType;
    private Long senderUserId;
    private Long receiverUserId;

    @Override
    public PushChannel getChannel() {
        return PushChannel.SITE;
    }
}
