package cn.org.joinup.message.domain.splash.entity;

import cn.org.joinup.common.handler.StringArrayTypeHandler;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@TableName("splash_strategy")
@Data
@ApiModel("投放策略")
public class SplashStrategy implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联的资源id
     */
    private Long resourceId;

    /**
     * 生效时间
     */
    private LocalDateTime startTime;
     /**
     * 失效时间
     */
    private LocalDateTime endTime;

    private Integer priority;

    @TableField(typeHandler = StringArrayTypeHandler.class)
    private List<String> targetPlatforms;

    private Boolean enabled;
    private Boolean deleted;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
