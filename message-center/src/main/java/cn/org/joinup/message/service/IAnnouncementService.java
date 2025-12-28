package cn.org.joinup.message.service;

import cn.org.joinup.message.domain.po.Announcement;
import cn.org.joinup.message.domain.vo.AnnouncementVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IAnnouncementService extends IService<Announcement> {
    List<Announcement> getBriefList();

    AnnouncementVO getDetail(Long announcementId);
}
