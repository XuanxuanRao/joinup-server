package cn.org.joinup.message.domain.rate;

import cn.org.joinup.message.domain.rate.entity.ExchangeRateMonitorRule;
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
    private ExchangeRateMonitorRule monitorRuleSnapshot;
}
