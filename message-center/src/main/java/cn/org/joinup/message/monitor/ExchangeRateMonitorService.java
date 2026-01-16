package cn.org.joinup.message.monitor;

import cn.org.joinup.message.monitor.domain.ExchangeRate;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ExchangeRateMonitorService {

    private final RateFetcher rateFetcher;
    private final ThresholdJudger thresholdJudger;
    private final Cache<String, ExchangeRate> rateCache;

    public ExchangeRateMonitorService(RateFetcher rateFetcher, ThresholdJudger thresholdJudger) {
        this.rateFetcher = rateFetcher;
        this.thresholdJudger = thresholdJudger;
        this.rateCache = Caffeine.newBuilder()
                .expireAfterWrite(290, TimeUnit.SECONDS) // Slightly less than 5 mins (300s)
                .maximumSize(10)
                .build();
    }

    public void performCheck() {
        log.info("[Monitor] Starting rate fetch...");
        Optional<ExchangeRate> rateOpt = rateFetcher.fetchRate("CNY", "JPY");
        
        rateOpt.ifPresent(rate -> {
            log.info("[Monitor] Successfully fetched CNY/JPY rate: {} from {}.", rate.getRate(), rate.getSource());
            rateCache.put(rate.getFromCurrency() + "/" + rate.getToCurrency(), rate);
            
            // Phase 2: Threshold check and Event publishing
            thresholdJudger.judge(rate);
        });
    }
}
