package cn.org.joinup.course.config;

import lombok.Data;

import java.util.List;

@Data
public class CrawlerConfig {
    private String channel;
    private List<String> appKeys;
}
