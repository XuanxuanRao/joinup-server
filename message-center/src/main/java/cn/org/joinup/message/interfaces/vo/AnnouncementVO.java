package cn.org.joinup.message.interfaces.vo;

import cn.org.joinup.message.domain.announcement.entity.Announcement;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chenxuanrao06@gmail.com
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AnnouncementVO extends Announcement {
    private String posterUsername;
    private String posterAvatar;
}
