package cn.org.joinup.message.service;

import cn.org.joinup.message.domain.dto.request.monitor.AddExchangeRateMonitorRuleDTO;
import cn.org.joinup.message.domain.dto.request.monitor.UpdateExchangeRateMonitorRuleDTO;
import cn.org.joinup.message.domain.po.ExchangeRateMonitorRule;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IExchangeRateRuleService extends IService<ExchangeRateMonitorRule> {
    ExchangeRateMonitorRule addRule(AddExchangeRateMonitorRuleDTO addRuleDTO);

    ExchangeRateMonitorRule updateRule(Long ruleId, UpdateExchangeRateMonitorRuleDTO updateRuleDTO);
}
