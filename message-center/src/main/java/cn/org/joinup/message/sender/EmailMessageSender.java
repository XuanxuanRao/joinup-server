package cn.org.joinup.message.sender;

import cn.org.joinup.message.enums.PushChannel;
import cn.org.joinup.message.service.IEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;


/**
 * @author chenxuanrao06@gmail.com
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailMessageSender implements MessageChannelSender<EmailMessageContext> {
    private final IEmailService emailService;

    @Override
    public PushChannel getChannel() {
        return PushChannel.EMAIL;
    }

    @Override
    public void send(EmailMessageContext messageContext) {
        emailService.sendSimpleEmail(messageContext.getTo(), messageContext.getSubject(), messageContext.getContent());
    }
}
