package cn.org.joinup.message.infrastructure.repository;

import cn.org.joinup.message.domain.rate.entity.RateThresholdEventLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RateThresholdEventLogMapper extends BaseMapper<RateThresholdEventLog> {
}
