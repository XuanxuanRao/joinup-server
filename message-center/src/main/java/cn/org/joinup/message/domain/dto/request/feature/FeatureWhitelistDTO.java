package cn.org.joinup.message.domain.dto.request.feature;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
public class FeatureWhitelistDTO {
    @NotEmpty
    private Set<Long> userIds;
}
