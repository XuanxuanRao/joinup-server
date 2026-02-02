package cn.org.joinup.message.interfaces.scheduler;

import cn.org.joinup.message.domain.rate.entity.ExchangeRateMonitorRule;
import cn.org.joinup.message.domain.rate.service.ExchangeRateMonitorService;
import cn.org.joinup.message.application.rate.service.IExchangeRateRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.scheduling.quartz.QuartzJobBean;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Slf4j
@RequiredArgsConstructor
public class RateFetchJob extends QuartzJobBean {

    private final ExchangeRateMonitorService monitorService;

    private final IExchangeRateRuleService exchangeRateRuleService;

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) throws JobExecutionException {
        log.info("Running job to fetch exchange rate ...");
        try {
            exchangeRateRuleService.lambdaQuery()
                            .eq(ExchangeRateMonitorRule::getActive, true)
                            .list()
                            .forEach(monitorService::performCheck);
            log.info("Job to fetch exchange rate completed successfully.");
        } catch (Exception e) {
            log.error("Error occurs when fetching exchange rate", e);
        }
    }
}
