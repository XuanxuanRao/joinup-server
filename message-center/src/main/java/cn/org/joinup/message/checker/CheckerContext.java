package cn.org.joinup.message.checker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckerContext {
    /**
     * 发送间隔
     */
    private Integer sendInterval;

    /**
     * 最大发送次数
     */
    private Integer sendMaxTimes;

    /**
     * 账号
     */
    private String account;
}
