package cn.org.joinup.message.controller.user;

import cn.org.joinup.common.result.Result;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.message.domain.dto.request.splash.AddExchangeRateMonitorRuleDTO;
import cn.org.joinup.message.domain.po.ExchangeRateMonitorRule;
import cn.org.joinup.message.service.IExchangeRateRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

}
