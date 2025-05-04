package cn.org.joinup.gateway.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
public class LogEntry {
    private String path;
    private String method;
    private Long userId;
    private String ip;
    private int status;
    private long duration;
    private LocalDateTime createTime;
}
