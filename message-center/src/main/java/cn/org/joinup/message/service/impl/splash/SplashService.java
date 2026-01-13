package cn.org.joinup.message.service.impl.splash;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.message.domain.dto.request.splash.UserFetchSplashRequestDTO;
import cn.org.joinup.message.domain.po.splash.SplashResource;
import cn.org.joinup.message.domain.po.splash.SplashStrategy;
import cn.org.joinup.message.domain.vo.SplashStatisticsVO;
import cn.org.joinup.message.domain.vo.SplashUserVO;
import cn.org.joinup.message.service.ISplashResourceService;
import cn.org.joinup.message.service.ISplashStrategyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SplashService {

    private final ISplashResourceService splashResourceService;
    private final ISplashStrategyService splashStrategyService;

    public SplashUserVO fetchSplash(UserFetchSplashRequestDTO fetchSplashDTO) {
        List<SplashStrategy> strategies = splashStrategyService.getActiveStrategies();
        log.info("fetch splash strategies: {}", strategies);
        SplashResource splashResource = strategies.stream()
                .filter(strategy -> strategy.getTargetPlatforms().contains(fetchSplashDTO.getPlatform()))
                .findFirst()
                .map(strategy -> splashResourceService.getById(strategy.getResourceId()))
                .orElse(null);

        if (splashResource == null) {
            var availableResources = splashResourceService.fetchAvailableResources();
            if (availableResources.isEmpty()) {
                log.warn("No available splash resource");
                return null;
            }
            splashResource = availableResources.get(0);
        }

        return BeanUtil.copyProperties(splashResource, SplashUserVO.class);
    }

    public PageResult<SplashStatisticsVO> listSplashStatistics(Integer pageNum, Integer pageSize, Boolean enabled, String platform) {
        var splashResources = splashResourceService.listSplash(pageNum, pageSize, enabled, platform);
        return PageResult.of(splashResources, (splashResource) -> {
                var vo = BeanUtil.copyProperties(splashResource, SplashStatisticsVO.class);
                vo.setResourceId(splashResource.getId());
                vo.setStrategies(splashStrategyService.lambdaQuery().eq(SplashStrategy::getResourceId, splashResource.getId())
                        .eq(SplashStrategy::getDeleted, false)
                        .list());
                return vo;
        });
    }
}
