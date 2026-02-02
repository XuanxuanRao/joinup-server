package cn.org.joinup.message.domain.message.model;

import cn.org.joinup.common.util.RegexUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author chenxuanrao06@gmail.com
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class EmailSendModel extends SendMessageModel {
    @Pattern(regexp = RegexUtil.EMAIL_REGEX, message = "邮箱格式错误")
    private String email;
    @NotNull
    private String subject;
}
