package cn.org.joinup.message.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author chenxuanrao06@gmail.com
 */
@Component
@Data
@ConfigurationProperties(prefix = "joinup.message.verify")
public class VerifyProperties {
    /**
     * 发送间隔(s)
     */
    private Integer sendInterval;
    /**
     * 每天最大发送次数
     */
    private Integer sendMaxTimes;
}
