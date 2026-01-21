package cn.org.joinup.message.domain.dto.request.feature;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class FeaturePublicDTO {
    @NotNull
    private Boolean isPublic;
}
