package cn.org.joinup.file.domain.po;

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
@TableName("files")
public class File implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文件名
     */
    private String name;
    /**
     * 文件大小(以字节为单位)
     */
    private Long size;
    /**
     * 存储在OSS的URL
     */
    private String url;
    private String md5;


    /**
     * 上传用户ID
     */
    private Long uploaderId;
    private LocalDateTime createTime;
}
