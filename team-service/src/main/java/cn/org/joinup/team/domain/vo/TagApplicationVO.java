package cn.org.joinup.team.domain.vo;

import cn.org.joinup.team.domain.po.TagApplication;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chenxuanrao06@gmail.com
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TagApplicationVO extends TagApplication {
    private String submitterUserName;
    private String submitterUserAvatar;
}