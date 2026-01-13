package cn.org.joinup.message.domain.dto.request.splash;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SplashStrategyCreateDTO {
    @NotNull
    private Long resourceId;
    @NotNull
    private Integer priority;
    @NotNull
    private LocalDateTime startTime;
    @NotNull
    private LocalDateTime endTime;
    @NotNull
    @Size(min = 1, max = 20)
    private List<String> targetPlatforms;
}
