package cn.org.joinup.api.client.fallback;

import cn.org.joinup.api.client.EmailClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * @author chenxuanrao06@gmail.com
 * @Description:
 */
@Slf4j
public class EmailFallBackFactory implements FallbackFactory<EmailClient> {
    @Override
    public EmailClient create(Throwable cause) {
        return sendEmailDTO -> log.error("send email failed, reason was: {}", cause.getMessage());
    }
}
