package cn.org.joinup.message.monitor;

import cn.org.joinup.message.monitor.domain.ExchangeRate;
import java.util.Optional;

public interface RateFetcher {
    Optional<ExchangeRate> fetchRate(String fromCurrency, String toCurrency);
    String getSourceId();
}
