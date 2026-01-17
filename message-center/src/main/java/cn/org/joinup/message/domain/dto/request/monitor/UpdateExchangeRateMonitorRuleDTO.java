package cn.org.joinup.message.domain.dto.request.monitor;

import cn.org.joinup.message.enums.CurrencyCode;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateExchangeRateMonitorRuleDTO {
    private Boolean active;
    private String email;
    private CurrencyCode baseCurrency;
    private CurrencyCode quoteCurrency;
    private BigDecimal absoluteUpper;
    private BigDecimal absoluteLower;
}
