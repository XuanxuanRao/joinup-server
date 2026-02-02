package cn.org.joinup.message.domain.message.event;

import cn.org.joinup.message.infrastructure.config.VerifyProperties;
import cn.org.joinup.message.domain.message.entity.MessageRecord;
import cn.org.joinup.message.domain.message.entity.VerifyLog;
import cn.org.joinup.message.infrastructure.enums.MessageType;
import cn.org.joinup.message.infrastructure.enums.PushChannel;
import cn.org.joinup.message.domain.message.sender.EmailMessageContext;
import cn.org.joinup.message.domain.message.sender.MessageChannelSender;
import cn.org.joinup.message.domain.message.sender.MessageContext;
import cn.org.joinup.message.application.message.service.IVerifyLogService;
import cn.org.joinup.message.domain.message.checker.CheckerContext;
import cn.org.joinup.message.domain.message.checker.SendIntervalChecker;
import cn.org.joinup.message.domain.message.checker.SendMaxTimesChecker;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.matcher.ElementMatchers;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author chenxuanrao06@gmail.com
 */
@Component
@Slf4j
public class MessageRecordEventProcessor {
    private final Map<PushChannel, MessageChannelSender<? extends MessageContext>> senders;
    private final VerifyProperties verifyProperties;
    private final IVerifyLogService verifyLogService;

    public MessageRecordEventProcessor(List<MessageChannelSender<? extends MessageContext>> senderList,
                                       VerifyProperties verifyProperties,
                                       IVerifyLogService verifyLogService) {
        this.senders = senderList.stream()
                .collect(Collectors.toMap(
                        MessageChannelSender::getChannel,
                        Function.identity()
                ));
        this.verifyProperties = verifyProperties;
        this.verifyLogService = verifyLogService;
    }

    /**
     * 处理消息记录创建事件，进行消息发送。
     * <p>首先根据{@link MessageType}进行消息发送的前置处理，
     * 然后根据消息记录的{@link PushChannel}把消息发送出去，
     * 最后根据{@link MessageType}进行发送完成后的处理。
     * @param event 消息记录创建事件
     */
    @EventListener
    public void onMessageRecordCreateEvent(MessageRecordEvents.MessageRecordCreateEvent event) {
        MessageRecord messageRecord = event.getMessageRecord();

        if (!preHandler(messageRecord.getMessageType(), event.getMessageContext())) {
            return;
        }

        MessageChannelSender<? extends MessageContext> sender = senders.get(messageRecord.getChannel());

        if (sender != null) {
            sendMessageInternal(sender, event.getMessageContext());
        } else {
            log.error("No sender found for channel {}", messageRecord.getChannel());
            return;
        }

        // 发送完成后的处理
        if (messageRecord.getMessageType() == MessageType.VERIFY) {
            VerifyLog log = new VerifyLog();
            log.setAccount(((EmailMessageContext) event.getMessageContext()).getTo());
            log.setCreateTime(LocalDateTime.now());
            log.setChannel(PushChannel.EMAIL);
            verifyLogService.save(log);
        }
    }

    // 私有辅助方法，用于处理类型转换和调用 send
    // 这里需要抑制 unchecked 警告，因为我们将进行类型转换
    @SuppressWarnings("unchecked")
    private <T extends MessageContext> void sendMessageInternal(MessageChannelSender<T> sender, MessageContext context) {
        try {
            sender.send((T) context);
        } catch (ClassCastException e) {
            log.error("Type mismatch error during message sending for channel {}. Sender expected context compatible with {} but got {}",
                    sender.getChannel(), sender.getClass().getName(), context.getClass().getName(), e);
        } catch (Exception e) {
            log.error("Error sending message via channel {}", sender.getChannel(), e);
        }
    }

    private boolean preHandler(MessageType type, MessageContext messageContext) {
        if (type != MessageType.VERIFY) {
            return true;
        }

        String account;

        if (messageContext.getChannel() == PushChannel.EMAIL) {
            EmailMessageContext emailSendModel = (EmailMessageContext) messageContext;
            account = emailSendModel.getTo();
        } else {
            throw new IllegalArgumentException("Unsupported channel: " + messageContext.getChannel());
        }

        return ElementMatchers.any()
                .and(new SendMaxTimesChecker())
                .and(new SendIntervalChecker())
                .matches(new CheckerContext(verifyProperties.getSendInterval(), verifyProperties.getSendMaxTimes(), account));
    }
}
