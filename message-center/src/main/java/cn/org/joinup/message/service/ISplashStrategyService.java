package cn.org.joinup.message.service;

import cn.org.joinup.message.domain.po.splash.SplashStrategy;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ISplashStrategyService extends IService<SplashStrategy> {
    List<SplashStrategy> getActiveStrategies();
}
