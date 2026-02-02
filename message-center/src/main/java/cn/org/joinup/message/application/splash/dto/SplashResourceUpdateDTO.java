package cn.org.joinup.message.application.splash.dto;

import cn.org.joinup.message.infrastructure.enums.ClickAction;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class SplashResourceUpdateDTO {
    @Length(min = 10, max = 100)
    private String title;
    private String resourceUrl;
    private ClickAction clickAction;
    private String clickUrl;
    private Long duration;
    private Boolean enabled;
}
