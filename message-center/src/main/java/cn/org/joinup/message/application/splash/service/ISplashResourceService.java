package cn.org.joinup.message.application.splash.service;

import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.message.application.splash.dto.SplashResourceCreateDTO;
import cn.org.joinup.message.application.splash.dto.SplashResourceUpdateDTO;
import cn.org.joinup.message.domain.splash.entity.SplashResource;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ISplashResourceService extends IService<SplashResource> {
    List<SplashResource> fetchAvailableResources();

    Page<SplashResource> listSplash(Integer pageNum, Integer pageSize, Boolean enabled, String platform);

    SplashResource updateSplashResource(Long resourceId, SplashResourceUpdateDTO updateDTO);

    SplashResource createSplashResource(SplashResourceCreateDTO resourceCreateDTO);

    PageResult<SplashResource> listSplashResource(Integer pageNum, Integer pageSize, String title, Boolean enabled);

    boolean markAsDeleted(Long resourceId);
}
