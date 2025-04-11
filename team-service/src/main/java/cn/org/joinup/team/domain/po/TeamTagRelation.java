package cn.org.joinup.team.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("team_tags")
public class TeamTagRelation implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 队伍ID (复合主键的一部分)
     */
    private Long teamId;

    /**
     * 标签ID (复合主键的一部分)
     */
    private Integer tagId;
}
