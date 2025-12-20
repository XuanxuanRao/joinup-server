package cn.org.joinup.user.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author chenxuanrao06@gmail.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterThirdPartyUserDTO {
    private String appKey;
    private String appUUID;
    private String username;
}
