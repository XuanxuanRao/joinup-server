package cn.org.joinup.message.domain.po;

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
@TableName("message_template")
public class MessageTemplate implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 模板编码
     */
    private String code;
    private String template;
    /**
     * 编码格式：TEXT, HTML, MARKDOWN
     */
    private String encoding;
    private String title;
    /**
     * 模板路径
     */
    private String resourcePath;
    private LocalDateTime createTime;
}
