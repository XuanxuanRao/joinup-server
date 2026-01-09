package cn.org.joinup.course.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "iclass-crawler")
@Component
public class CrawlerConfig {
    private String channel;
    private List<String> appKeys;
}
