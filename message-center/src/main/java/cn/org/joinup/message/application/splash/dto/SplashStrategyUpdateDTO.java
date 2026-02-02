package cn.org.joinup.message.application.splash.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SplashStrategyUpdateDTO {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer priority;
    private List<String> targetPlatforms;
    private Boolean enabled;
}
