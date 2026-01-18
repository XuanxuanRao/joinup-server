package cn.org.joinup.message.controller.user;

import cn.org.joinup.common.result.Result;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.message.domain.dto.request.monitor.AddExchangeRateMonitorRuleDTO;
import cn.org.joinup.message.domain.dto.request.monitor.UpdateExchangeRateMonitorRuleDTO;
import cn.org.joinup.message.domain.po.ExchangeRateMonitorRule;
import cn.org.joinup.message.enums.CurrencyCode;
import cn.org.joinup.message.service.IExchangeRateRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/message/feature/rate-monitor")
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateMonitorController {

    private final IExchangeRateRuleService exchangeRateRuleService;

    @GetMapping("/rules")
    public Result<List<ExchangeRateMonitorRule>> getRules() {
        return Result.success(exchangeRateRuleService.lambdaQuery()
                .eq(ExchangeRateMonitorRule::getUserId, UserContext.getUserId())
                .list());
    }

    @PostMapping("/rules")
    public Result<ExchangeRateMonitorRule> addRule(@RequestBody @Validated AddExchangeRateMonitorRuleDTO addRuleDTO) {
        try {
            log.info("Add exchange rate monitor rule, userId={}, addRuleDTO={}", UserContext.getUserId(), addRuleDTO);
            return Result.success(exchangeRateRuleService.addRule(addRuleDTO));
        } catch (Exception e) {
            log.error("Add exchange rate monitor rule failed, userId={}, addRuleDTO={}", UserContext.getUserId(), addRuleDTO, e);
            return Result.error("Add exchange rate monitor rule failed");
        }
    }

    @PutMapping("/rules/{ruleId}")
    public Result<ExchangeRateMonitorRule> updateRule(@PathVariable Long ruleId,
                                                      @RequestBody @Validated UpdateExchangeRateMonitorRuleDTO updateRuleDTO) {
        try {
            log.info("Update exchange rate monitor rule, userId={}, ruleId={}, updateRuleDTO={}", UserContext.getUserId(), ruleId, updateRuleDTO);
            return Result.success(exchangeRateRuleService.updateRule(ruleId, updateRuleDTO));
        } catch (Exception e) {
            log.error("Update exchange rate monitor rule failed, userId={}, ruleId={}, updateRuleDTO={}", UserContext.getUserId(), ruleId, updateRuleDTO, e);
            return Result.error("Update exchange rate monitor rule failed");
        }
    }

    @GetMapping("/config/currency")
    public Result<Map<String, String>> getAvailableCurrencies() {
        return Result.success(CurrencyCode.getCodeToNameMap());
    }

    @GetMapping("/rules/{ruleId}/unsubscribe")
    public Result<Void> unsubscribe(@PathVariable Long ruleId, @RequestParam String token) {
        try {
            log.info("Unsubscribe exchange rate monitor rule, ruleId={}, token={}", ruleId, token);
            exchangeRateRuleService.disableRule(ruleId, token);
            return Result.success();
        } catch (Exception e) {
            log.error("Unsubscribe exchange rate monitor rule failed, ruleId={}, token={}", ruleId, token, e);
            return Result.error("Unsubscribe exchange rate monitor rule failed");
        }
    }

}
