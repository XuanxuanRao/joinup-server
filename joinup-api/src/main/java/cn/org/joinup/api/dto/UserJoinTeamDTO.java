package cn.org.joinup.api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
@Builder
public class UserJoinTeamDTO {
    private Long teamId;
    private Long userId;
    private LocalDateTime createTime;
}
