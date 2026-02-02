package cn.org.joinup.message.domain.rate.service;

import cn.org.joinup.message.domain.rate.entity.ExchangeRateMonitorRule;
import cn.org.joinup.message.domain.rate.ExchangeRate;
import cn.org.joinup.message.domain.rate.RateThresholdEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ThresholdJudgerTest {

    private EventPublisher eventPublisher;
    private ThresholdJudger thresholdJudger;

    @BeforeEach
    public void setUp() {
        eventPublisher = Mockito.mock(EventPublisher.class);
        thresholdJudger = new ThresholdJudger(eventPublisher);
    }

    @Test
    @DisplayName("Should trigger alert when rate exceeds upper threshold")
    public void testEnterHighState() {
        ExchangeRate rate = ExchangeRate.builder()
                .rate(new BigDecimal("21.51"))
                .fromCurrency("CNY")
                .toCurrency("JPY")
                .source("test")
                .timestamp(LocalDateTime.now())
                .build();

        thresholdJudger.judge(rate, ExchangeRateMonitorRule.builder()
                        .thresholds(ExchangeRateMonitorRule.Thresholds.builder()
                                .absoluteUpper(new BigDecimal("21.50"))
                                .absoluteLower(new BigDecimal("20.00"))
                                .hysteresisMargin(new BigDecimal("0.02"))
                                .build())
                .build());

        verify(eventPublisher, times(1)).publish(argThat(event -> 
            event.getTriggerType().equals("ABSOLUTE_UPPER") && 
            event.getThreshold().compareTo(new BigDecimal("21.50")) == 0
        ));
    }

    @Test
    @DisplayName("Should trigger alert when rate drops below lower threshold")
    public void testEnterLowState() {
        ExchangeRate rate = ExchangeRate.builder()
                .rate(new BigDecimal("19.99"))
                .fromCurrency("CNY")
                .toCurrency("JPY")
                .source("test")
                .timestamp(LocalDateTime.now())
                .build();

        thresholdJudger.judge(rate, ExchangeRateMonitorRule.builder()
                        .thresholds(ExchangeRateMonitorRule.Thresholds.builder()
                                .absoluteUpper(new BigDecimal("21.50"))
                                .absoluteLower(new BigDecimal("20.00"))
                                .hysteresisMargin(new BigDecimal("0.02"))
                                .build())
                .build());

        verify(eventPublisher, times(1)).publish(argThat(event -> 
            event.getTriggerType().equals("ABSOLUTE_LOWER") && 
            event.getThreshold().compareTo(new BigDecimal("20.00")) == 0
        ));
    }

    @Test
    @DisplayName("Should respect hysteresis for upper threshold")
    public void testHysteresisUpper() {
        ExchangeRateMonitorRule rule = ExchangeRateMonitorRule.builder()
                .thresholds(ExchangeRateMonitorRule.Thresholds.builder()
                        .absoluteUpper(new BigDecimal("21.50"))
                        .absoluteLower(new BigDecimal("20.00"))
                        .hysteresisMargin(new BigDecimal("0.02"))
                        .build())
                .build();

        // 1. Enter High State
        ExchangeRate rateHigh = ExchangeRate.builder()
                .rate(new BigDecimal("21.51"))
                .fromCurrency("CNY").toCurrency("JPY").source("test").build();
        thresholdJudger.judge(rateHigh, rule);
        verify(eventPublisher, times(1)).publish(any(RateThresholdEvent.class));

        // 2. Stay in High State (between 21.48 and 21.50)
        ExchangeRate rateMiddle = ExchangeRate.builder()
                .rate(new BigDecimal("21.49"))
                .fromCurrency("CNY").toCurrency("JPY").source("test").build();
        thresholdJudger.judge(rateMiddle, rule);
        // Should NOT trigger anything new (stay high)
        verify(eventPublisher, times(1)).publish(any(RateThresholdEvent.class));

        // 3. Exit High State (drop below 21.50 - 0.02 = 21.48)
        ExchangeRate rateLow = ExchangeRate.builder()
                .rate(new BigDecimal("21.47"))
                .fromCurrency("CNY").toCurrency("JPY").source("test").build();
        thresholdJudger.judge(rateLow, rule);
        // Still no new event, just internal state reset
        verify(eventPublisher, times(1)).publish(any(RateThresholdEvent.class));

        // 4. Re-enter High State
        thresholdJudger.judge(rateHigh, rule);
        // Should trigger again
        verify(eventPublisher, times(2)).publish(any(RateThresholdEvent.class));
    }

    @Test
    @DisplayName("Should respect hysteresis for lower threshold")
    public void testHysteresisLower() {
        ExchangeRateMonitorRule rule = ExchangeRateMonitorRule.builder()
                .thresholds(ExchangeRateMonitorRule.Thresholds.builder()
                        .absoluteUpper(new BigDecimal("21.50"))
                        .absoluteLower(new BigDecimal("20.00"))
                        .hysteresisMargin(new BigDecimal("0.02"))
                        .build())
                .build();

        // 1. Enter Low State (<= 20.00)
        ExchangeRate rateLow = ExchangeRate.builder()
                .rate(new BigDecimal("19.99"))
                .fromCurrency("CNY").toCurrency("JPY").source("test").build();
        thresholdJudger.judge(rateLow, rule);
        verify(eventPublisher, times(1)).publish(argThat(event -> event.getTriggerType().equals("ABSOLUTE_LOWER")));

        // 2. Stay in Low State (between 20.00 and 20.00 + 0.02 = 20.02)
        ExchangeRate rateMiddle = ExchangeRate.builder()
                .rate(new BigDecimal("20.01"))
                .fromCurrency("CNY").toCurrency("JPY").source("test").build();
        thresholdJudger.judge(rateMiddle, rule);
        // Should NOT trigger anything new
        verify(eventPublisher, times(1)).publish(any(RateThresholdEvent.class));

        // 3. Exit Low State (rise above 20.02)
        ExchangeRate rateNormal = ExchangeRate.builder()
                .rate(new BigDecimal("20.03"))
                .fromCurrency("CNY").toCurrency("JPY").source("test").build();
        thresholdJudger.judge(rateNormal, rule);
        // Still no new event
        verify(eventPublisher, times(1)).publish(any(RateThresholdEvent.class));

        // 4. Re-enter Low State
        thresholdJudger.judge(rateLow, rule);
        // Should trigger again
        verify(eventPublisher, times(2)).publish(any(RateThresholdEvent.class));
    }
}
