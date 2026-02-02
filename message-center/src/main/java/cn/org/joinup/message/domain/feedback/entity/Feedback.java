package cn.org.joinup.message.domain.feedback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author chenxuanrao06@gmail.com
 */
@TableName("feedbacks")
@Data
public class Feedback implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String subject;
    private String content;
    private Boolean handled;

    /**
     * 联系方式
     */
    private String contact;

    private LocalDateTime createTime;
}
