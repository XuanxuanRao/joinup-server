package cn.org.joinup.message.application.splash.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.org.joinup.common.exception.BadRequestException;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.message.application.splash.dto.SplashResourceCreateDTO;
import cn.org.joinup.message.application.splash.dto.SplashResourceUpdateDTO;
import cn.org.joinup.message.domain.splash.entity.SplashResource;
import cn.org.joinup.message.infrastructure.repository.SplashResourceMapper;
import cn.org.joinup.message.application.splash.service.ISplashResourceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SplashResourceServiceImpl extends ServiceImpl<SplashResourceMapper, SplashResource> implements ISplashResourceService {

    private final SplashResourceMapper splashResourceMapper;

    @Override
    public List<SplashResource> fetchAvailableResources() {
        return lambdaQuery().eq(SplashResource::getEnabled, true)
                .eq(SplashResource::getDeleted, false)
                .list();
    }

    @Override
    public Page<SplashResource> listSplash(Integer pageNum, Integer pageSize, Boolean enabled, String platform) {
        IPage<SplashResource> splashResourceIPage = splashResourceMapper.selectPageByPlatform(
                new Page<>(pageNum, pageSize),
                enabled,
                platform
        );

        Page<SplashResource> result = new Page<>();
        result.setRecords(splashResourceIPage.getRecords());
        result.setTotal(splashResourceIPage.getTotal());
        result.setSize(splashResourceIPage.getSize());
        result.setCurrent(splashResourceIPage.getCurrent());
        result.setPages(splashResourceIPage.getPages());
        return result;
    }

    @Override
    public SplashResource updateSplashResource(Long resourceId, SplashResourceUpdateDTO updateDTO) {
        SplashResource splashResource = getById(resourceId);
        if (splashResource == null) {
            log.warn("SplashResource not found, resourceId: {}", resourceId);
            throw new BadRequestException("SplashResource not found, resourceId: " + resourceId);
        }

        BeanUtil.copyProperties(updateDTO, splashResource);
        splashResource.setUpdateTime(LocalDateTime.now());
        updateById(splashResource);
        return splashResource;
    }

    @Override
    public SplashResource createSplashResource(SplashResourceCreateDTO resourceCreateDTO) {
        SplashResource splashResource = BeanUtil.copyProperties(resourceCreateDTO, SplashResource.class);
        splashResource.setEnabled(true);
        splashResource.setDeleted(false);
        splashResource.setCreateTime(LocalDateTime.now());
        splashResource.setUpdateTime(LocalDateTime.now());
        save(splashResource);
        return splashResource;
    }

    @Override
    public PageResult<SplashResource> listSplashResource(Integer pageNum, Integer pageSize, String title, Boolean enabled) {
        Page<SplashResource> splashResourceIPage = page(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<SplashResource>()
                        .like(StrUtil.isNotBlank(title), SplashResource::getTitle, title)
                        .eq(enabled != null, SplashResource::getEnabled, enabled)
                        .eq(SplashResource::getDeleted, false)
                        .orderByDesc(SplashResource::getCreateTime)
        );

        return PageResult.of(splashResourceIPage);
    }

    @Override
    public boolean markAsDeleted(Long resourceId) {
        SplashResource splashResource = getById(resourceId);
        if (splashResource == null) {
            log.warn("SplashResource not found, resourceId: {}", resourceId);
            return false;
        }

        splashResource.setDeleted(true);
        splashResource.setUpdateTime(LocalDateTime.now());
        updateById(splashResource);
        return true;
    }

}
