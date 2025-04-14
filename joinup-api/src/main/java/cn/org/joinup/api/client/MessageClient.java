package cn.org.joinup.api.client;

import cn.org.joinup.api.dto.SendEmailMessageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "message-center")
public interface MessageClient {
    @PostMapping("/message/email/send")
    void sendEmail(@RequestBody SendEmailMessageDTO sendEmailMessageDTO);

}
