package cn.org.joinup.message.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("rate_threshold_event_log")
public class RateThresholdEventLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String eventId;

    private Long ruleId;

    private String currencyPair;

    private BigDecimal currentRate;

    private BigDecimal threshold;

    private String triggerType; // ABSOLUTE_UPPER, ABSOLUTE_LOWER

    private String dataSource;

    private String message;

    private LocalDateTime triggerTime;

    private LocalDateTime createTime;
}
