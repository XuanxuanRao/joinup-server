package cn.org.joinup.message.domain.dto.request.monitor;

import cn.org.joinup.common.util.RegexUtil;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@Data
public class AddExchangeRateMonitorRuleDTO {
    @Pattern(regexp = RegexUtil.EMAIL_REGEX)
    private String email;
    @NotNull
    private String baseCurrency;
    @NotNull
    private String quoteCurrency;
    private BigDecimal absoluteUpper;
    private BigDecimal absoluteLower;
}
