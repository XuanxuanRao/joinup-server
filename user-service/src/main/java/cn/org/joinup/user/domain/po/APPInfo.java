package cn.org.joinup.user.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 * @author chenxuanrao06@gmail.com
 */
@TableName("app_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class APPInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String appKey;

    private String appSecret;

    private Boolean enabled;

    private Boolean deleted;

    private Long tokenExpireMinutes;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
