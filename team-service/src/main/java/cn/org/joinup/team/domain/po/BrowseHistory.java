package cn.org.joinup.team.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("browse_history")
public class BrowseHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    /*
    * 主键id
    * */
    @TableId(value = "id", type = IdType.AUTO)
    private long id;

    /*
    * 用户id
    * */
    private long userId;

    /*
    * 队伍id
    * */
    private long teamId;

    /*
    * 浏览时间
    * */
    private LocalDateTime createTime;
}
