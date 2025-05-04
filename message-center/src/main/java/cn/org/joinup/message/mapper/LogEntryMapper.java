package cn.org.joinup.message.mapper;

import cn.org.joinup.message.domain.po.LogEntry;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogEntryMapper extends BaseMapper<LogEntry> {
}
