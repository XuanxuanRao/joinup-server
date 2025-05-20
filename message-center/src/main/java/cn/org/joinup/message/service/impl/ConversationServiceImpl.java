package cn.org.joinup.message.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.org.joinup.api.client.TeamClient;
import cn.org.joinup.api.client.UserClient;
import cn.org.joinup.api.dto.ConversationDTO;
import cn.org.joinup.api.dto.TeamDTO;
import cn.org.joinup.api.dto.UserDTO;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.message.constant.RedisConstant;
import cn.org.joinup.message.domain.po.ChatMessage;
import cn.org.joinup.message.domain.po.Conversation;
import cn.org.joinup.message.mapper.ConversationMapper;
import cn.org.joinup.message.service.IChatMessageService;
import cn.org.joinup.message.service.IConversationParticipantService;
import cn.org.joinup.message.service.IConversationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
@RequiredArgsConstructor
public class ConversationServiceImpl extends ServiceImpl<ConversationMapper, Conversation> implements IConversationService {

    private final StringRedisTemplate stringRedisTemplate;

    private final IConversationParticipantService conversationParticipantService;

    private final UserClient userClient;

    private final TeamClient teamClient;

    @Lazy
    @Resource
    private IChatMessageService chatMessageService;

    @Override
    public Conversation getConversationById(String conversationId) {
        final String key = RedisConstant.CONVERSATION_KEY_PREFIX + conversationId;
        if (stringRedisTemplate.hasKey(key)) {
            String conversationJSON = stringRedisTemplate.opsForValue().get(key);
            if (StrUtil.isNotBlank(conversationJSON)) {
                return JSONUtil.toBean(conversationJSON, Conversation.class);
            }
        }
        Conversation conversation = getById(conversationId);
        if (conversation == null) {
            stringRedisTemplate.opsForValue().set(key, "", RedisConstant.CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(conversation), RedisConstant.CONVERSATION_TTL, TimeUnit.SECONDS);
        return conversation;
    }

    @Override
    public Set<Long> getParticipants(String conversationId) {
        final String key = RedisConstant.CONVERSATION_PARTICIPANTS_KEY_PREFIX + conversationId;
        if (stringRedisTemplate.hasKey(key)) {
            Set<String> userIds = stringRedisTemplate.opsForSet().members(key);
            if (userIds != null) {
                return userIds.stream().map(Long::valueOf).collect(Collectors.toSet());
            }
        }

        Set<Long> participants = conversationParticipantService.getParticipantsByConversationId(conversationId);
        stringRedisTemplate.opsForValue().set(key, String.valueOf(participants), RedisConstant.CONVERSATION_PARTICIPANTS_TTL, TimeUnit.SECONDS);
        return participants;
    }

    @Override
    public boolean in(String conversationId, Long userId) {
        return getParticipants(conversationId).contains(userId);
    }

    @Override
    public PageResult<Conversation> queryConversations(Long userId, Integer pageNumber, Integer pageSize, String type) {
        String key = RedisConstant.USER_CONVERSATIONS_KEY_PREFIX + userId;
        List<Conversation> conversations;
        if (stringRedisTemplate.hasKey(key)) {
            Set<String> conversationIds = stringRedisTemplate.opsForSet().members(key);
            conversations = lambdaQuery()
                    .in(Conversation::getId, conversationIds)
                    .eq(StrUtil.isNotBlank(type), Conversation::getType, type)
                    .list();
        } else {
            conversations = lambdaQuery()
                    .eq(Conversation::getType, type)
                    .list();
            if (conversations != null) {
                stringRedisTemplate.opsForSet().add(key, conversations.stream().map(Conversation::getId).distinct().toArray(String[]::new));
                stringRedisTemplate.expire(key, RedisConstant.USER_CONVERSATIONS_TTL, TimeUnit.SECONDS);
            }
        }

        if (conversations == null || conversations.isEmpty()) {
            return PageResult.empty(0L, 0L);
        }

        Map<String, LocalDateTime> lastMessageTimes = new HashMap<>();
        for (Conversation conversation : conversations) {
            lastMessageTimes.put(conversation.getId(),
                    Optional.ofNullable(getLastMessage(conversation.getId()))
                            .map(ChatMessage::getCreateTime)
                            .orElse(conversation.getCreateTime()));
        }

        conversations.sort((o1, o2) -> lastMessageTimes.get(o2.getId()).compareTo(lastMessageTimes.get(o1.getId())));

        int start = (pageNumber - 1) * pageSize;
        int end = Math.min(start + pageSize, conversations.size());

        List<Conversation> paginatedConversations = conversations.subList(start, end);
        PageResult<Conversation> pageResult = new PageResult<>();
        pageResult.setTotal((long) conversations.size());
        pageResult.setPages((long) Math.ceil((double) conversations.size() / pageSize));
        pageResult.setList(paginatedConversations);
        return pageResult;
    }

    @Override
    public ConversationDTO getConversationDTO(String conversationId) {
        Conversation conversation = getConversationById(conversationId);
        ConversationDTO conversationDTO = BeanUtil.copyProperties(conversation, ConversationDTO.class);
        if ("private".equals(conversation.getType())) {
            UserDTO userInfo = userClient.getUserInfo().getData();
            conversationDTO.setName(userInfo.getUsername());
            conversationDTO.setCover(userInfo.getAvatar());
        } else if ("group".equals(conversation.getType())) {
            TeamDTO teamInfo = teamClient.queryTeam(conversation.getTeamId()).getData();
            conversationDTO.setName(teamInfo.getName());
            conversationDTO.setCover(null);
        }
        conversationDTO.setLastMessage(chatMessageService.toChatMessageVO(getLastMessage(conversationId)));
        return conversationDTO;
    }

    @Override
    public ChatMessage getLastMessage(String conversationId) {
        final String key = RedisConstant.CONVERSATION_LAST_MESSAGE_KEY_PREFIX + conversationId;
        if (!stringRedisTemplate.hasKey(key)) {
            return null;
        }
        return JSONUtil.toBean(stringRedisTemplate.opsForValue().get(key), ChatMessage.class);
    }

}
