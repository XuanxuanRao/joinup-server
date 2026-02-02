package cn.org.joinup.message.application.feature.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
public class FeatureWhitelistDTO {
    @NotEmpty
    private Set<Long> userIds;
}
