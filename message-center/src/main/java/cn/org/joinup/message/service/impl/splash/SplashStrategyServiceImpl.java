package cn.org.joinup.message.service.impl.splash;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.message.domain.dto.request.splash.SplashStrategyCreateDTO;
import cn.org.joinup.message.domain.dto.request.splash.SplashStrategyUpdateDTO;
import cn.org.joinup.message.domain.po.splash.SplashStrategy;
import cn.org.joinup.message.mapper.SplashStrategyMapper;
import cn.org.joinup.message.service.ISplashStrategyService;
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

}
