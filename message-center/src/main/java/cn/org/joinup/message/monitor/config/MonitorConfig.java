package cn.org.joinup.message.monitor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "exchange.monitor")
public class MonitorConfig {
    private Integer retryTimes;
    private Datasource datasource = new Datasource();
    private Thresholds thresholds = new Thresholds();
    private Event event = new Event();

    @Data
    public static class Datasource {
        private String primary;
        private String secondary;
        private Map<String, String> urls = new HashMap<>();
        private Map<String, String> apiKeys = new HashMap<>();
    }

    @Data
    public static class Thresholds {
        private BigDecimal absoluteUpper = new BigDecimal("21.50");
        private BigDecimal absoluteLower = new BigDecimal("20.00");
        private BigDecimal hysteresisMargin = new BigDecimal("0.02");
        private BigDecimal relativeIncreasePercentage = new BigDecimal("0.5");
    }

    @Data
    public static class Event {
        private boolean enabled = true;
        private String exchangeName = "exchange.rate.topic";
        private String routingKey = "rate.cnyjpy.triggered";
        private int suppressDurationInMinutes = 60;
    }
}
