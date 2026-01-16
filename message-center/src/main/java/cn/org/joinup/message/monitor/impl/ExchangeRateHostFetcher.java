package cn.org.joinup.message.monitor.impl;

import cn.org.joinup.message.monitor.RateFetcher;
import cn.org.joinup.message.monitor.config.MonitorConfig;
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

    private final MonitorConfig monitorConfig;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ExchangeRateHostFetcher(MonitorConfig monitorConfig, ObjectMapper objectMapper) {
        this.monitorConfig = monitorConfig;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public Optional<ExchangeRate> fetchRate(String fromCurrency, String toCurrency) {
        String urlTemplate = monitorConfig.getDatasource()
                .getUrls()
                .get("exchangerate_host");
        if (urlTemplate == null) {
            // Fallback default if not configured
            urlTemplate = "https://v6.exchangerate-api.com/v6/%s/pair/%s/%s";
        }
        
        // Ensure the URL matches the requested currency if possible, or assume config is correct for now as per requirement
        // Ideally we should replace params if the URL template supports it.
        // For Phase 1, we focus on CNY/JPY as per requirement.
        
        String url = String.format(urlTemplate, monitorConfig.getDatasource().getPrimaryApiKey(), fromCurrency, toCurrency);

        return fetchWithRetry(url, 3, fromCurrency, toCurrency);
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

    @Override
    public String getSourceId() {
        return "exchangerate.host";
    }

}
