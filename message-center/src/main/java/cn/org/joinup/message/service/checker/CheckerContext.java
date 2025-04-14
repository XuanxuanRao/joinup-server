package cn.org.joinup.message.service.checker;

import lombok.Data;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
public class CheckerContext {
    /**
     * 发送间隔
     */
    private Integer sendInterval;

    /**
     * 最大发送次数
     */
    private Integer sendMaxTimes;
}
