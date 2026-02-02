package cn.org.joinup.message.interfaces.vo;

import cn.org.joinup.message.domain.splash.entity.SplashStrategy;
import cn.org.joinup.message.infrastructure.enums.ClickAction;
import lombok.Data;

import java.util.List;

@Data
public class SplashStatisticsVO {
    private Long resourceId;
    private String title;
    private String resourceUrl;
    private ClickAction clickAction;
    private String clickUrl;
    private Long duration;
    private Boolean enabled;
    private List<SplashStrategy> strategies;
}
