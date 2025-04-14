package cn.org.joinup.team.domain.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
@ApiModel("加入队伍请求")
public class JoinTeamDTO {
    private String applicationMessage;
}
