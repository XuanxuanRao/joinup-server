package cn.org.joinup.team.domain.po;

import cn.org.joinup.team.enums.TagApplicationStatus;
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
@TableName("tag_applications")
public class TagApplication implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 标签名
     */
    private String name;

    /**
     * 标签描述
     */
    private String description;

    /**
     * 申请用户id
     */
    private Long submitterUserId;

    /**
     * 申请状态，枚举值：
     * 0：待审批
     * 1：已通过
     * 2：已拒绝
     */
    private TagApplicationStatus status;

    /**
     * 审核意见
     */
    private String reviewerComment;

    /**
     * 审核完成时间
     */
    private LocalDateTime finishTime;

    /**
     * 创建时间 (申请提交时间)
     */
    private LocalDateTime createTime;
}