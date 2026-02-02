package cn.org.joinup.message.domain.message.entity;

import cn.org.joinup.message.infrastructure.enums.NotifyType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author chenxuanrao06@gmail.com
 */
@TableName("site_messages")
@Data
public class SiteMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String title;
    private String content;
    /**
     * 接收用户id（必须）
     */
    private Long receiverUserId;
    /**
     * 发送用户id（可选）
     */
    private Long senderUserId;
    private NotifyType notifyType;
    /**
     * 是否已读
     */
    @TableField("`read`")
    private Boolean read;
    private Boolean deleted;
    /**
     * 接收时间
     */
    private LocalDateTime createTime;
    /**
     * 阅读时间
     */
    private LocalDateTime readTime;
}
