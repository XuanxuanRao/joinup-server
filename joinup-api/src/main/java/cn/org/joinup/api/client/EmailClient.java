package cn.org.joinup.api.client;

import cn.org.joinup.api.client.fallback.EmailFallBackFactory;
import cn.org.joinup.api.dto.SendEmailDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author chenxuanrao06@gmail.com
 * @Description:
 */
@FeignClient(name = "email-service", fallbackFactory = EmailFallBackFactory.class)
public interface EmailClient {
    @PostMapping("/common/email/send")
    void sendEmail(@RequestBody SendEmailDTO sendEmailDTO);

}
