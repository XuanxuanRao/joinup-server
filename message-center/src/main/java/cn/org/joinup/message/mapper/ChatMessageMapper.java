package cn.org.joinup.message.mapper;

import cn.org.joinup.message.domain.po.ChatMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
    List<ChatMessage> findConversationLastMessage();
}
