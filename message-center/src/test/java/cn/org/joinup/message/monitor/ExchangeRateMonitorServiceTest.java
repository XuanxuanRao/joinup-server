package cn.org.joinup.message.monitor;

import cn.org.joinup.message.config.ExchangeRateMonitorConfig;
import cn.org.joinup.message.domain.po.ExchangeRateMonitorRule;
import cn.org.joinup.message.domain.po.ExchangeRateMonitorRule.Thresholds;
import cn.org.joinup.message.monitor.domain.ExchangeRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateMonitorServiceTest {

    @Mock
    private ThresholdJudger thresholdJudger;
    @Mock
    private RateFetcher primaryFetcher;
    @Mock
    private RateFetcher secondaryFetcher;

    private ExchangeRateMonitorConfig monitorConfig;
    private ExchangeRateMonitorService service;

    @BeforeEach
    void setUp() {
        monitorConfig = new ExchangeRateMonitorConfig();
        monitorConfig.getDatasource().setPrimary("primary");
        monitorConfig.getDatasource().setSecondary("secondary");

        // Lenient stubs because some tests might not use both fetchers
        lenient().when(primaryFetcher.getSourceId()).thenReturn("primary");
        lenient().when(secondaryFetcher.getSourceId()).thenReturn("secondary");

        service = new ExchangeRateMonitorService(
                Arrays.asList(primaryFetcher, secondaryFetcher),
                thresholdJudger,
                monitorConfig
        );
    }

    private ExchangeRateMonitorRule createValidRule() {
        return ExchangeRateMonitorRule.builder()
                .baseCurrency("CNY")
                .quoteCurrency("JPY")
                .thresholds(Thresholds.builder()
                        .absoluteUpper(new BigDecimal("20"))
                        .absoluteLower(new BigDecimal("15"))
                        .hysteresisMargin(new BigDecimal("0.1"))
                        .relativeIncreasePercentage(new BigDecimal("5"))
                        .build())
                .build();
    }

    @Test
    @DisplayName("Primary source succeeds: should not call secondary and proceed to judge")
    void testPerformCheck_PrimarySuccess() {
        ExchangeRateMonitorRule rule = createValidRule();
        ExchangeRate mockRate = ExchangeRate.builder()
                .rate(BigDecimal.TEN)
                .fromCurrency("CNY").toCurrency("JPY").source("primary").build();

        when(primaryFetcher.fetchRate(eq("CNY"), eq("JPY"))).thenReturn(Optional.of(mockRate));

        service.performCheck(rule);

        verify(primaryFetcher, times(1)).fetchRate(eq("CNY"), eq("JPY"));
        verify(secondaryFetcher, never()).fetchRate(any(), any());
        verify(thresholdJudger, times(1)).judge(eq(mockRate), eq(rule));
    }

    @Test
    @DisplayName("Primary source fails (empty): should call secondary and proceed to judge")
    void testPerformCheck_PrimaryFail_SecondarySuccess() {
        ExchangeRateMonitorRule rule = createValidRule();
        ExchangeRate mockRate = ExchangeRate.builder()
                .rate(BigDecimal.TEN)
                .fromCurrency("CNY").toCurrency("JPY").source("secondary").build();

        when(primaryFetcher.fetchRate(any(), any())).thenReturn(Optional.empty());
        when(secondaryFetcher.fetchRate(eq("CNY"), eq("JPY"))).thenReturn(Optional.of(mockRate));

        service.performCheck(rule);

        verify(primaryFetcher, times(1)).fetchRate(eq("CNY"), eq("JPY"));
        verify(secondaryFetcher, times(1)).fetchRate(eq("CNY"), eq("JPY"));
        verify(thresholdJudger, times(1)).judge(eq(mockRate), eq(rule));
    }

    @Test
    @DisplayName("All sources fail: should not call judge")
    void testPerformCheck_AllFail() {
        ExchangeRateMonitorRule rule = createValidRule();

        when(primaryFetcher.fetchRate(any(), any())).thenReturn(Optional.empty());
        when(secondaryFetcher.fetchRate(any(), any())).thenReturn(Optional.empty());

        service.performCheck(rule);

        verify(primaryFetcher, times(1)).fetchRate(any(), any());
        verify(secondaryFetcher, times(1)).fetchRate(any(), any());
        verify(thresholdJudger, never()).judge(any(), any());
    }

    @Test
    @DisplayName("Primary source throws exception: should catch and switch to secondary")
    void testPerformCheck_PrimaryException() {
        ExchangeRateMonitorRule rule = createValidRule();
        ExchangeRate mockRate = ExchangeRate.builder()
                .rate(BigDecimal.TEN)
                .fromCurrency("CNY").toCurrency("JPY").source("secondary").build();

        when(primaryFetcher.fetchRate(any(), any())).thenThrow(new RuntimeException("API error"));
        when(secondaryFetcher.fetchRate(any(), any())).thenReturn(Optional.of(mockRate));

        service.performCheck(rule);

        verify(primaryFetcher, times(1)).fetchRate(any(), any());
        verify(secondaryFetcher, times(1)).fetchRate(any(), any());
        verify(thresholdJudger, times(1)).judge(any(), any());
    }

    @Test
    @DisplayName("Invalid rule (missing currency): should skip fetch")
    void testPerformCheck_InvalidRule_MissingCurrency() {
        ExchangeRateMonitorRule rule = createValidRule();
        rule.setBaseCurrency(null);

        service.performCheck(rule);

        verify(primaryFetcher, never()).fetchRate(any(), any());
        verify(secondaryFetcher, never()).fetchRate(any(), any());
        verify(thresholdJudger, never()).judge(any(), any());
    }

    @Test
    @DisplayName("Invalid rule (same currency): should skip fetch")
    void testPerformCheck_InvalidRule_SameCurrency() {
        ExchangeRateMonitorRule rule = createValidRule();
        rule.setQuoteCurrency("CNY"); // Same as base

        service.performCheck(rule);

        verify(primaryFetcher, never()).fetchRate(any(), any());
        verify(secondaryFetcher, never()).fetchRate(any(), any());
    }

    @Test
    @DisplayName("Source ID not found in fetcher map: should handle gracefully")
    void testPerformCheck_SourceConfigError() {
        // Setup config with unknown source IDs
        monitorConfig.getDatasource().setPrimary("unknown_primary");
        monitorConfig.getDatasource().setSecondary("unknown_secondary");
        
        // Re-initialize service with these configs (but fetchers still report "primary" and "secondary")
        service = new ExchangeRateMonitorService(
                Arrays.asList(primaryFetcher, secondaryFetcher),
                thresholdJudger,
                monitorConfig
        );
        
        ExchangeRateMonitorRule rule = createValidRule();
        
        service.performCheck(rule);
        
        // Should attempt to look up "unknown_primary", fail safely, then "unknown_secondary", fail safely.
        // Fetchers should NOT be called because the map lookup fails.
        verify(primaryFetcher, never()).fetchRate(any(), any());
        verify(secondaryFetcher, never()).fetchRate(any(), any());
        verify(thresholdJudger, never()).judge(any(), any());
    }
    
    @Test
    @DisplayName("Constructor handles empty fetcher list")
    void testConstructor_EmptyFetchers() {
        service = new ExchangeRateMonitorService(
                Collections.emptyList(),
                thresholdJudger,
                monitorConfig
        );
        
        ExchangeRateMonitorRule rule = createValidRule();
        service.performCheck(rule);
        
        verify(thresholdJudger, never()).judge(any(), any());
    }
}
