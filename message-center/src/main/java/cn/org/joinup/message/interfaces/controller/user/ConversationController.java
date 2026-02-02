package cn.org.joinup.message.interfaces.controller.user;

import cn.org.joinup.api.client.TeamClient;
import cn.org.joinup.api.dto.ConversationDTO;
import cn.org.joinup.api.dto.TeamDTO;
import cn.org.joinup.api.enums.TeamStatus;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.message.infrastructure.constant.RedisConstant;
import cn.org.joinup.message.domain.chat.entity.ChatMessage;
import cn.org.joinup.message.domain.chat.entity.Conversation;
import cn.org.joinup.message.infrastructure.repository.ChatMessageMapper;
import cn.org.joinup.message.application.chat.service.IConversationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/conversation")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "会话接口")
public class ConversationController {

    private final IConversationService conversationService;
    private final ChatMessageMapper chatMessageMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final TeamClient teamClient;

    @GetMapping("/list")
    @ApiOperation("分页获取会话列表")
    public Result<PageResult<ConversationDTO>> list(
            @RequestParam(required = false) String type,
            @RequestParam Integer pageNumber,
            @RequestParam Integer pageSize) {
        return Result.success(conversationService.queryConversations(UserContext.getUserId(), pageNumber, pageSize, type));
    }

    @GetMapping("/{conversationId}")
    @ApiOperation("获取会话详情")
    @PreAuthorize("@permissionChecker.hasAccessToConversation(#conversationId)")
    public Result<ConversationDTO> getConversation(@PathVariable String conversationId) {
        return Result.success(conversationService.getConversationDTO(conversationId));
    }

    @PostMapping("/create")
    @ApiOperation(value = "发起会话", notes = "通过userId或teamId发起会话，如果已经存在，直接返回该会话，否则会创建新的会话")
    public Result<Conversation> create(@RequestParam(required = false) Long userId, @RequestParam(required = false) Long teamId) {
        if (userId != null && teamId != null) {
            return Result.error("userId and teamId cannot be both set");
        }
        if (userId != null) {
            if (Objects.equals(UserContext.getUserId(), userId)) {
                return Result.error("Cannot create conversation with yourself");
            } else {
                return Result.success(conversationService.tryCreatePrivateConversation(UserContext.getUserId(), userId));
            }
        } else if (teamId != null) {
            return Optional.ofNullable(conversationService.tryCreateGroupConversation(UserContext.getUserId(), teamId))
                    .map(Result::success)
                    .orElseGet(() -> Result.error("未加入该队伍"));
        }
        return Result.error("userId or teamId must be set");
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

    @PostMapping("/{conversationId}/read")
    @ApiOperation("清除会话的未读消息")
    @PreAuthorize("@permissionChecker.hasAccessToConversation(#conversationId)")
    public Result<Void> clearConversationUnreadMessage(@PathVariable String conversationId) {
        conversationService.clearConversationUnreadMessage(conversationId);
        return Result.success();
    }

    @PostMapping("/loadTeam")
    @ApiOperation("加载队伍聊天会话")
    public Result<Void> loadGroupConversation(@RequestParam Long maxTeamId) {
        for (long teamId = 0; teamId < maxTeamId; teamId++) {
            Result<TeamDTO> queryTeamResult = teamClient.queryTeam(teamId);
            if (Objects.equals(queryTeamResult.getCode(), Result.ERROR) || queryTeamResult.getData() == null) {
                continue;
            }
            if (queryTeamResult.getData().getStatus() != TeamStatus.NORMAL) {
                continue;
            }

            Conversation conversation = conversationService.lambdaQuery()
                    .eq(Conversation::getType, "group")
                    .eq(Conversation::getTeamId, queryTeamResult.getData().getId())
                    .one();
            if (conversation != null) {
                continue;
            }

            conversation = new Conversation();
            conversation.setType("group");
            conversation.setTeamId(queryTeamResult.getData().getId());
            conversation.setCreateTime(queryTeamResult.getData().getCreateTime());
            conversationService.save(conversation);

            log.info("load group conversation for team {}", teamId);
        }
        return Result.success();
    }

    @PostMapping("/{conversationId}/enter")
    @PreAuthorize("@permissionChecker.hasAccessToConversation(#conversationId)")
    public Result<Void> enterConversation(@PathVariable String conversationId){
        final String key = RedisConstant.USER_AT_CONVERSATION + UserContext.getUserId();
        stringRedisTemplate.opsForValue().set(key, conversationId);
        return Result.success();
    }

    @DeleteMapping("/exit")
    public Result<Void> exitConversation() {
        String key = RedisConstant.USER_AT_CONVERSATION + UserContext.getUserId();
        stringRedisTemplate.delete(key);
        return Result.success();
    }

}
