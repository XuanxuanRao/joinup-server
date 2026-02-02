package cn.org.joinup.message.domain.rate.service;

import cn.org.joinup.message.domain.rate.ExchangeRate;
import java.util.Optional;

/**
 * abstract class to fetch exchange rate
 */
public interface RateFetcher {
    /**
     * fetch exchange rate
     * @param fromCurrency currency code from which to convert
     * @param toCurrency currency code to which to convert
     * @return exchange rate
     */
    Optional<ExchangeRate> fetchRate(String fromCurrency, String toCurrency);

    /**
     * specify source id of rate fetcher
     * @return source id
     */
    String getSourceId();
}
