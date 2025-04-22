package cn.org.joinup.message.service.impl;

import cn.org.joinup.message.domain.po.VerifyLog;
import cn.org.joinup.message.mapper.VerifyLogMapper;
import cn.org.joinup.message.service.IVerifyLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
public class VerifyLogServiceImpl extends ServiceImpl<VerifyLogMapper, VerifyLog> implements IVerifyLogService {

    @Override
    public LocalDateTime lastSendTime(String account) {
    return lambdaQuery()
            .eq(VerifyLog::getAccount, account)
            .orderByDesc(VerifyLog::getCreateTime)
            .last("limit 1")
            .oneOpt()
            .map(VerifyLog::getCreateTime)
            .orElse(null); // 如果不存在，返回 null
    }

    @Override
    public Integer getSendCount(String account, LocalDate date) {
        return lambdaQuery()
                .eq(VerifyLog::getAccount, account)
                .ge(VerifyLog::getCreateTime, date.atStartOfDay())
                .lt(VerifyLog::getCreateTime, date.plusDays(1).atStartOfDay())
                .count()
                .intValue();
    }
}
