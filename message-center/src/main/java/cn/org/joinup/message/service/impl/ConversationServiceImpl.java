package cn.org.joinup.message.service.impl;

import cn.org.joinup.message.domain.po.Conversation;
import cn.org.joinup.message.mapper.ConversationMapper;
import cn.org.joinup.message.service.IConversationParticipantService;
import cn.org.joinup.message.service.IConversationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
@RequiredArgsConstructor
public class ConversationServiceImpl extends ServiceImpl<ConversationMapper, Conversation> implements IConversationService {

    private final StringRedisTemplate stringRedisTemplate;

    private final IConversationParticipantService conversationParticipantService;

    @Override
    public Conversation getConversationById(String conversationId) {
        Conversation conversation = getById(conversationId);
        return conversation;
    }

    @Override
    public Set<Long> getParticipants(String conversationId) {
        Set<Long> participants = conversationParticipantService.getParticipantsByConversationId(conversationId);

        return participants;
    }
}
