package cn.org.joinup.message.interfaces.mq;

import cn.org.joinup.api.dto.ChatMessageDTO;
import cn.org.joinup.message.application.chat.service.IChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author chenxuanrao06@gmail.com
 */
@Component
@RequiredArgsConstructor
public class ReceiveChatMessageListener {

    private final IChatMessageService chatMessageService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "chat.receive.queue", durable = "true"),
            exchange = @org.springframework.amqp.rabbit.annotation.Exchange(value = "chat.message.direct"),
            key = {"onMessage"}
    ))
    public void onMessage(ChatMessageDTO chatMessageDTO) {
        chatMessageService.onMessage(chatMessageDTO);
    }

}
