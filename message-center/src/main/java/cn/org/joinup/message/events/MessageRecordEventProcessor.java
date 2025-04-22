package cn.org.joinup.message.events;

import cn.org.joinup.message.config.VerifyProperties;
import cn.org.joinup.message.domain.po.MessageRecord;
import cn.org.joinup.message.domain.po.VerifyLog;
import cn.org.joinup.message.enums.MessageType;
import cn.org.joinup.message.enums.PushChannel;
import cn.org.joinup.message.sender.EmailMessageContext;
import cn.org.joinup.message.sender.MessageChannelSender;
import cn.org.joinup.message.sender.MessageContext;
import cn.org.joinup.message.service.IVerifyLogService;
import cn.org.joinup.message.service.checker.CheckerContext;
import cn.org.joinup.message.service.checker.SendIntervalChecker;
import cn.org.joinup.message.service.checker.SendMaxTimesChecker;
import net.bytebuddy.matcher.ElementMatchers;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author chenxuanrao06@gmail.com
 */
@Component
public class MessageRecordEventProcessor {
    private final Map<PushChannel, MessageChannelSender<? extends MessageContext>> senders;
    @Resource
    private VerifyProperties verifyProperties;

    @Resource
    private IVerifyLogService verifyLogService;

    public MessageRecordEventProcessor(List<MessageChannelSender<? extends MessageContext>> senderList) {
        this.senders = senderList.stream()
                .collect(Collectors.toMap(
                    MessageChannelSender::getChannel,
                    Function.identity()
                ));
    }

    /**
     * 处理消息记录创建事件，进行消息发送。
     * <p>首先根据{@link cn.org.joinup.message.enums.MessageType}进行消息发送的前置处理，
     * 然后根据消息记录的{@link cn.org.joinup.message.enums.PushChannel}把消息发送出去，
     * 最后根据{@link cn.org.joinup.message.enums.MessageType}进行发送完成后的处理。
     * @param event
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
            System.err.println("No sender found for channel: " + messageRecord.getChannel());
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
            System.err.println("Type mismatch error during message sending for channel " + sender.getChannel() +
                    ". Sender expected context compatible with " + sender.getClass().getName() +
                    " but got " + context.getClass().getName() + ". Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error sending message via channel " + sender.getChannel() + ": " + e.getMessage());
        }
    }

    private <T extends MessageContext> boolean preHandler(MessageType type, MessageContext messageContext) {
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
