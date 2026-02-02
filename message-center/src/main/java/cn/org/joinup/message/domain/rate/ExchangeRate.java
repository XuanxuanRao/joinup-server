package cn.org.joinup.message.domain.rate;

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
public class ExchangeRate {
    private String fromCurrency;
    private String toCurrency;
    private BigDecimal rate;
    private LocalDateTime timestamp;
    private String source;
}
