package cn.org.joinup.message.application.rate.service;

import cn.org.joinup.message.application.rate.dto.AddExchangeRateMonitorRuleDTO;
import cn.org.joinup.message.application.rate.dto.UpdateExchangeRateMonitorRuleDTO;
import cn.org.joinup.message.domain.rate.entity.ExchangeRateMonitorRule;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IExchangeRateRuleService extends IService<ExchangeRateMonitorRule> {
    ExchangeRateMonitorRule addRule(AddExchangeRateMonitorRuleDTO addRuleDTO);

    ExchangeRateMonitorRule updateRule(Long ruleId, UpdateExchangeRateMonitorRuleDTO updateRuleDTO);

    void disableRule(Long ruleId, String token);
}
