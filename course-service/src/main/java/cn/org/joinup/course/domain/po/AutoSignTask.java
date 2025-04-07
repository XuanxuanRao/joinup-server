package cn.org.joinup.course.domain.po;

import cn.org.joinup.course.enums.SignTaskStatus;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 自动签到任务配置
 * @author chenxuanrao06@gmail.com
 */
@Data
@TableName("auto_sign_task")
public class AutoSignTask implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String studentId;
    private Integer courseId;

    private SignTaskStatus status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime lastSignTime;

}
