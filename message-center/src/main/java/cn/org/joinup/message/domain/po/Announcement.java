package cn.org.joinup.message.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author chenxuanrao06@gmail.com
 */
@TableName("announcements")
@Data
@ApiModel("系统公告")
public class Announcement implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("公告标题")
    private String title;
    @ApiModelProperty("公告内容")
    private String content;
    @ApiModelProperty("发布用户id")
    private Long posterUserId;
    @ApiModelProperty("封面图片的url")
    private String cover;
    @ApiModelProperty("发布时间")
    private LocalDateTime createTime;
    @ApiModelProperty("最后修改时间")
    private LocalDateTime updateTime;
    @ApiModelProperty("是否已删除")
    private Boolean deleted;

    @ApiModelProperty("目标用户类型")
    private String targetUserType;
    @ApiModelProperty("目标 app")
    private List<String> targetAppKeys;
}
