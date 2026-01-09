package cn.org.joinup.message.domain.po.splash;

import cn.org.joinup.message.enums.ClickAction;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("splash_resource")
@Data
@ApiModel("开屏页资源")
public class SplashResource implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String title;

    private String resourceUrl;

    private ClickAction clickAction;
    private String clickUrl;

    private Long duration;

    private Boolean enabled;

    private Boolean deleted;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
