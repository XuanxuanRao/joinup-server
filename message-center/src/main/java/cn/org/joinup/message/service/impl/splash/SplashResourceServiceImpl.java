package cn.org.joinup.message.service.impl.splash;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.common.exception.BadRequestException;
import cn.org.joinup.message.domain.dto.request.SplashResourceUpdateDTO;
import cn.org.joinup.message.domain.po.splash.SplashResource;
import cn.org.joinup.message.mapper.SplashResourceMapper;
import cn.org.joinup.message.service.ISplashResourceService;
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

}
