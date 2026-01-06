package cn.org.joinup.message.service.impl.splash;

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

}
