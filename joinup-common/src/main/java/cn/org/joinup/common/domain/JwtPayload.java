package cn.org.joinup.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtPayload {
    private Long userId;
    private String role;
    private String appKey;
}
