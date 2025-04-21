package cn.org.joinup.api.dto;

import cn.org.joinup.api.enums.MessageType;
import cn.org.joinup.api.enums.NotifyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendSiteMessageDTO {
    private MessageType messageType;
    private String templateCode;
    private Map<String, Object> params;
    private Long receiverUserId;
    private NotifyType notifyType;
}
