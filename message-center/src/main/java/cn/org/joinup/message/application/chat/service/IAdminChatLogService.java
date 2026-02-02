package cn.org.joinup.message.application.chat.service;

import cn.org.joinup.message.domain.chat.entity.ChatMessage;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.data.domain.Pageable;

public interface IAdminChatLogService extends IService<ChatMessage> {
    /**
     * 分页查询聊天记录
     *
     * @param pageable 分页参数
     * @return 聊天记录列表
     */
    IPage<ChatMessage> getPageChatMessages(Pageable pageable);

    /**
     * 分页查询标签
     *
     * @param name     聊天内容
     * @param pageable 分页参数
     * @return 聊天记录列表
     */
    IPage<ChatMessage> getPageChatMessagesSearch(String name, Pageable pageable);

    //TODO 根据conversationId(userId, teamId)查询
}
