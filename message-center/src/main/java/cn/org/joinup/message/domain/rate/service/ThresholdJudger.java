package cn.org.joinup.message.domain.rate.service;

import cn.org.joinup.message.domain.rate.entity.ExchangeRateMonitorRule;
import cn.org.joinup.message.domain.rate.ExchangeRate;
import cn.org.joinup.message.domain.rate.RateThresholdEvent;
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
    // State for Hysteresis: true if currently in "Low" state
    private boolean isLowState = false;

    public void judge(ExchangeRate exchangeRate, ExchangeRateMonitorRule rule) {
        BigDecimal currentRate = exchangeRate.getRate();
        BigDecimal upper = rule.getThresholds().getAbsoluteUpper();
        BigDecimal lower = rule.getThresholds().getAbsoluteLower();
        BigDecimal margin = rule.getThresholds().getHysteresisMargin();
        
        log.info("[ThresholdJudger] Current: {}, Upper: {}, Lower: {}, Margin: {}, HighState: {}, LowState: {}", 
                currentRate, upper, lower, margin, isHighState, isLowState);

        // Logic for Upper Threshold with Hysteresis
        // Enter High State
        if (!isHighState && currentRate.compareTo(upper) >= 0) {
            isHighState = true;
            publishEvent(exchangeRate, upper, rule, "ABSOLUTE_UPPER",
                    String.format("人民币兑日元汇率(%s)已超过上限阈值(%s)，建议关注。", currentRate, upper));
        }
        // Exit High State
        else if (isHighState && currentRate.compareTo(upper.subtract(margin)) < 0) {
            isHighState = false;
            log.info("[ThresholdJudger] Rate dropped below hysteresis exit threshold ({}). State reset to normal.", upper.subtract(margin));
        }

        // Logic for Lower Threshold with Hysteresis
        // Enter Low State
        if (!isLowState && currentRate.compareTo(lower) <= 0) {
            isLowState = true;
            publishEvent(exchangeRate, lower, rule, "ABSOLUTE_LOWER",
                    String.format("人民币兑日元汇率(%s)已低于下限阈值(%s)，建议关注。", currentRate, lower));
        }
        // Exit Low State
        else if (isLowState && currentRate.compareTo(lower.add(margin)) > 0) {
            isLowState = false;
            log.info("[ThresholdJudger] Rate rose above hysteresis exit threshold ({}). State reset to normal.", lower.add(margin));
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
