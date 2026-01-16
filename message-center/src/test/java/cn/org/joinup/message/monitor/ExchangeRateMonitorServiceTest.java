package cn.org.joinup.message.monitor;

import cn.org.joinup.message.monitor.config.MonitorConfig;
import cn.org.joinup.message.monitor.domain.ExchangeRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ExchangeRateMonitorServiceTest {

    private MonitorConfig monitorConfig;
    private ThresholdJudger thresholdJudger;
    private RateFetcher primaryFetcher;
    private RateFetcher secondaryFetcher;
    private ExchangeRateMonitorService service;

    @BeforeEach
    public void setUp() {
        monitorConfig = new MonitorConfig();
        monitorConfig.getDatasource().setPrimary("primary");
        monitorConfig.getDatasource().setSecondary("secondary");

        thresholdJudger = Mockito.mock(ThresholdJudger.class);
        
        primaryFetcher = Mockito.mock(RateFetcher.class);
        when(primaryFetcher.getSourceId()).thenReturn("primary");
        
        secondaryFetcher = Mockito.mock(RateFetcher.class);
        when(secondaryFetcher.getSourceId()).thenReturn("secondary");

        List<RateFetcher> fetchers = Arrays.asList(primaryFetcher, secondaryFetcher);
        
        service = new ExchangeRateMonitorService(fetchers, thresholdJudger, monitorConfig);
    }

    @Test
    public void testPrimarySuccess() {
        when(primaryFetcher.fetchRate(any(), any())).thenReturn(Optional.of(ExchangeRate.builder()
                .rate(BigDecimal.TEN)
                .fromCurrency("CNY").toCurrency("JPY").source("primary").build()));

        service.performCheck();

        verify(primaryFetcher, times(1)).fetchRate(any(), any());
        verify(secondaryFetcher, never()).fetchRate(any(), any());
        verify(thresholdJudger, times(1)).judge(any());
    }

    @Test
    public void testPrimaryFailSecondarySuccess() {
        when(primaryFetcher.fetchRate(any(), any())).thenReturn(Optional.empty());
        when(secondaryFetcher.fetchRate(any(), any())).thenReturn(Optional.of(ExchangeRate.builder()
                .rate(BigDecimal.TEN)
                .fromCurrency("CNY").toCurrency("JPY").source("secondary").build()));

        service.performCheck();

        verify(primaryFetcher, times(1)).fetchRate(any(), any());
        verify(secondaryFetcher, times(1)).fetchRate(any(), any());
        verify(thresholdJudger, times(1)).judge(any());
    }

    @Test
    public void testAllFail() {
        when(primaryFetcher.fetchRate(any(), any())).thenReturn(Optional.empty());
        when(secondaryFetcher.fetchRate(any(), any())).thenReturn(Optional.empty());

        service.performCheck();

        verify(primaryFetcher, times(1)).fetchRate(any(), any());
        verify(secondaryFetcher, times(1)).fetchRate(any(), any());
        verify(thresholdJudger, never()).judge(any());
    }
}
