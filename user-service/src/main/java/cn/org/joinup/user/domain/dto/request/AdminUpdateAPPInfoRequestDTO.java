package cn.org.joinup.user.domain.dto.request;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class AdminUpdateAPPInfoRequestDTO {
    private Boolean enabled;
    @Range(min = 30, max = 48 * 60)
    private Long tokenExpireSeconds;
}
