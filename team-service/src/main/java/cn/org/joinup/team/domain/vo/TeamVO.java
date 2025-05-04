package cn.org.joinup.team.domain.vo;

import cn.org.joinup.team.domain.po.Tag;
import cn.org.joinup.team.domain.po.Team;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author chenxuanrao06@gmail.com
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamVO extends Team {
    private String themeName;
    private List<Tag> tags;
    private List<TeamMemberVO> members;
}
