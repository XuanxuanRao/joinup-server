package cn.org.joinup.course.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
@AllArgsConstructor
@ApiModel("课程签到表单")
public class SignDTO {
    @ApiModelProperty("学号")
    private String studentId;
    @ApiModelProperty("本节课的课程id")
    private Integer courseScheduleId;
}
