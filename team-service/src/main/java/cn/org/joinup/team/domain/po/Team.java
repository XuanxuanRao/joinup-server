package cn.org.joinup.team.domain.po;

import cn.org.joinup.team.enums.TeamStatus;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("teams")
public class Team implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
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
}