package cn.org.joinup.message.controller;

import cn.org.joinup.api.dto.BriefConversationDTO;
import cn.org.joinup.api.dto.ChatMessageVO;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.message.domain.po.ChatMessage;
import cn.org.joinup.message.service.IChatMessageService;
import cn.org.joinup.message.service.IConversationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/message/chat")
@RequiredArgsConstructor
public class ChatMessageController {

    private final IChatMessageService chatMessageService;
    private final IConversationService conversationService;

    @GetMapping("/{conversationId}")
    public Result<PageResult<ChatMessageVO>> list(@PathVariable String conversationId,
                                            @RequestParam Integer pageNumber,
                                            @RequestParam Integer pageSize) {
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessage::getConversationId, conversationId);
        queryWrapper.orderByDesc(ChatMessage::getCreateTime);

        BriefConversationDTO conversation = conversationService.getBriefConversation(conversationId, UserContext.getUser());

        Page<ChatMessage> page = chatMessageService.page(new Page<>(pageNumber, pageSize), queryWrapper);
        List<ChatMessageVO> collect = page.getRecords()
                .stream()
                .map(chat -> {
                    ChatMessageVO vo = chatMessageService.convertToVO(chat, UserContext.getUser(), false);
                    vo.setConversation(conversation);
                    return vo;
                })
                .collect(Collectors.toList());
        return Result.success(PageResult.of(page, collect));
    }

}
