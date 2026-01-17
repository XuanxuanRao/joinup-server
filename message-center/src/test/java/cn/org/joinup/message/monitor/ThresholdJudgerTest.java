package cn.org.joinup.message.monitor;

import cn.org.joinup.message.domain.po.ExchangeRateMonitorRule;
import cn.org.joinup.message.monitor.domain.ExchangeRate;
import cn.org.joinup.message.monitor.domain.RateThresholdEvent;
import org.junit.jupiter.api.BeforeEach;
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
                                .hysteresisMargin(new BigDecimal("0.02"))
                                .build())
                .build());

        verify(eventPublisher, times(1)).publish(any(RateThresholdEvent.class));
    }

    @Test
    public void testHysteresis() {
        // 1. Enter High State
        ExchangeRate rateHigh = ExchangeRate.builder()
                .rate(new BigDecimal("21.51"))
                .fromCurrency("CNY").toCurrency("JPY").source("test").build();
        thresholdJudger.judge(rateHigh, ExchangeRateMonitorRule.builder()
                        .thresholds(ExchangeRateMonitorRule.Thresholds.builder()
                                .absoluteUpper(new BigDecimal("21.50"))
                                .hysteresisMargin(new BigDecimal("0.02"))
                                .build())
                .build());
        verify(eventPublisher, times(1)).publish(any(RateThresholdEvent.class));

        // 2. Stay in High State (between 21.48 and 21.50)
        ExchangeRate rateMiddle = ExchangeRate.builder()
                .rate(new BigDecimal("21.49"))
                .fromCurrency("CNY").toCurrency("JPY").source("test").build();
        thresholdJudger.judge(rateMiddle, ExchangeRateMonitorRule.builder()
                        .thresholds(ExchangeRateMonitorRule.Thresholds.builder()
                                .absoluteUpper(new BigDecimal("21.50"))
                                .hysteresisMargin(new BigDecimal("0.02"))
                                .build())
                .build());
        // Should NOT trigger anything (stay high)
        verify(eventPublisher, times(1)).publish(any(RateThresholdEvent.class));

        // 3. Exit High State
        ExchangeRate rateLow = ExchangeRate.builder()
                .rate(new BigDecimal("21.47"))
                .fromCurrency("CNY").toCurrency("JPY").source("test").build();
        thresholdJudger.judge(rateLow, ExchangeRateMonitorRule.builder()
                        .thresholds(ExchangeRateMonitorRule.Thresholds.builder()
                                .absoluteUpper(new BigDecimal("21.50"))
                                .hysteresisMargin(new BigDecimal("0.02"))
                                .build())
                .build());
        // Should have exited state (internally), but no event published for exit in current impl
        // Verify no new event
        verify(eventPublisher, times(1)).publish(any(RateThresholdEvent.class));

        // 4. Re-enter High State
        thresholdJudger.judge(rateHigh, ExchangeRateMonitorRule.builder()
                        .thresholds(ExchangeRateMonitorRule.Thresholds.builder()
                                .absoluteUpper(new BigDecimal("21.50"))
                                .hysteresisMargin(new BigDecimal("0.02"))
                                .build())
                .build());
        // Should trigger again
        verify(eventPublisher, times(2)).publish(any(RateThresholdEvent.class));
    }
}
