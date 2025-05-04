package cn.org.joinup.team.domain.vo;

import cn.org.joinup.team.domain.po.Team;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chenxuanrao06@gmail.com
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BriefTeamVO extends Team {
    private String creatorUserName;
    private String creatorAvatar;
}
