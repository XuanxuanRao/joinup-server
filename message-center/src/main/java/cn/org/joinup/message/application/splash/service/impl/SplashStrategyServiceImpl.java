package cn.org.joinup.message.application.splash.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.message.application.splash.dto.SplashStrategyCreateDTO;
import cn.org.joinup.message.application.splash.dto.SplashStrategyUpdateDTO;
import cn.org.joinup.message.domain.splash.entity.SplashStrategy;
import cn.org.joinup.message.infrastructure.repository.SplashStrategyMapper;
import cn.org.joinup.message.application.splash.service.ISplashStrategyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SplashStrategyServiceImpl extends ServiceImpl<SplashStrategyMapper, SplashStrategy> implements ISplashStrategyService {

    @Override
    public List<SplashStrategy> getActiveStrategies() {
        return lambdaQuery().eq(SplashStrategy::getEnabled, true)
                .eq(SplashStrategy::getDeleted, false)
                .le(SplashStrategy::getStartTime, LocalDateTime.now())
                .ge(SplashStrategy::getEndTime, LocalDateTime.now())
                .orderByDesc(SplashStrategy::getPriority)
                .list();
    }

    @Override
    public SplashStrategy updateSplashStrategy(Long strategyId, SplashStrategyUpdateDTO updateDTO) {
        SplashStrategy strategy = getById(strategyId);
        if (strategy == null) {
            log.error("splash strategy not found, strategyId: {}", strategyId);
            return null;
        }

        BeanUtil.copyProperties(updateDTO, strategy);
        updateById(strategy);
        return strategy;
    }

    @Override
    public SplashStrategy createSplashStrategy(SplashStrategyCreateDTO createStrategyDTO) {
        SplashStrategy strategy = BeanUtil.copyProperties(createStrategyDTO, SplashStrategy.class);
        strategy.setEnabled(true);
        strategy.setDeleted(false);
        strategy.setCreateTime(LocalDateTime.now());
        strategy.setUpdateTime(LocalDateTime.now());
        save(strategy);
        return strategy;
    }

    @Override
    public boolean markAsDeleted(Long strategyId) {
        SplashStrategy strategy = getById(strategyId);
        if (strategy == null) {
            log.error("splash strategy not found, strategyId: {}", strategyId);
            return false;
        }

        strategy.setDeleted(true);
        strategy.setUpdateTime(LocalDateTime.now());
        updateById(strategy);
        return true;
    }

}
