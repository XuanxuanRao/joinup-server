package cn.org.joinup.message.service.impl.splash;

import cn.org.joinup.message.domain.po.splash.SplashResource;
import cn.org.joinup.message.mapper.SplashResourceMapper;
import cn.org.joinup.message.service.ISplashResourceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SplashResourceServiceImpl extends ServiceImpl<SplashResourceMapper, SplashResource> implements ISplashResourceService {

    @Override
    public List<SplashResource> fetchAvailableResources() {
        return lambdaQuery().eq(SplashResource::getEnabled, true)
                .eq(SplashResource::getDeleted, false)
                .list();
    }

}
