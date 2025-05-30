package cn.org.joinup.websocket.listener;

import cn.org.joinup.api.dto.ChatMessageVO;
import cn.org.joinup.websocket.websocket.ChatWebSocketServer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author chenxuanrao06@gmail.com
 */
@Component
@RequiredArgsConstructor
public class SendMessageListener {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "message.send.queue", durable = "true"),
            exchange = @Exchange(value = "chat.send.exchange"),
            key = {"message.send"}
    ))
    public void handleSendMessage(ChatMessageVO chatMessageVO) {
        if (chatMessageVO.getReceiverId() == null) {
            return;
        }
        ChatWebSocketServer.sendMessageToUser(chatMessageVO.getReceiverId(), chatMessageVO);
    }

}
