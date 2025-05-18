package cn.org.joinup.team.domain.vo;

import cn.org.joinup.team.domain.po.BrowseHistory;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TeamBrowseVO extends BrowseHistory {

    private String creatorUserName;

    private String creatorAvatar;

    private String teamName;

    private String teamTheme;

}
