package cn.org.joinup.message.application.splash.dto;

import cn.org.joinup.message.infrastructure.enums.ClickAction;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Data
public class SplashResourceCreateDTO {
    @Length(min = 10, max = 100)
    private String title;
    @NotNull
    private String resourceUrl;
    @NotNull
    private ClickAction clickAction;
    private String clickUrl;
    @Range(min = 1000, max = 10000)
    private Long duration;
}
