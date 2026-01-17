package cn.org.joinup.message.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "exchange.monitor")
public class ExchangeRateMonitorConfig {
    private Integer retryTimes = 3;
    private String googleFinanceLink;
    private Datasource datasource = new Datasource();
    private Event event = new Event();

    @Data
    public static class Datasource {
        private String primary;
        private String secondary;
        private Map<String, String> urls = new HashMap<>();
        private Map<String, String> apiKeys = new HashMap<>();
    }

    @Data
    public static class Event {
        private boolean enabled = true;
        private String exchangeName = "exchange.rate.topic";
        private String routingKeyFormat = "rate.%s.%s.triggered";
        private int suppressDurationInMinutes = 60;
    }
}
