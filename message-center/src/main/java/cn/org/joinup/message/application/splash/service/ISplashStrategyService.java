package cn.org.joinup.message.application.splash.service;

import cn.org.joinup.message.application.splash.dto.SplashStrategyCreateDTO;
import cn.org.joinup.message.application.splash.dto.SplashStrategyUpdateDTO;
import cn.org.joinup.message.domain.splash.entity.SplashStrategy;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ISplashStrategyService extends IService<SplashStrategy> {
    List<SplashStrategy> getActiveStrategies();

    SplashStrategy updateSplashStrategy(Long strategyId, SplashStrategyUpdateDTO updateDTO);

    SplashStrategy createSplashStrategy(SplashStrategyCreateDTO createStrategyDTO);

    boolean markAsDeleted(Long strategyId);
}
