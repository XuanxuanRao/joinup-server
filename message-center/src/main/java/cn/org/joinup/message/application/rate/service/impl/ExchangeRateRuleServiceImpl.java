package cn.org.joinup.message.application.rate.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.common.exception.BadRequestException;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.message.infrastructure.config.ExchangeRateMonitorConfig;
import cn.org.joinup.message.application.rate.dto.AddExchangeRateMonitorRuleDTO;
import cn.org.joinup.message.application.rate.dto.UpdateExchangeRateMonitorRuleDTO;
import cn.org.joinup.message.domain.rate.entity.ExchangeRateMonitorRule;
import cn.org.joinup.message.infrastructure.repository.ExchangeRateMonitorRuleMapper;
import cn.org.joinup.message.application.rate.service.IExchangeRateRuleService;
import cn.org.joinup.message.infrastructure.util.TokenUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExchangeRateRuleServiceImpl extends ServiceImpl<ExchangeRateMonitorRuleMapper, ExchangeRateMonitorRule> implements IExchangeRateRuleService {

    private final TokenUtil tokenUtil;
    private final ExchangeRateMonitorConfig monitorConfig;

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

    @Override
    public ExchangeRateMonitorRule updateRule(Long ruleId, UpdateExchangeRateMonitorRuleDTO updateRuleDTO) {
        ExchangeRateMonitorRule rule = getById(ruleId);
        if (rule == null) {
            throw new BadRequestException("exchange rate monitor rule not found");
        }

        BeanUtil.copyProperties(updateRuleDTO, rule);
        rule.setUpdateTime(LocalDateTime.now());
        if (updateRuleDTO.getAbsoluteLower() != null) {
            rule.getThresholds().setAbsoluteLower(updateRuleDTO.getAbsoluteLower());
        }
        if (updateRuleDTO.getAbsoluteUpper() != null) {
            rule.getThresholds().setAbsoluteUpper(updateRuleDTO.getAbsoluteUpper());
        }
        if (updateById(rule)) {
            return rule;
        }
        throw new BadRequestException("update exchange rate monitor rule failed");
    }

    @Override
    public void disableRule(Long ruleId, String token) {
        ExchangeRateMonitorRule rule = getById(ruleId);
        if (rule == null) {
            throw new BadRequestException("exchange rate monitor rule not found");
        }
        // validate token
        try {
            Map<String, String> params = tokenUtil.validateAndConsumeToken(monitorConfig.getUnsubscribeBusinessCode(), token);
            if (params == null || !String.valueOf(ruleId).equals(params.get("ruleId"))) {
                throw new BadRequestException("invalid token for the specified rule");
            }
        } catch (Exception e) {
            throw new BadRequestException("invalid or expired token");
        }

        rule.setActive(false);
        rule.setUpdateTime(LocalDateTime.now());
        if (!updateById(rule)) {
            throw new BadRequestException("disable exchange rate monitor rule failed");
        }
    }
}
