package cn.org.joinup.message.monitor;

import cn.org.joinup.message.monitor.config.MonitorConfig;
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
        MonitorConfig monitorConfig = new MonitorConfig();
        // Set defaults
        monitorConfig.getThresholds().setAbsoluteUpper(new BigDecimal("21.50"));
        monitorConfig.getThresholds().setHysteresisMargin(new BigDecimal("0.02"));
        
        eventPublisher = Mockito.mock(EventPublisher.class);
        thresholdJudger = new ThresholdJudger(monitorConfig, eventPublisher);
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

        thresholdJudger.judge(rate);

        verify(eventPublisher, times(1)).publish(any(RateThresholdEvent.class));
    }

    @Test
    public void testHysteresis() {
        // 1. Enter High State
        ExchangeRate rateHigh = ExchangeRate.builder()
                .rate(new BigDecimal("21.51"))
                .fromCurrency("CNY").toCurrency("JPY").source("test").build();
        thresholdJudger.judge(rateHigh);
        verify(eventPublisher, times(1)).publish(any(RateThresholdEvent.class));

        // 2. Stay in High State (between 21.48 and 21.50)
        ExchangeRate rateMiddle = ExchangeRate.builder()
                .rate(new BigDecimal("21.49"))
                .fromCurrency("CNY").toCurrency("JPY").source("test").build();
        thresholdJudger.judge(rateMiddle);
        // Should NOT trigger anything (stay high)
        verify(eventPublisher, times(1)).publish(any(RateThresholdEvent.class));

        // 3. Exit High State
        ExchangeRate rateLow = ExchangeRate.builder()
                .rate(new BigDecimal("21.47"))
                .fromCurrency("CNY").toCurrency("JPY").source("test").build();
        thresholdJudger.judge(rateLow);
        // Should have exited state (internally), but no event published for exit in current impl
        // Verify no new event
        verify(eventPublisher, times(1)).publish(any(RateThresholdEvent.class));

        // 4. Re-enter High State
        thresholdJudger.judge(rateHigh);
        // Should trigger again
        verify(eventPublisher, times(2)).publish(any(RateThresholdEvent.class));
    }
}
