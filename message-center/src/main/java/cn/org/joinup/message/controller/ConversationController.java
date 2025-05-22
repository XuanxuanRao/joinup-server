package cn.org.joinup.message.controller;

import cn.org.joinup.api.dto.ConversationDTO;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.message.constant.RedisConstant;
import cn.org.joinup.message.domain.po.ChatMessage;
import cn.org.joinup.message.domain.po.Conversation;
import cn.org.joinup.message.mapper.ChatMessageMapper;
import cn.org.joinup.message.service.IChatMessageService;
import cn.org.joinup.message.service.IConversationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/conversation")
@RequiredArgsConstructor
@Api(tags = "会话接口")
public class ConversationController {

    private final IConversationService conversationService;
    private final IChatMessageService chatMessageService;
    private final ChatMessageMapper chatMessageMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @GetMapping("/list")
    public Result<PageResult<ConversationDTO>> list(
            @RequestParam(required = false) String type,
            @RequestParam Integer pageNumber,
            @RequestParam Integer pageSize) {
        return Result.success(conversationService.queryConversations(UserContext.getUser(), pageNumber, pageSize, type));
    }


    @PostMapping("/create")
    @ApiOperation("发起会话")
    public Result<Conversation> create(@RequestParam Long userId) {
        if (userId == null || Objects.equals(userId, UserContext.getUser())) {
            return Result.error("Invalid Operation");
        }
        return Result.success(conversationService.tryCreateConversation(UserContext.getUser(), userId));
    }

    @PostMapping("/updateRedis")
    @ApiOperation("刷新聊天系统缓存，与数据库同步")
    public Result<Void> updateCache() {
        List<ChatMessage> conversationLastMessage = chatMessageMapper.findConversationLastMessage();
        for (ChatMessage chatMessage : conversationLastMessage) {
            String key = RedisConstant.CONVERSATION_LAST_MESSAGE_KEY_PREFIX + chatMessage.getConversationId();
            stringRedisTemplate.opsForValue().set(key, chatMessage.getId().toString());
        }
        return Result.success();
    }

}
