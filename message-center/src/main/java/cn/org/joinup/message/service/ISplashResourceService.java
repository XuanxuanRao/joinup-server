package cn.org.joinup.message.service;

import cn.org.joinup.message.domain.dto.request.SplashResourceUpdateDTO;
import cn.org.joinup.message.domain.po.splash.SplashResource;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ISplashResourceService extends IService<SplashResource> {
    List<SplashResource> fetchAvailableResources();

    Page<SplashResource> listSplash(Integer pageNum, Integer pageSize, Boolean enabled, String platform);

    SplashResource updateSplashResource(Long resourceId, SplashResourceUpdateDTO updateDTO);
}
