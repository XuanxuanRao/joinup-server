package cn.org.joinup.message.application.splash.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserFetchSplashRequestDTO {
    @NotNull
    private String platform;
    @NotNull
    private String deviceId;
    private String appVersion;
}
