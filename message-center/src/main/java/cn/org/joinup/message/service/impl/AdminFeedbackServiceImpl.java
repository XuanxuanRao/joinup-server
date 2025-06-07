package cn.org.joinup.message.service.impl;

import cn.org.joinup.message.domain.po.ChatMessage;
import cn.org.joinup.message.domain.po.Feedback;
import cn.org.joinup.message.mapper.FeedbackMapper;
import cn.org.joinup.message.service.IAdminFeedbackService;
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
public class AdminFeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements IAdminFeedbackService {

    @Override
    public IPage<Feedback> getPageFeedbacks(Pageable pageable) {
        Page<Feedback> page = new Page<>(pageable.getPageNumber(), pageable.getPageSize());

        QueryWrapper<Feedback> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time"); // 按创建时间倒序

        return this.page(page, wrapper);
        // return this.page(page);
    }

    @Override
    public IPage<Feedback> getPageFeedbacksSearch(String name, Pageable pageable) {
        Page<Feedback> page = new Page<>(pageable.getPageNumber(), pageable.getPageSize());

        QueryWrapper<Feedback> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), "content", name);

        wrapper.orderByDesc("create_time"); // 按创建时间倒序

        return this.baseMapper.selectPage(page, wrapper);
    }
}
