package cn.org.joinup.message.domain.vo;

import cn.org.joinup.message.domain.po.Announcement;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chenxuanrao06@gmail.com
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AnnouncementVO extends Announcement {
    private String username;
    private String avatar;
}
