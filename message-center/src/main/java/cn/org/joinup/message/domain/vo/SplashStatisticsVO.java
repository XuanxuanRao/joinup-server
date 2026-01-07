package cn.org.joinup.message.domain.vo;

import cn.org.joinup.message.domain.po.splash.SplashStrategy;
import cn.org.joinup.message.enums.ClickAction;
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
