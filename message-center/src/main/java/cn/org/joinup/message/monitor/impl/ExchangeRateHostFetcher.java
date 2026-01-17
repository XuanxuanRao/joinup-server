package cn.org.joinup.message.monitor.impl;

import cn.org.joinup.message.monitor.RateFetcher;
import cn.org.joinup.message.config.ExchangeRateMonitorConfig;
import cn.org.joinup.message.monitor.domain.ExchangeRate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
public class ExchangeRateHostFetcher implements RateFetcher {

    private final ExchangeRateMonitorConfig monitorConfig;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ExchangeRateHostFetcher(ExchangeRateMonitorConfig monitorConfig, ObjectMapper objectMapper) {
        this.monitorConfig = monitorConfig;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public Optional<ExchangeRate> fetchRate(String fromCurrency, String toCurrency) {
        return fetchWithRetry(
                buildUrl(fromCurrency, toCurrency),
                monitorConfig.getRetryTimes(),
                fromCurrency,
                toCurrency);
    }

    private Optional<ExchangeRate> fetchWithRetry(String url, int maxRetries, String fromCurrency, String toCurrency) {
        int attempt = 0;
        long delay = 1000; // Initial delay 1s

        while (attempt < maxRetries) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(10))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return parseResponse(response.body(), fromCurrency, toCurrency);
                } else {
                    log.warn("[RateFetcher] Failed to fetch rate. Status: {}", response.statusCode());
                }
            } catch (IOException | InterruptedException e) {
                log.warn("[RateFetcher] Exception during fetch: {}", e.getMessage());
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
        
        log.error("[RateFetcher] Failed to fetch rate after {} attempts.", maxRetries);
        return Optional.empty();
    }

    private Optional<ExchangeRate> parseResponse(String body, String fromCurrency, String toCurrency) {
        try {
            JsonNode root = objectMapper.readTree(body);
            if (root.has("result") && root.get("result").asText().equals("success")
                    && root.has("base_code") && root.get("base_code").asText().equals(fromCurrency)
                    && root.has("target_code") && root.get("target_code").asText().equals(toCurrency)) {
                BigDecimal rate = new BigDecimal(root.get("conversion_rate").asText());

                return Optional.of(ExchangeRate.builder()
                        .fromCurrency(fromCurrency)
                        .toCurrency(toCurrency)
                        .rate(rate)
                        .timestamp(LocalDateTime.now())
                        .source(getSourceId())
                        .build());
            }
        } catch (Exception e) {
            log.error("[RateFetcher] Failed to parse response: {}", e.getMessage());
        }
        return Optional.empty();
    }

    private String buildUrl(String fromCurrency, String toCurrency) {
        // https://v6.exchangerate-api.com/v6/{apiKey}/pair/{fromCurrency}/{toCurrency}
        String urlTemplate = monitorConfig.getDatasource()
                .getUrls()
                .getOrDefault(getSourceId(), "https://v6.exchangerate-api.com/v6");
        urlTemplate = urlTemplate + "/%s/pair/%s/%s";

        return String.format(urlTemplate,
                monitorConfig.getDatasource().getApiKeys().get(getSourceId()), fromCurrency, toCurrency);
    }

    @Override
    public String getSourceId() {
        return "exchangerate";
    }

}
