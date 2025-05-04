package cn.org.joinup.user.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author chenxuanrao06@gmail.com
 */
@TableName("user_interests")
@Data
public class UserInterest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long userId;
    private Long interestId;
    private LocalDateTime createTime;
}
