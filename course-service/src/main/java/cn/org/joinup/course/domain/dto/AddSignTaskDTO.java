package cn.org.joinup.course.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
@ApiModel("添加自动签到任务表单")
public class AddSignTaskDTO {
    @ApiModelProperty("课程id")
    private Integer courseId;
}
