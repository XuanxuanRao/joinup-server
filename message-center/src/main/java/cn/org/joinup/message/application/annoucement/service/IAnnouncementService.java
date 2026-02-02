package cn.org.joinup.message.application.annoucement.service;

import cn.org.joinup.message.domain.announcement.entity.Announcement;
import cn.org.joinup.message.interfaces.vo.AnnouncementVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IAnnouncementService extends IService<Announcement> {
    List<Announcement> getBriefList();

    AnnouncementVO getDetail(Long announcementId);
}
