package cn.org.joinup.user.domain.vo;

import cn.org.joinup.user.enums.ScanLoginStatus;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanLoginVO {
    private ScanLoginStatus status;
    private String token;
    private LocalDateTime expireAt;
    private String ipAddress;
    private String browser;
    private String os;
    private String device;
    private LocalDateTime requestTime;
}
