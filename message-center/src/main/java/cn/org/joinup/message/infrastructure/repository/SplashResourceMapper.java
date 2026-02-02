package cn.org.joinup.message.infrastructure.repository;

import cn.org.joinup.message.domain.splash.entity.SplashResource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SplashResourceMapper extends BaseMapper<SplashResource> {

    IPage<SplashResource> selectPageByPlatform(
            Page<SplashResource> page,
            @Param("enabled") Boolean enabled,
            @Param("platform") String platform
    );

}
