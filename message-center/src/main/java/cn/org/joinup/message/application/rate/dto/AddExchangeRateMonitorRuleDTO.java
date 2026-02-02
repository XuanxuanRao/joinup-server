package cn.org.joinup.message.application.rate.dto;

import cn.org.joinup.common.util.RegexUtil;
import cn.org.joinup.message.infrastructure.enums.CurrencyCode;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@Data
public class AddExchangeRateMonitorRuleDTO {
    @Pattern(regexp = RegexUtil.EMAIL_REGEX)
    private String email;
    @NotNull
    private CurrencyCode baseCurrency;
    @NotNull
    private CurrencyCode quoteCurrency;
    private BigDecimal absoluteUpper;
    private BigDecimal absoluteLower;
}
