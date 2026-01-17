package cn.org.joinup.message.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName(value = "exchange_rate_monitor_rule", autoResultMap = true)
public class ExchangeRateMonitorRule implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String email;

    private String baseCurrency;

    private String quoteCurrency;

    @TableField(value = "thresholds", typeHandler = JacksonTypeHandler.class)
    private Thresholds thresholds;

    @TableField(value = "active")
    private Boolean active;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Thresholds {
        private BigDecimal absoluteUpper = new BigDecimal("22.50");
        private BigDecimal absoluteLower = new BigDecimal("20.00");
        private BigDecimal hysteresisMargin = new BigDecimal("0.02");
        private BigDecimal relativeIncreasePercentage = new BigDecimal("0.5");
    }

}


