package cn.org.joinup.message.service;

import cn.org.joinup.message.domain.po.ConversationParticipant;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Set;

public interface IConversationParticipantService extends IService<ConversationParticipant> {
    Set<Long> getParticipantsByConversationId(String conversationId);

    Set<String> getConversationsByUserId(Long userId);

    void addParticipant(String conversationId, Long userId);

    void removeParticipant(String conversationId, Long userId);
}
