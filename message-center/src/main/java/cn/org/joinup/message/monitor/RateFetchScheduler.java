package cn.org.joinup.message.monitor;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RateFetchScheduler {

    private final ExchangeRateMonitorService monitorService;

    @Scheduled(fixedRateString = "${exchange.monitor.fetch-rate-in-milliseconds:300000}")
    public void fetchRate() {
        monitorService.performCheck();
    }
}
