package cn.org.joinup.team.domain.po;

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
@TableName("themes")
public class Theme implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 主题名
     */
    private String name;

    /**
     * 主题描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

