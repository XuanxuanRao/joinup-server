package cn.org.joinup.message.service.impl;

import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.message.domain.po.LogEntry;
import cn.org.joinup.message.mapper.LogEntryMapper;
import cn.org.joinup.message.service.ILogEntryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
public class LogEntryServiceImpl extends ServiceImpl<LogEntryMapper, LogEntry> implements ILogEntryService {

    @Override
    public PageResult<LogEntry> pageQuery(String path, String method, Long userId, LocalDateTime startTime, LocalDateTime endTime, Integer pageSize, Integer pageNumber) {
        LambdaQueryWrapper<LogEntry> queryWrapper = new LambdaQueryWrapper<>();
        if (path != null) {
            queryWrapper.eq(LogEntry::getPath, path);
        }
        if (method != null) {
            queryWrapper.eq(LogEntry::getMethod, method);
        }
        if (userId != null) {
            queryWrapper.eq(LogEntry::getUserId, userId);
        }
        if (startTime != null) {
            queryWrapper.ge(LogEntry::getCreateTime, startTime);
        }
        if (endTime != null) {
            queryWrapper.le(LogEntry::getCreateTime, endTime);
        }
        queryWrapper.orderByDesc(LogEntry::getCreateTime);
        return PageResult.of(page(new Page<>(pageNumber, pageSize), queryWrapper));
    }
}
