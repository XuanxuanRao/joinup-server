package cn.org.joinup.team.domain.vo;

import cn.org.joinup.team.domain.po.TeamMember;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chenxuanrao06@gmail.com
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamMemberVO extends TeamMember {
    private String userName;
    private String avatar;
}
