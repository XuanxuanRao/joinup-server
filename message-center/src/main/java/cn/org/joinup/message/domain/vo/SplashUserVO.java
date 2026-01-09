package cn.org.joinup.message.domain.vo;

import cn.org.joinup.message.enums.ClickAction;
import lombok.Data;

@Data
public class SplashUserVO {
    /**
     * 用于后续上报点击事件
     */
    private Long id;
    private String resourceUrl;
    private ClickAction clickAction;
    private String clickUrl;
    private Long duration;
}
