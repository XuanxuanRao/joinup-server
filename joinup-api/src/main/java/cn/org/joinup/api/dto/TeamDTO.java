package cn.org.joinup.api.dto;

import cn.org.joinup.api.enums.TeamStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeamDTO {
    private Long id;

    /**
     * 队伍名
     */
    private String name;

    /**
     * 队伍描述
     */
    private String description;

    /**
     * 创建用户id
     */
    private Long creatorUserId;

    /**
     * 主题id (Nullable)
     */
    private Long themeId;

    /**
     * 当前队伍人数（包括创建者）
     * 冗余字段，方便查询
     */
    private Integer currentMembersCount;

    /**
     * 队伍状态，枚举值：
     * 0：已解散
     * 1：正常
     * 2：封禁
     */
    private TeamStatus status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否公开
     */
    private Boolean open;

    private Integer maxMembers;
}
