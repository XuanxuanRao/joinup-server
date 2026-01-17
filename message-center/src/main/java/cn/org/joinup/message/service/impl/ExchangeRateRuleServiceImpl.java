package cn.org.joinup.message.service.impl;

import cn.org.joinup.common.exception.BadRequestException;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.message.domain.dto.request.splash.AddExchangeRateMonitorRuleDTO;
import cn.org.joinup.message.domain.po.ExchangeRateMonitorRule;
import cn.org.joinup.message.mapper.ExchangeRateMonitorRuleMapper;
import cn.org.joinup.message.service.IExchangeRateRuleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ExchangeRateRuleServiceImpl extends ServiceImpl<ExchangeRateMonitorRuleMapper, ExchangeRateMonitorRule> implements IExchangeRateRuleService {
    @Override
    public ExchangeRateMonitorRule addRule(AddExchangeRateMonitorRuleDTO addRuleDTO) {
        ExchangeRateMonitorRule.Thresholds thresholds = new ExchangeRateMonitorRule.Thresholds();
        if (addRuleDTO.getAbsoluteUpper() != null) {
            thresholds.setAbsoluteUpper(addRuleDTO.getAbsoluteUpper());
        }
        if (addRuleDTO.getAbsoluteLower() != null) {
            thresholds.setAbsoluteLower(addRuleDTO.getAbsoluteLower());
        }

        ExchangeRateMonitorRule rule = ExchangeRateMonitorRule.builder()
                .userId(UserContext.getUserId())
                .email(addRuleDTO.getEmail())
                .baseCurrency(addRuleDTO.getBaseCurrency())
                .quoteCurrency(addRuleDTO.getQuoteCurrency())
                .thresholds(thresholds)
                .active(true)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        if (save(rule)) {
            return rule;
        }
        throw new BadRequestException("create exchange rate monitor rule failed");
    }
}
