package cn.org.joinup.team.domain.po;

import cn.org.joinup.team.enums.TeamJoinApplicationStatus;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
@TableName("team_join_applications")
public class TeamJoinApplication implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long teamId;
    private Long userId;

    private TeamJoinApplicationStatus status;
    private String applicationMessage;
    private String reviewerComment;
    private LocalDateTime createTime;
    private LocalDateTime finishTime;
}
