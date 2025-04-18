package cn.org.joinup.team.serivice.impl;


import cn.org.joinup.team.domain.po.Theme;
import cn.org.joinup.team.mapper.ThemeMapper;
import cn.org.joinup.team.serivice.IAdminThemeService;
import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.data.domain.Pageable;   // 仅用来接收参数也行

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminThemeServiceImpl extends ServiceImpl<ThemeMapper, Theme> implements IAdminThemeService {
    // 这里可以添加一些特定于管理员的主题服务方法
    // 例如，获取所有主题、删除主题等

    // 分页获取主题
    @Override
    public IPage<Theme> getPageThemes(Pageable pageable) {
        Page<Theme> page = new Page<>(pageable.getPageNumber(), pageable.getPageSize());
        return this.page(page);
    }

    @Override
    public IPage<Theme> getPageThemesSearch(String name, Pageable pageable) {
        Page<Theme> page = new Page<>(pageable.getPageNumber(), pageable.getPageSize());

        QueryWrapper<Theme> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), "name", name);

        return this.baseMapper.selectPage(page, wrapper);
    }

}
