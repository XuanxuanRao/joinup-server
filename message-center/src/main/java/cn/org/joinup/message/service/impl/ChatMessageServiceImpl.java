package cn.org.joinup.message.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.api.client.UserClient;
import cn.org.joinup.api.dto.ChatMessageDTO;
import cn.org.joinup.api.dto.ChatMessageVO;
import cn.org.joinup.api.dto.ConversationVO;
import cn.org.joinup.message.domain.po.ChatMessage;
import cn.org.joinup.message.mapper.ChatMessageMapper;
import cn.org.joinup.message.service.IChatMessageService;
import cn.org.joinup.message.service.IConversationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements IChatMessageService {

    private final RabbitTemplate rabbitTemplate;
    private final UserClient userClient;
    private final IConversationService conversationService;

    @Override
    public void onMessage(ChatMessageDTO chatMessageDTO) {
        // 1. 存储到数据库
        ChatMessage chatMessage = buildChatMessage(chatMessageDTO);
        save(chatMessage);
        // 2. 发送给接收方
        conversationService.getParticipants(chatMessageDTO.getConversationId())
                .forEach(receiverId -> {
                    if (Objects.equals(receiverId, chatMessage.getSenderId())) {
                        return;
                    }
                    ChatMessageVO chatMessageVO = buildChatMessageVO(chatMessage, receiverId);
                    rabbitTemplate.convertAndSend("chat.send.exchange", "message.send", chatMessageVO);
                });
    }

    private ChatMessage buildChatMessage(ChatMessageDTO chatMessageDTO) {
        ChatMessage chatMessage = BeanUtil.copyProperties(chatMessageDTO, ChatMessage.class);
        return chatMessage;
    }

    private ChatMessageVO buildChatMessageVO(ChatMessage chatMessage, Long receiverId) {
        ChatMessageVO chatMessageVO = BeanUtil.copyProperties(chatMessage, ChatMessageVO.class);
        chatMessageVO.setSender(userClient.queryUser(chatMessage.getSenderId()).getData());
        chatMessageVO.setReceiverId(receiverId);
        chatMessageVO.setConversation(BeanUtil.copyProperties(conversationService.getConversationById(chatMessage.getConversationId()), ConversationVO.class));
        return chatMessageVO;
    }


}