package cn.org.joinup.message.monitor;

import cn.org.joinup.message.domain.po.ExchangeRateMonitorRule;
import cn.org.joinup.message.monitor.domain.ExchangeRate;
import cn.org.joinup.message.monitor.domain.RateThresholdEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ThresholdJudger {

    private final EventPublisher eventPublisher;

    // State for Hysteresis: true if currently in "High" state
    private boolean isHighState = false;

    public void judge(ExchangeRate exchangeRate, ExchangeRateMonitorRule rule) {
        BigDecimal currentRate = exchangeRate.getRate();
        BigDecimal upper = rule.getThresholds().getAbsoluteUpper();
        BigDecimal margin = rule.getThresholds().getHysteresisMargin();
        
        log.info("[ThresholdJudger] Current: {}, Upper: {}, Margin: {}, HighState: {}", 
                currentRate, upper, margin, isHighState);

        // Logic for Upper Threshold with Hysteresis
        // Enter High State
        if (!isHighState && currentRate.compareTo(upper) >= 0) {
            isHighState = true;
            publishEvent(exchangeRate, upper, rule, "ABSOLUTE_UPPER",
                    String.format("人民币兑日元汇率(%s)已超过阈值(%s)，建议关注。", currentRate, upper));
        }
        // Exit High State
        else if (isHighState && currentRate.compareTo(upper.subtract(margin)) < 0) {
            isHighState = false;
            log.info("[ThresholdJudger] Rate dropped below hysteresis exit threshold ({}). State reset to normal.", upper.subtract(margin));
        }
    }

    private void publishEvent(ExchangeRate rate, BigDecimal threshold, ExchangeRateMonitorRule rule, String type, String msg) {
        RateThresholdEvent event = RateThresholdEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .currencyPair(rate.getFromCurrency() + "/" + rate.getToCurrency())
                .currentRate(rate.getRate())
                .threshold(threshold)
                .triggerType(type)
                .dataSource(rate.getSource())
                .message(msg)
                .monitorRuleSnapshot(rule)
                .build();
        
        eventPublisher.publish(event);
    }
}
