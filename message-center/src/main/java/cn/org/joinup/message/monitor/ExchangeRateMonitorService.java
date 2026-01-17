package cn.org.joinup.message.monitor;

import cn.org.joinup.message.config.ExchangeRateMonitorConfig;
import cn.org.joinup.message.domain.po.ExchangeRateMonitorRule;
import cn.org.joinup.message.monitor.domain.ExchangeRate;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExchangeRateMonitorService {

    private final Map<String, RateFetcher> fetcherMap;
    private final ThresholdJudger thresholdJudger;
    private final ExchangeRateMonitorConfig monitorConfig;
    private final Cache<String, ExchangeRate> rateCache;

    public ExchangeRateMonitorService(List<RateFetcher> fetchers, 
                                      ThresholdJudger thresholdJudger,
                                      ExchangeRateMonitorConfig monitorConfig) {
        this.fetcherMap = fetchers.stream()
                .collect(Collectors.toMap(RateFetcher::getSourceId, Function.identity()));
        this.thresholdJudger = thresholdJudger;
        this.monitorConfig = monitorConfig;
        this.rateCache = Caffeine.newBuilder()
                .expireAfterWrite(290, TimeUnit.SECONDS) // Slightly less than 5 mins (300s)
                .maximumSize(10)
                .build();
    }

    public void performCheck(ExchangeRateMonitorRule rule) {
        log.info("[Monitor] Starting rate fetch...");
        
        if (!validateRule(rule)) {
            log.error("[Monitor] Invalid rule configuration: {}", rule);
            return;
        }

        Optional<ExchangeRate> rateOpt = fetchFromPrimary(rule.getBaseCurrency().getCode(), rule.getQuoteCurrency().getCode());
        if (rateOpt.isEmpty()) {
            log.warn("[Monitor] Primary source failed. Switching to secondary.");
            rateOpt = fetchFromSecondary(rule.getBaseCurrency().getCode(), rule.getQuoteCurrency().getCode());
        }

        rateOpt.ifPresentOrElse(rate -> {
             log.info("[Monitor] Successfully fetched {} rate: {} from {}.",
                     rule.getBaseCurrency() + "/" + rule.getQuoteCurrency(), rate.getRate(), rate.getSource());
            rateCache.put(rate.getFromCurrency() + "/" + rate.getToCurrency(), rate);
            
            // Phase 2: Threshold check and Event publishing
            thresholdJudger.judge(rate, rule);
        }, () -> log.error("[Monitor] All data sources failed to fetch rate."));
    }

    private Optional<ExchangeRate> fetchFromPrimary(String baseCurrency, String quoteCurrency) {
        String primaryId = monitorConfig.getDatasource().getPrimary();
        return fetchFromSource(primaryId, baseCurrency, quoteCurrency);
    }

    private Optional<ExchangeRate> fetchFromSecondary(String baseCurrency, String quoteCurrency) {
        String secondaryId = monitorConfig.getDatasource().getSecondary();
        return fetchFromSource(secondaryId, baseCurrency, quoteCurrency);
    }

    private Optional<ExchangeRate> fetchFromSource(String sourceId, String baseCurrency, String quoteCurrency) {
        if (sourceId == null || !fetcherMap.containsKey(sourceId)) {
            log.warn("[Monitor] Source ID {} not configured or implementation not found.", sourceId);
            return Optional.empty();
        }
        
        try {
            return fetcherMap.get(sourceId).fetchRate(baseCurrency, quoteCurrency);
        } catch (Exception e) {
            log.error("[Monitor] Exception fetching from source {}: {}", sourceId, e.getMessage());
            return Optional.empty();
        }
    }

    private boolean validateRule(ExchangeRateMonitorRule rule) {
        return rule.getBaseCurrency() != null
                && rule.getQuoteCurrency() != null
                && !rule.getBaseCurrency().equals(rule.getQuoteCurrency())
                && rule.getThresholds() != null
                && rule.getThresholds().getAbsoluteUpper() != null
                && rule.getThresholds().getAbsoluteLower() != null
                && rule.getThresholds().getHysteresisMargin() != null
                && rule.getThresholds().getRelativeIncreasePercentage() != null;
    }
}
