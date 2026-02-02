package cn.org.joinup.message.application.chat.service;

import cn.org.joinup.api.dto.BriefConversationDTO;
import cn.org.joinup.api.dto.ChatMessageVO;
import cn.org.joinup.api.dto.ConversationDTO;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.message.domain.chat.entity.ChatMessage;
import cn.org.joinup.message.domain.chat.entity.Conversation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Set;

public interface IConversationService extends IService<Conversation> {

    Conversation getConversationById(String conversationId);

    Set<Long> getParticipants(String conversationId);

    boolean in(String conversationId, Long userId);

    PageResult<ConversationDTO> queryConversations(Long userId, Integer pageNumber, Integer pageSize, String type);

    ConversationDTO getConversationDTO(String conversationId);

    ChatMessageVO getLastMessage(String conversationId);

    Conversation tryCreatePrivateConversation(Long requesterId, Long inviteeId);

    Conversation tryCreateGroupConversation(Long requesterId, Long teamId);

    BriefConversationDTO getBriefConversation(String conversationId);

    BriefConversationDTO getBriefConversation(String conversationId, Long receiverId);

    /**
     * 当conversation收到新消息时，更新会话状态 <p>
     * 更新用户未读消息数，conversation的最后一条消息
     * @param conversationId 会话ID
     * @param chatMessage 新消息
     */
    void updateConversationOnMessage(String conversationId, ChatMessage chatMessage);

    void clearConversationUnreadMessage(String conversationId);
}
