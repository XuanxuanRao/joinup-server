package cn.org.joinup.message.service;

import cn.org.joinup.message.domain.po.Conversation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Set;

public interface IConversationService extends IService<Conversation> {

    Conversation getConversationById(String conversationId);

    Set<Long> getParticipants(String conversationId);

}
