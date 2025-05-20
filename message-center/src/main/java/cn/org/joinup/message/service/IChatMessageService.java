package cn.org.joinup.message.service;

import cn.org.joinup.api.dto.ChatMessageDTO;
import cn.org.joinup.api.dto.ChatMessageVO;
import cn.org.joinup.message.domain.po.ChatMessage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IChatMessageService extends IService<ChatMessage> {
    void onMessage(ChatMessageDTO chatMessageDTO);

    ChatMessageVO toChatMessageVO(ChatMessage chatMessage);
}