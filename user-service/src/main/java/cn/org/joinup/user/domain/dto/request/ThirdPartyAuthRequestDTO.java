package cn.org.joinup.user.domain.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 *
 * @author chenxuanrao06@gmail.com
 */
@Data
public class ThirdPartyAuthRequestDTO {
    @NotNull
    private String appUUID;
    @NotNull
    private String appKey;
    @NotNull
    private Long timestamp;
    @NotNull
    private String signature;
}
