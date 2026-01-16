package cn.org.joinup.message.monitor.impl;

import cn.org.joinup.message.monitor.RateFetcher;
import cn.org.joinup.message.monitor.config.MonitorConfig;
import cn.org.joinup.message.monitor.domain.ExchangeRate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
public class AlphaVantageFetcher implements RateFetcher {

    private final MonitorConfig monitorConfig;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AlphaVantageFetcher(MonitorConfig monitorConfig, ObjectMapper objectMapper) {
        this.monitorConfig = monitorConfig;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public Optional<ExchangeRate> fetchRate(String fromCurrency, String toCurrency) {
        // Alpha Vantage API Key
        String apiKey = monitorConfig.getDatasource().getApiKeys().get(getSourceId());
        if (!StringUtils.hasText(apiKey)) {
            log.warn("[AlphaVantageFetcher] API Key not configured. Skipping.");
            return Optional.empty();
        }

        return fetchWithRetry(
                monitorConfig.getDatasource()
                        .getUrls()
                        .getOrDefault(getSourceId(), "https://api.tatum.io/v4/data/rate/symbol"),
                monitorConfig.getRetryTimes(),
                fromCurrency,
                toCurrency);
    }

    private Optional<ExchangeRate> fetchWithRetry(String url, int maxRetries, String fromCurrency, String toCurrency) {
        int attempt = 0;
        long delay = 1000;

        while (attempt < maxRetries) {
            try {
                // request params: symbol=CNY&basePair=JPY
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url + "?symbol=" + fromCurrency + "&basePair=" + toCurrency))
                        .timeout(Duration.ofSeconds(10))
                        .header("X-API-Key", monitorConfig.getDatasource().getApiKeys().get(getSourceId()))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return parseResponse(response.body(), fromCurrency, toCurrency);
                } else {
                    log.warn("[AlphaVantageFetcher] Failed to fetch rate. Status: {}", response.statusCode());
                }
            } catch (IOException | InterruptedException e) {
                log.warn("[AlphaVantageFetcher] Exception during fetch: {}", e.getMessage());
            }

            attempt++;
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            delay *= 2;
        }
        
        log.error("[AlphaVantageFetcher] Failed to fetch rate after {} attempts.", maxRetries);
        return Optional.empty();
    }

    private Optional<ExchangeRate> parseResponse(String body, String fromCurrency, String toCurrency) {
        try {
            JsonNode root = objectMapper.readTree(body);
            if (root.has("value")) {
                BigDecimal rate = new BigDecimal(root.get("value").asText());
                return Optional.of(ExchangeRate.builder()
                        .fromCurrency(fromCurrency)
                        .toCurrency(toCurrency)
                        .rate(rate)
                        .timestamp(LocalDateTime.now())
                        .source(getSourceId())
                        .build());
            } else if (root.has("Note")) {
                // Rate limit exceeded
                log.warn("[AlphaVantageFetcher] Rate limit exceeded: {}", root.get("Note").asText());
            } else if (root.has("Error Message")) {
                log.warn("[AlphaVantageFetcher] API Error: {}", root.get("Error Message").asText());
            }
        } catch (Exception e) {
            log.error("[AlphaVantageFetcher] Failed to parse response: {}", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public String getSourceId() {
        return "alpha_vantage";
    }
}
