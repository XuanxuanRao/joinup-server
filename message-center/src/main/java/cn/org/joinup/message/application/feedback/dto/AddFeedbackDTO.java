package cn.org.joinup.message.application.feedback.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
@ApiModel("用户提交反馈表单")
public class AddFeedbackDTO {
    @ApiModelProperty("反馈主题")
    private String subject;
    @ApiModelProperty("反馈内容")
    @NotNull
    private String content;
    @ApiModelProperty("联系方式")
    private String contact;
}
