package cn.org.joinup.message.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author chenxuanrao06@gmail.com
 */
@ApiModel("添加公告表单")
@Data
public class AddAnnouncementDTO {
    @ApiModelProperty("公告标题")
    @Size(min = 2, max = 60, message = "标题长度在2-60个字符之间")
    private String title;
    @ApiModelProperty("公告内容")
    @NotNull
    private String content;
    @ApiModelProperty("封面图片的url")
    @NotNull
    private String cover;
}
