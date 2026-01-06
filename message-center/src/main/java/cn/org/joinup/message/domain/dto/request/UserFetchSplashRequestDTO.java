package cn.org.joinup.message.domain.dto.request;

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
