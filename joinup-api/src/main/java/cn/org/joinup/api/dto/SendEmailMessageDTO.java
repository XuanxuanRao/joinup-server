package cn.org.joinup.api.dto;

import cn.org.joinup.api.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendEmailMessageDTO {
    private MessageType messageType;
    private String templateCode;
    private Map<String, Object> params;
    private String email;
}
