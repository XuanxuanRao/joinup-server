package cn.org.joinup.message.application.log.service;

import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.message.domain.log.entity.LogEntry;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;

public interface ILogEntryService extends IService<LogEntry> {
    PageResult<LogEntry> pageQuery(String path, String method, Long userId, LocalDateTime startTime, LocalDateTime endTime, Integer pageSize, Integer pageNumber);
}
