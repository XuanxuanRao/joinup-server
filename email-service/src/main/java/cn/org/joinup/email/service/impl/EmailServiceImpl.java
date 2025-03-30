package cn.org.joinup.email.service.impl;

import cn.org.joinup.email.domain.po.EmailLog;
import cn.org.joinup.email.mapper.EmailLogMapper;
import cn.org.joinup.email.service.IEmailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author chenxuanrao06@gmail.com
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class EmailServiceImpl extends ServiceImpl<EmailLogMapper, EmailLog> implements IEmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String email;

    @Override
    @Retryable(
            value = MailSendException.class,
            maxAttempts = 2,
            backoff = @Backoff(delay = 500)
    )
    public void sendSimpleEmail(String to, String subject, String body) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // true表示发送HTML邮件
            helper.setFrom(email);
            mailSender.send(message);
            log.info("Sent email to {} successfully", to);
            EmailLog emailLog = EmailLog.builder()
                    .sender(email)
                    .receiver(to)
                    .subject(subject)
                    .createTime(LocalDateTime.now())
                    .build();
            save(emailLog);
        } catch (MailAuthenticationException e) {
            log.error("Email authentication failed: {}", e.getMessage());
            throw new RuntimeException("Authentication failed while sending email");
        } catch (MailSendException e) {
            log.error("Failed to send email to {}. Error: {}", to, e.getMessage());
            throw new RuntimeException("Error occurred while sending email");
        } catch (Exception e) {
            log.error("Unexpected error while sending email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Unexpected error occurred while sending email");
        }
    }

    @Override
    public void sendEmailWithAttachments(String to, String subject, String body, List<File> attachments) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // true表示发送HTML邮件
            helper.setFrom(email);
            attachments.forEach(attachment -> {
                try {
                    helper.addAttachment(attachment.getName(), attachment);
                } catch (Exception e) {
                    log.error("Failed to add attachment: {}", e.getMessage());
                    throw new RuntimeException("Error occurred while adding attachment");
                }
            });
            mailSender.send(message);
            log.info("Sent email to {} successfully", to);
            EmailLog emailLog = EmailLog.builder()
                    .sender(email)
                    .receiver(to)
                    .subject(subject)
                    .createTime(LocalDateTime.now())
                    .build();
            save(emailLog);
        } catch (MailAuthenticationException e) {
            log.error("Email authentication failed: {}", e.getMessage());
            throw new RuntimeException("Authentication failed while sending email");
        } catch (MailSendException e) {
            log.error("Failed to send email to {}. Error: {}", to, e.getMessage());
            throw new RuntimeException("Error occurred while sending email");
        } catch (Exception e) {
            log.error("Unexpected error while sending email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Unexpected error occurred while sending email");
        }
    }
}
