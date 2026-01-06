package cn.org.joinup.message.service;

import cn.org.joinup.message.domain.po.splash.SplashResource;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ISplashResourceService extends IService<SplashResource> {
    List<SplashResource> fetchAvailableResources();
}
