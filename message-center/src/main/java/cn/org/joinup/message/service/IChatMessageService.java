package cn.org.joinup.message.service;

import cn.org.joinup.api.dto.ChatMessageDTO;
import cn.org.joinup.api.dto.ChatMessageVO;
import cn.org.joinup.message.domain.po.ChatMessage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IChatMessageService extends IService<ChatMessage> {
    void onMessage(ChatMessageDTO chatMessageDTO);

    ChatMessageVO convertToVO(ChatMessage chatMessage, boolean needConversation);

    ChatMessageVO convertToVO(ChatMessage chatMessage, Long receiverId, boolean needConversation);

    ChatMessageVO getChatMessageVO(Long id, boolean needConversation);

    ChatMessageVO getChatMessageVO(Long id);
}