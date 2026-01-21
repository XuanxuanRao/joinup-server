package cn.org.joinup.message.service.impl;

import cn.org.joinup.message.domain.po.RateThresholdEventLog;
import cn.org.joinup.message.mapper.RateThresholdEventLogMapper;
import cn.org.joinup.message.service.IRateThresholdEventLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class RateThresholdEventLogServiceImpl extends ServiceImpl<RateThresholdEventLogMapper, RateThresholdEventLog> implements IRateThresholdEventLogService {
}
