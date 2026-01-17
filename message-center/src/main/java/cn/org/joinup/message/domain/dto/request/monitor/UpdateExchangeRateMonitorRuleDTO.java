package cn.org.joinup.message.domain.dto.request.monitor;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateExchangeRateMonitorRuleDTO {
    private Boolean active;
    private String email;
    private String baseCurrency;
    private String quoteCurrency;
    private BigDecimal absoluteUpper;
    private BigDecimal absoluteLower;
}
