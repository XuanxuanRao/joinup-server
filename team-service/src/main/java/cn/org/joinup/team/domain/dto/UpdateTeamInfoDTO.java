package cn.org.joinup.team.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Size;

/**
 * @author chenxuanrao06@gmail.com
 */
@ApiModel("更新队伍信息表单")
@Data
public class UpdateTeamInfoDTO {
    @ApiModelProperty("新的队伍名称")
    @Size(min = 1, max = 60, message = "队伍名称长度必须在1到60之间")
    private String name;
    @ApiModelProperty("新的队伍介绍")
    private String description;
    @ApiModelProperty("队伍是否公开")
    private Boolean open;
    @ApiModelProperty("最大人数")
    @Max(value = 100, message = "最大人数不能超过100")
    private Integer maxMembers;
    @ApiModelProperty("新的队伍封面")
    private String cover;
    @ApiModelProperty("绑定的课程名")
    private String courseName;
}
