package cn.org.joinup.team.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
@ApiModel("创建队伍表单")
public class CreateTeamDTO {
    @ApiModelProperty("队伍名称")
    @NotNull
    @Size(min = 1, max = 60, message = "队伍名称长度必须在1到60之间")
    private String name;

    @ApiModelProperty("队伍介绍")
    private String description;

    @ApiModelProperty("队伍主题")
    @NotNull
    private Long themeId;

    @ApiModelProperty("队伍是否公开")
    @NotNull
    private Boolean open;

    @ApiModelProperty("最大人数")
    @NotNull
    @Max(value = 100, message = "最大人数不能超过100")
    private Integer maxMembers;

    @ApiModelProperty("队伍标签")
    @Size(max = 10, message = "队伍标签数量不能超过10")
    private List<Integer> tagIds;
    
    @ApiModelProperty("封面")
    private String cover;
}
