package cn.org.joinup.message.service.impl;

import cn.org.joinup.message.domain.po.ConversationParticipant;
import cn.org.joinup.message.mapper.ConversationParticipantMapper;
import cn.org.joinup.message.service.IConversationParticipantService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
public class ConversationParticipantServiceImpl extends ServiceImpl<ConversationParticipantMapper, ConversationParticipant> implements IConversationParticipantService {

    @Override
    public Set<Long> getParticipantsByConversationId(String conversationId) {
        return lambdaQuery()
                .select(ConversationParticipant::getUserId)
                .eq(ConversationParticipant::getConversationId, conversationId)
                .list()
                .stream()
                .map(ConversationParticipant::getUserId)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getConversationsByUserId(Long userId) {
        return lambdaQuery()
                .select(ConversationParticipant::getConversationId)
                .eq(ConversationParticipant::getUserId, userId)
                .list()
                .stream()
                .map(ConversationParticipant::getConversationId)
                .collect(Collectors.toSet());
    }
}
