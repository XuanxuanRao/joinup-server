package cn.org.joinup.message.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.message.domain.po.ChatMessage;
import cn.org.joinup.message.service.IAdminChatLogService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/message/chat")
@RequiredArgsConstructor
public class AdminChatLogController {

    private final IAdminChatLogService iAdminChatLogService;

    @GetMapping("/count")
    public Result<Long> count() {
        Long count = iAdminChatLogService.count();
        return Result.success(count);
    }

    // 分页加载数据
    @GetMapping("/list")
    public IPage<ChatMessage> list(Pageable pageable) {
        return iAdminChatLogService.getPageChatMessages(pageable);
    }

    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestBody Map<String, Long> body) {
        Long id = body.get("id");
        iAdminChatLogService.removeById(id);
        return Result.success();
    }

    @DeleteMapping("/delete/batch")
    public Result<Void> deleteBatch(@RequestBody Map<String, List<Long>> body) {
        List<Long> ids = body.get("ids");
        iAdminChatLogService.removeByIds(ids);
        return Result.success();
    }

    // 根据队伍名称模糊查询
    @GetMapping("/querySearch")
    public Result<List<ChatMessage>> querySearch(@RequestParam String name) {
        List<ChatMessage> messages = iAdminChatLogService.list();
        List<ChatMessage> result = new ArrayList<>();
        for (ChatMessage message : messages) {
            if (message.getConversationId().contains(name)) {
                result.add(message);
            }
        }

        return Result.success(result);
    }

    // 返回模糊查询数量
    @GetMapping("/searchCount")
    public Result<Long> searchCount(@RequestParam String name) {
        List<ChatMessage> messages = iAdminChatLogService.list();
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
        return iAdminChatLogService.getPageChatMessagesSearch(name, pageable);
    }
    
}