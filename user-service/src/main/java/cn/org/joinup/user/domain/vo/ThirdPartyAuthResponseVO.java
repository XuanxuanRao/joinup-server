package cn.org.joinup.user.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 *
 * @author chenxuanrao06@gmail.com
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ThirdPartyAuthResponseVO {
    private String token;
    private LocalDateTime expireAt;
}
