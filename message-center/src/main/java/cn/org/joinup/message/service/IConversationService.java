package cn.org.joinup.message.service;

import cn.org.joinup.api.dto.ConversationDTO;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.message.domain.po.ChatMessage;
import cn.org.joinup.message.domain.po.Conversation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Set;

public interface IConversationService extends IService<Conversation> {

    Conversation getConversationById(String conversationId);

    Set<Long> getParticipants(String conversationId);

    boolean in(String conversationId, Long userId);

    PageResult<Conversation> queryConversations(Long userId, Integer pageNumber, Integer pageSize, String type);

    ConversationDTO getConversationDTO(String conversationId);

    ChatMessage getLastMessage(String conversationId);

    Conversation tryCreateConversation(Long requesterId, Long inviteeId);
}
