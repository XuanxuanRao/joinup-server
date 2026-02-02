package cn.org.joinup.message.interfaces.controller.admin;

import cn.org.joinup.api.dto.ChatMessageVO;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.message.application.chat.dto.MessageFilterDTO;
import cn.org.joinup.message.domain.chat.entity.ChatMessage;
import cn.org.joinup.message.application.chat.service.IAdminChatLogService;
import cn.org.joinup.message.application.chat.service.IChatMessageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin/message/chat")
@RequiredArgsConstructor
public class AdminChatMessageController {

    private final IAdminChatLogService adminChatLogService;

    private final IChatMessageService chatMessageService;

    @GetMapping("/count")
    public Result<Long> count() {
        Long count = adminChatLogService.count();
        return Result.success(count);
    }

    // 分页加载数据
    @GetMapping("/list")
    public IPage<ChatMessage> list(Pageable pageable) {
        return adminChatLogService.getPageChatMessages(pageable);
    }

    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestBody Map<String, Long> body) {
        Long id = body.get("id");
        adminChatLogService.removeById(id);
        return Result.success();
    }

    @DeleteMapping("/delete/batch")
    public Result<Void> deleteBatch(@RequestBody Map<String, List<Long>> body) {
        List<Long> ids = body.get("ids");
        adminChatLogService.removeByIds(ids);
        return Result.success();
    }

    // 根据队伍名称模糊查询
    @GetMapping("/querySearch")
    public Result<List<ChatMessage>> querySearch(@RequestParam String name) {
        List<ChatMessage> messages = adminChatLogService.list();
        List<ChatMessage> result = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (ChatMessage message : messages) {
            String cid = message.getConversationId();
            // 包含搜索关键字 & 还没见过这个 ID
            if (cid.contains(name) && seen.add(cid)) {
                result.add(message);
            }
        }

        return Result.success(result);
    }

    // 返回模糊查询数量
    @GetMapping("/searchCount")
    public Result<Long> searchCount(@RequestParam String name) {
        List<ChatMessage> messages = adminChatLogService.list();
        Long count = 0L;
        for (ChatMessage message : messages) {
            if (message.getConversationId().contains(name)) {
                count++;
            }
        }
        return Result.success(count);
    }

    // 分页显示模糊查询结果
    @GetMapping("/search")
    public IPage<ChatMessage> search(@RequestParam String name,
                              @RequestParam("page") int page, @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return adminChatLogService.getPageChatMessagesSearch(name, pageable);
    }

    @PreAuthorize("@permissionChecker.hasAccessToConversation(#conversationId)")
    @PostMapping("/{conversationId}/filter")
    @ApiOperation("对聊天信息进行过滤")
    public Result<PageResult<ChatMessageVO>> filterMessage(@PathVariable String conversationId,
                                                           @RequestBody MessageFilterDTO messageFilterDTO){
        return Result.success(chatMessageService.queryConversationMessage(conversationId, messageFilterDTO));
    }
    
}