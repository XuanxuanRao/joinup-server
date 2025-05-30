package cn.org.joinup.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserJoinTeamDTO {
    private Long teamId;
    private Long userId;
    private LocalDateTime createTime;
}
