package cn.org.joinup.message.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.org.joinup.api.client.UserClient;
import cn.org.joinup.api.dto.BriefConversationDTO;
import cn.org.joinup.api.dto.ChatMessageDTO;
import cn.org.joinup.api.dto.ChatMessageVO;
import cn.org.joinup.common.enums.ChatMessageType;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.message.domain.dto.MessageFilterDTO;
import cn.org.joinup.message.domain.po.ChatMessage;
import cn.org.joinup.message.mapper.ChatMessageMapper;
import cn.org.joinup.message.service.IChatMessageService;
import cn.org.joinup.message.service.IConversationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements IChatMessageService {

    private final RabbitTemplate rabbitTemplate;
    private final UserClient userClient;
    private final IConversationService conversationService;

    @Override
    public void onMessage(ChatMessageDTO chatMessageDTO) {
        // 1. 校验权限
        if (!conversationService.in(chatMessageDTO.getConversationId(), chatMessageDTO.getSenderId())) {
            log.warn("Detect invalid behaviour from user {}: " +
                    "Try to send message in conversation {}", chatMessageDTO.getSenderId(), chatMessageDTO.getConversationId());
            return;
        }
        // 2. 存储到数据库
        ChatMessage chatMessage = buildChatMessage(chatMessageDTO);
        save(chatMessage);
        // 3. 发送给接收方
        conversationService.getParticipants(chatMessageDTO.getConversationId())
                .forEach(receiverId -> {
                    if (Objects.equals(receiverId, chatMessage.getSenderId())) {
                        return;
                    }
                    ChatMessageVO chatMessageVO = buildChatMessageVO(chatMessage, receiverId, true);
                    rabbitTemplate.convertAndSend("chat.send.exchange", "message.send", chatMessageVO);
                });
        // 4. 维护缓存
        conversationService.updateConversationOnMessage(chatMessageDTO.getConversationId(), chatMessage);
    }

    @Override
    public ChatMessageVO convertToVO(ChatMessage chatMessage, boolean needConversation) {
        return buildChatMessageVO(chatMessage, null, needConversation);
    }

    @Override
    public ChatMessageVO convertToVO(ChatMessage chatMessage, Long receiverId, boolean needConversation) {
        return buildChatMessageVO(chatMessage, receiverId, needConversation);
    }

    private ChatMessage buildChatMessage(ChatMessageDTO chatMessageDTO) {
        return BeanUtil.copyProperties(chatMessageDTO, ChatMessage.class);
    }

    private ChatMessageVO buildChatMessageVO(ChatMessage chatMessage, Long receiverId, boolean needConversation) {
        ChatMessageVO chatMessageVO = BeanUtil.copyProperties(chatMessage, ChatMessageVO.class);
        chatMessageVO.setSender(userClient.queryUser(chatMessage.getSenderId()).getData());
        chatMessageVO.setReceiverId(receiverId);
        if (needConversation) {
            chatMessageVO.setConversation(conversationService.getBriefConversation(chatMessage.getConversationId(), receiverId));
        }
        return chatMessageVO;
    }

    @Override
    public ChatMessageVO getChatMessageVO(Long id) {
        return getChatMessageVO(id, true);
    }

    @Override
    public PageResult<ChatMessageVO> queryConversationMessage(String conversationId, MessageFilterDTO messageFilterDTO) {
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessage::getConversationId, conversationId);
        if(messageFilterDTO.getSenderId() != null){
            queryWrapper.eq(ChatMessage::getSenderId, messageFilterDTO.getSenderId());
        }
        if(messageFilterDTO.getMessageType() != null){
            queryWrapper.eq(ChatMessage::getType, messageFilterDTO.getMessageType());
        }
        if(messageFilterDTO.getMessageDate() != null){
            LocalDateTime startOfTheDay = messageFilterDTO.getMessageDate().atStartOfDay();
            LocalDateTime endOfTheDay = messageFilterDTO.getMessageDate().plusDays(1).atStartOfDay().minusSeconds(1);
            queryWrapper.between(ChatMessage::getCreateTime,startOfTheDay,endOfTheDay);
        }
      
        if(StrUtil.isNotBlank(messageFilterDTO.getMessageContent()) &&
                messageFilterDTO.getMessageType().equals(ChatMessageType.TEXT)){
            queryWrapper.apply("JSON_UNQUOTE(JSON_EXTRACT(content, '$.text')) LIKE {0}", "%" +
                    messageFilterDTO.getMessageContent() + "%");
        }

        queryWrapper.orderByDesc(ChatMessage::getCreateTime,ChatMessage::getId);

        BriefConversationDTO conversation = conversationService.getBriefConversation(conversationId, UserContext.getUser());

        Page<ChatMessage> page = page(new Page<>(messageFilterDTO.getPageNumber(), messageFilterDTO.getPageSize()), queryWrapper);

        List<ChatMessageVO> collect = page.getRecords()
                .stream()
                .map(chat -> {
                    ChatMessageVO vo = convertToVO(chat, UserContext.getUser(), false);
                    vo.setConversation(conversation);
                    return vo;
                })
                .collect(Collectors.toList());

        return PageResult.of(page, collect);
    }

    @Override
    public ChatMessageVO getChatMessageVO(Long id, boolean needConversation) {
        ChatMessage chatMessage = getById(id);
        if (chatMessage == null) {
            return null;
        }
        return convertToVO(chatMessage, needConversation);
    }


}
