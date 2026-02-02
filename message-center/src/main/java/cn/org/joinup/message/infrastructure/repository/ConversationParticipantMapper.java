package cn.org.joinup.message.infrastructure.repository;

import cn.org.joinup.message.domain.chat.entity.ConversationParticipant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConversationParticipantMapper extends BaseMapper<ConversationParticipant> {

}