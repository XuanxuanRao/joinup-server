package cn.org.joinup.message.domain.message.model;

import cn.org.joinup.message.infrastructure.enums.NotifyType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chenxuanrao06@gmail.com
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SiteSendModel extends SendMessageModel {
    private String title;
    private Long receiverUserId;
    private Long senderUserId;
    private NotifyType notifyType;
}
