package cn.org.joinup.team.domain.vo;

import cn.org.joinup.team.domain.po.TeamJoinApplication;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chenxuanrao06@gmail.com
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamJoinApplicationVO extends TeamJoinApplication {
    private String username;
    private String avatar;
}
