package cn.org.joinup.user.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class APPInfoVO {
    private String appKey;
    private Boolean enabled;
    private Long tokenExpireMinutes;
    private LocalDateTime createTime;
    private Long usersCount;
}
