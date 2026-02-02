package cn.org.joinup.message.application.rate.service.impl;

import cn.org.joinup.message.domain.rate.entity.RateThresholdEventLog;
import cn.org.joinup.message.infrastructure.repository.RateThresholdEventLogMapper;
import cn.org.joinup.message.application.rate.service.IRateThresholdEventLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class RateThresholdEventLogServiceImpl extends ServiceImpl<RateThresholdEventLogMapper, RateThresholdEventLog> implements IRateThresholdEventLogService {
}
