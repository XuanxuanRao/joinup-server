package cn.org.joinup.message.service.impl;

import cn.org.joinup.message.domain.po.Announcement;
import cn.org.joinup.message.mapper.AnnouncementMapper;
import cn.org.joinup.message.service.IAdminAnnouncementService;
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
        return this.page(page);
    }

    @Override
    public IPage<Announcement> getPageAnnouncementsSearch(String name, Pageable pageable) {
        Page<Announcement> page = new Page<>(pageable.getPageNumber(), pageable.getPageSize());

        QueryWrapper<Announcement> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), "name", name);

        return this.baseMapper.selectPage(page, wrapper);
    }
}
