package cn.org.joinup.message.infrastructure.repository;

import cn.org.joinup.message.domain.log.entity.LogEntry;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogEntryMapper extends BaseMapper<LogEntry> {
}
