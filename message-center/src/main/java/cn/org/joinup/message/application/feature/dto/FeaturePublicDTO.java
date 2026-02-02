package cn.org.joinup.message.application.feature.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class FeaturePublicDTO {
    @NotNull
    private Boolean isPublic;
}
