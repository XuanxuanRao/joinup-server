package cn.org.joinup.message.controller;

import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.message.domain.po.Conversation;
import cn.org.joinup.message.service.IConversationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/conversation")
@RequiredArgsConstructor
@Api(tags = "会话接口")
public class ConversationController {

    private final IConversationService conversationService;

    @GetMapping("/list")
    public Result<PageResult<Conversation>> list(
            @RequestParam(required = false) String type,
            @RequestParam Integer pageNumber,
            @RequestParam Integer pageSize) {
        return Result.success(conversationService.queryConversations(UserContext.getUser(), pageNumber, pageSize, type));
    }


    @PostMapping("/create")
    @ApiOperation("发起会话")
    public Result<Conversation> create(@RequestParam Long userId) {
        return Result.success(conversationService.tryCreateConversation(UserContext.getUser(), userId));
    }

}
