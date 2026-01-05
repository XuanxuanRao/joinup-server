package cn.org.joinup.user.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("app_application")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppApplication implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String appKey;

    private String contactEmail;

    private Long applicantUserId;

    private String status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
