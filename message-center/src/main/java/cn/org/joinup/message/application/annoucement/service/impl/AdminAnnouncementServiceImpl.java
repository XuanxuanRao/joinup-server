package cn.org.joinup.message.application.annoucement.service.impl;

import cn.org.joinup.message.domain.announcement.entity.Announcement;
import cn.org.joinup.message.infrastructure.repository.AnnouncementMapper;
import cn.org.joinup.message.application.annoucement.service.IAdminAnnouncementService;
import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements IAdminAnnouncementService {
    
    @Override
    public IPage<Announcement> getPageAnnouncements(Pageable pageable) {
        Page<Announcement> page = new Page<>(pageable.getPageNumber(), pageable.getPageSize());

        QueryWrapper<Announcement> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time"); // 按创建时间倒序

        return this.page(page, wrapper);
        // return this.page(page);
    }

    @Override
    public IPage<Announcement> getPageAnnouncementsSearch(String name, Pageable pageable) {
        Page<Announcement> page = new Page<>(pageable.getPageNumber(), pageable.getPageSize());

        QueryWrapper<Announcement> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), "name", name);

        wrapper.orderByDesc("update_time"); // 按创建时间倒序

        return this.baseMapper.selectPage(page, wrapper);
    }
}
