package cn.org.joinup.message.interfaces.vo;

import cn.org.joinup.message.domain.feedback.entity.Feedback;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chenxuanrao06@gmail.com
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FeedbackVO extends Feedback {
    private String username;
    private String avatar;
}
