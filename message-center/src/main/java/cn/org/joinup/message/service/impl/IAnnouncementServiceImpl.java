package cn.org.joinup.message.service.impl;

import cn.org.joinup.message.domain.po.Announcement;
import cn.org.joinup.message.mapper.AnnouncementMapper;
import cn.org.joinup.message.service.IAnnouncementService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
public class IAnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements IAnnouncementService {
    @Override
    public List<Announcement> getBriefList() {
        return this.lambdaQuery()
                .eq(Announcement::getDeleted, false)
                .select(Announcement::getId, Announcement::getTitle, Announcement::getCreateTime)
                .orderByDesc(false, Announcement::getCreateTime)
                .list();
    }
}
