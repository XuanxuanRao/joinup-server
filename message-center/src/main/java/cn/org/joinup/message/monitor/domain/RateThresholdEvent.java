package cn.org.joinup.message.monitor.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateThresholdEvent {
    private String eventId;
    private LocalDateTime timestamp;
    private String currencyPair;
    private BigDecimal currentRate;
    private BigDecimal threshold;
    private String triggerType; // ABSOLUTE_UPPER, ABSOLUTE_LOWER
    private String dataSource;
    private String message;
    private Long exchangeRateMonitorSubscriptionId;
}
