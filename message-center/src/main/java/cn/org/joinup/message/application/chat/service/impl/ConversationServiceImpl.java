package cn.org.joinup.message.application.chat.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.org.joinup.api.client.TeamClient;
import cn.org.joinup.api.client.UserClient;
import cn.org.joinup.api.dto.*;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.message.infrastructure.constant.RedisConstant;
import cn.org.joinup.message.domain.chat.entity.ChatMessage;
import cn.org.joinup.message.domain.chat.entity.Conversation;
import cn.org.joinup.message.domain.chat.entity.ConversationParticipant;
import cn.org.joinup.message.infrastructure.repository.ConversationMapper;
import cn.org.joinup.message.application.chat.service.IChatMessageService;
import cn.org.joinup.message.application.chat.service.IConversationParticipantService;
import cn.org.joinup.message.application.chat.service.IConversationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.org.joinup.common.util.UserContext.getUserId;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
@Slf4j
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
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
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
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            Set<String> userIds = stringRedisTemplate.opsForSet().members(key);
            if (userIds != null) {
                return userIds.stream().map(Long::valueOf).collect(Collectors.toSet());
            }
        }

        Set<Long> participants = conversationParticipantService.getParticipantsByConversationId(conversationId);
        if (!participants.isEmpty()) {
            stringRedisTemplate.opsForSet().add(key, participants.stream().map(String::valueOf).toArray(String[]::new));
            stringRedisTemplate.expire(key, RedisConstant.CONVERSATION_PARTICIPANTS_TTL, TimeUnit.SECONDS);
        }
        return participants;
    }

    @Override
    public boolean in(String conversationId, Long userId) {
        return getParticipants(conversationId).contains(userId);
    }

    @Override
    public PageResult<ConversationDTO> queryConversations(Long userId, Integer pageNumber, Integer pageSize, String type) {
        Set<String> conversationsIds = findUserConversations(userId);
        if (conversationsIds == null || conversationsIds.isEmpty()) {
            return PageResult.empty(0L, 0L);
        }

        List<Conversation> conversations = lambdaQuery()
                .in(Conversation::getId, conversationsIds)
                .eq(StrUtil.isNotBlank(type), Conversation::getType, type)
                .list();

        if (conversations == null || conversations.isEmpty()) {
            return PageResult.empty(0L, 0L);
        }

        Map<String, LocalDateTime> lastMessageTimes = new HashMap<>();
        for (Conversation conversation : conversations) {
            lastMessageTimes.put(conversation.getId(),
                    Optional.ofNullable(getLastMessage(conversation.getId()))
                            .map(ChatMessageVO::getCreateTime)
                            .orElse(conversation.getCreateTime()));
        }

        conversations.sort((o1, o2) -> lastMessageTimes.get(o2.getId()).compareTo(lastMessageTimes.get(o1.getId())));

        int start = (pageNumber - 1) * pageSize;
        int end = Math.min(start + pageSize, conversations.size());

        List<ConversationDTO> paginatedConversations = conversations.subList(start, end).stream()
                .map(conversation -> convertToDTO(conversation, userId))
                .collect(Collectors.toList());
        PageResult<ConversationDTO> pageResult = new PageResult<>();
        pageResult.setTotal((long) conversations.size());
        pageResult.setPages((long) Math.ceil((double) conversations.size() / pageSize));
        pageResult.setList(paginatedConversations);
        return pageResult;
    }

    @Override
    public ConversationDTO getConversationDTO(String conversationId) {
        Conversation conversation = getConversationById(conversationId);
        return convertToDTO(conversation, getUserId());
    }

    @Override
    public ChatMessageVO getLastMessage(String conversationId) {
        final String key = RedisConstant.CONVERSATION_LAST_MESSAGE_KEY_PREFIX + conversationId;
        if (!stringRedisTemplate.hasKey(key)) {
            return null;
        }
        String messageId = stringRedisTemplate.opsForValue().get(key);
        if (messageId == null) {
            return null;
        }
        return chatMessageService.getChatMessageVO(Long.valueOf(messageId), false);
    }

    private Set<String> findUserConversations(Long userId) {
        String key = RedisConstant.USER_CONVERSATIONS_KEY_PREFIX + userId;
        if (stringRedisTemplate.hasKey(key)) {
            return stringRedisTemplate.opsForSet().members(key);
        }
        Set<String> conversationIds = conversationParticipantService.getConversationsByUserId(userId);
        if (conversationIds == null || conversationIds.isEmpty()) {
            return Collections.emptySet();
        }
        stringRedisTemplate.opsForSet().add(key, conversationIds.toArray(new String[0]));
        stringRedisTemplate.expire(key, RedisConstant.USER_CONVERSATIONS_TTL, TimeUnit.SECONDS);
        return conversationIds;
    }

    @Override
    @Transactional
    public Conversation tryCreatePrivateConversation(Long requesterId, Long inviteeId) {
        Conversation conversation = findPrivateConversation(requesterId, inviteeId);
        if (conversation != null) {
            return conversation;
        }

        if (userClient.queryUser(inviteeId).getData() == null) {
            return null;
        }

        conversation = new Conversation();
        conversation.setType("private");
        conversation.setCreateTime(LocalDateTime.now());
        conversation.setTeamId(null);
        save(conversation);
        conversationParticipantService.save(new ConversationParticipant(conversation.getId(), requesterId, LocalDateTime.now()));
        conversationParticipantService.save(new ConversationParticipant(conversation.getId(), inviteeId, LocalDateTime.now()));

        stringRedisTemplate.opsForSet().add(RedisConstant.USER_CONVERSATIONS_KEY_PREFIX + requesterId, conversation.getId());
        stringRedisTemplate.opsForSet().add(RedisConstant.USER_CONVERSATIONS_KEY_PREFIX + inviteeId, conversation.getId());

        stringRedisTemplate.opsForSet().add(RedisConstant.CONVERSATION_PARTICIPANTS_KEY_PREFIX + conversation.getId(), requesterId.toString(), inviteeId.toString());

        return conversation;
    }

    @Override
    @Transactional
    public Conversation tryCreateGroupConversation(Long requesterId, Long teamId) {
        if (teamClient.getUserRole(teamId).getData() == null) {
            return null;
        }

        Conversation conversation = lambdaQuery()
                .eq(Conversation::getType, "group")
                .eq(Conversation::getTeamId, teamId)
                .one();

        if (conversation == null) {
            conversation = new Conversation();
            conversation.setType("group");
            conversation.setCreateTime(LocalDateTime.now());
            conversation.setTeamId(teamId);
            save(conversation);
        }

        if (getParticipants(conversation.getId()).contains(requesterId)) {
            return conversation;
        }
        conversationParticipantService.addParticipant(conversation.getId(), requesterId);
        return conversation;
    }

    @Override
    public BriefConversationDTO getBriefConversation(String conversationId) {
        return getBriefConversation(conversationId, getUserId());
    }

    @Override
    public BriefConversationDTO getBriefConversation(String conversationId, Long receiverId) {
        Conversation conversation = getConversationById(conversationId);
        if (conversation == null) {
            return null;
        }
        BriefConversationDTO briefConversationDTO = BeanUtil.copyProperties(conversation, BriefConversationDTO.class);
        switch (conversation.getType()) {
            case "private":
                UserDTO userInfo = userClient.queryUser(findAnotherUser(conversationId, receiverId)).getData();
                briefConversationDTO.setName(userInfo.getUsername());
                briefConversationDTO.setCover(userInfo.getAvatar());
                break;
            case "group":
                TeamDTO teamInfo = teamClient.queryTeam(conversation.getTeamId()).getData();
                briefConversationDTO.setName(teamInfo.getName());
                briefConversationDTO.setCover(teamInfo.getCover());
                break;
        }
        return briefConversationDTO;
    }

    @Override
    public void updateConversationOnMessage(String conversationId, ChatMessage chatMessage) {
        String lastMessageKey = RedisConstant.CONVERSATION_LAST_MESSAGE_KEY_PREFIX + conversationId;
        stringRedisTemplate.opsForValue().set(lastMessageKey, String.valueOf(chatMessage.getId()));

        getParticipants(conversationId).forEach(userId -> {
            if (Objects.equals(chatMessage.getSenderId(), userId)) {
                return;
            }
            String userUnreadMessageKey = RedisConstant.USER_CONVERSATION_UNREAD_MESSAGE_KEY_PREFIX + conversationId + ":" + userId;
            String userCurrentConversationKey = RedisConstant.USER_AT_CONVERSATION + userId;
            String userCurrentConversationId = stringRedisTemplate.opsForValue().get(userCurrentConversationKey);
            if(!conversationId.equals(userCurrentConversationId)) {
                stringRedisTemplate.opsForValue().increment(userUnreadMessageKey, 1);
            }
        });
    }

    @Override
    public void clearConversationUnreadMessage(String conversationId) {
        Long userId = getUserId();
        String userUnreadMessageKey = RedisConstant.USER_CONVERSATION_UNREAD_MESSAGE_KEY_PREFIX + conversationId + ":" + userId;
        stringRedisTemplate.opsForValue().set(userUnreadMessageKey,String.valueOf(0));
    }

    private Conversation findPrivateConversation(Long userId1, Long userId2) {
        Set<String> ids = stringRedisTemplate.opsForSet().intersect(
                RedisConstant.USER_CONVERSATIONS_KEY_PREFIX + userId1,
                RedisConstant.USER_CONVERSATIONS_KEY_PREFIX + userId2
        );

        if (ids == null || ids.isEmpty()) {
            return null;
        }

        return lambdaQuery()
                .in(true, Conversation::getId, ids)
                .eq(Conversation::getType, "private")
                .last("LIMIT 1")
                .one();
    }

    /**
     * 找到私聊会话中的另一个人
     */
    private Long findAnotherUser(String conversationId, Long userId) {
        Set<Long> participants = getParticipants(conversationId);
        participants.remove(userId);
        if (participants.size() != 1) {
            log.info("Strange Behaviour: {}", userId);
        }
        return participants.isEmpty() ? null : participants.iterator().next();
    }

    private int getUnreadMessageOfUserInConversation(String conversationId, Long userId) {
        String key = RedisConstant.USER_CONVERSATION_UNREAD_MESSAGE_KEY_PREFIX + conversationId + ":" + userId;
        if (!stringRedisTemplate.hasKey(key)) {
            return 0;
        }
        String num = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isBlank(num)) {
            return 0;
        } else {
            return Integer.parseInt(num);
        }
    }

    /**
     * Convert conversation entity to VO
     */
    private ConversationDTO convertToDTO(Conversation conversation, Long receiverId) {
        ConversationDTO conversationDTO = BeanUtil.copyProperties(conversation, ConversationDTO.class);
        switch (conversationDTO.getType()) {
            case "private":
                UserDTO userInfo = userClient.queryUser(findAnotherUser(conversation.getId(), receiverId)).getData();
                conversationDTO.setName(userInfo.getUsername());
                conversationDTO.setCover(userInfo.getAvatar());
                break;
            case "group":
                Optional.ofNullable(teamClient.queryTeam(conversation.getTeamId()).getData())
                        .ifPresent(teamInfo -> {
                            conversationDTO.setName(teamInfo.getName());
                            conversationDTO.setCover(teamInfo.getCover());
                        });
                break;
        }
        conversationDTO.setUnreadMessageCount(getUnreadMessageOfUserInConversation(conversation.getId(), getUserId()));
        conversationDTO.setLastMessage(getLastMessage(conversation.getId()));
        return conversationDTO;
    }


}
