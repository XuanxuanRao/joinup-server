package cn.org.joinup.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("用户队伍统计信息")
public class UserTeamStatisticDTO {
    @ApiModelProperty("加入的队伍数量")
    private Integer joinedTeamCount;
    @ApiModelProperty("创建的队伍数量")
    private Integer createdTeamCount;
}
