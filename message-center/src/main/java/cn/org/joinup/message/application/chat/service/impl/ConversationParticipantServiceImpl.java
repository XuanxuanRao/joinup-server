package cn.org.joinup.message.application.chat.service.impl;

import cn.org.joinup.message.infrastructure.constant.RedisConstant;
import cn.org.joinup.message.domain.chat.entity.ConversationParticipant;
import cn.org.joinup.message.infrastructure.repository.ConversationParticipantMapper;
import cn.org.joinup.message.application.chat.service.IConversationParticipantService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
@RequiredArgsConstructor
public class ConversationParticipantServiceImpl extends ServiceImpl<ConversationParticipantMapper, ConversationParticipant> implements IConversationParticipantService {

    private final StringRedisTemplate stringRedisTemplate;

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

    @Override
    public void addParticipant(String conversationId, Long userId) {
        ConversationParticipant conversationParticipant = new ConversationParticipant();
        conversationParticipant.setConversationId(conversationId);
        conversationParticipant.setUserId(userId);
        conversationParticipant.setCreateTime(LocalDateTime.now());
        save(conversationParticipant);

        final String conversationParticipantsKey = RedisConstant.CONVERSATION_PARTICIPANTS_KEY_PREFIX + conversationId;
        final String userConversationsKey = RedisConstant.USER_CONVERSATIONS_KEY_PREFIX + userId;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(conversationParticipantsKey))) {
            stringRedisTemplate.opsForSet().add(conversationParticipantsKey, String.valueOf(userId));
        }
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(userConversationsKey))) {
            stringRedisTemplate.opsForSet().add(userConversationsKey, conversationId);
        }
    }

    @Override
    public void removeParticipant(String conversationId, Long userId) {
        lambdaUpdate()
                .eq(ConversationParticipant::getConversationId, conversationId)
                .eq(ConversationParticipant::getUserId, userId)
                .remove();

        final String conversationParticipantsKey = RedisConstant.CONVERSATION_PARTICIPANTS_KEY_PREFIX + conversationId;
        final String userConversationsKey = RedisConstant.USER_CONVERSATIONS_KEY_PREFIX + userId;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(conversationParticipantsKey))) {
            stringRedisTemplate.opsForSet().remove(conversationParticipantsKey, String.valueOf(userId));
        }
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(userConversationsKey))) {
            stringRedisTemplate.opsForSet().remove(userConversationsKey, conversationId);
        }
    }
}
