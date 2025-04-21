package cn.org.joinup.team.serivice.impl;

import cn.org.joinup.team.domain.po.Tag;
import cn.org.joinup.team.mapper.TagMapper;
import cn.org.joinup.team.serivice.IAdminTagService;
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
public class AdminTagServiceImpl extends ServiceImpl<TagMapper, Tag> implements IAdminTagService {

    @Override
    public IPage<Tag> getPageTags(Pageable pageable) {
        Page<Tag> page = new Page<>(pageable.getPageNumber(), pageable.getPageSize());
        return this.page(page);
    }

    @Override
    public IPage<Tag> getPageTagsSearch(String name, Pageable pageable) {
        Page<Tag> page = new Page<>(pageable.getPageNumber(), pageable.getPageSize());

        QueryWrapper<Tag> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), "name", name);

        return this.baseMapper.selectPage(page, wrapper);
    }

}