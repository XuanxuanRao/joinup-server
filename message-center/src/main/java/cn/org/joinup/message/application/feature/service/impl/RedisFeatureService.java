package cn.org.joinup.message.application.feature.service.impl;

import cn.org.joinup.message.infrastructure.constant.RedisConstant;
import cn.org.joinup.message.interfaces.vo.FeatureVO;
import cn.org.joinup.message.application.feature.service.IFeatureDiscoveryService;
import cn.org.joinup.message.application.feature.service.IFeatureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Primary
@Slf4j
@RequiredArgsConstructor
public class RedisFeatureService implements IFeatureService {

    private final StringRedisTemplate stringRedisTemplate;
    private final IFeatureDiscoveryService featureDiscoveryService;

    @Override
    public List<FeatureVO> listFeatures() {
        Set<String> featureNames = featureDiscoveryService.discoverFeatures();
        return featureNames.stream()
                .map(featureName -> new FeatureVO(
                        featureName,
                        isFeaturePublic(featureName),
                        getWhitelist(featureName).size()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean canAccess(String featureName, Long userId) {
        if (userId == null) {
            return false;
        }

        // 1. Check if the feature is public for everyone
        if (isFeaturePublic(featureName)) {
            return true;
        }

        // 2. Check if the user is in the whitelist set
        String whitelistKey = RedisConstant.FEATURE_WHITELIST_KEY_PREFIX + featureName;
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(whitelistKey, String.valueOf(userId));

        if (Boolean.TRUE.equals(isMember)) {
            return true;
        }
        
        log.debug("Access denied for user: {} to feature: {}", userId, featureName);
        return false;
    }

    @Override
    public void addUsersToWhitelist(String featureName, Set<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        String whitelistKey = RedisConstant.FEATURE_WHITELIST_KEY_PREFIX + featureName;
        String[] userIdStrings = userIds.stream().map(String::valueOf).toArray(String[]::new);
        stringRedisTemplate.opsForSet().add(whitelistKey, userIdStrings);
    }

    @Override
    public void removeUsersFromWhitelist(String featureName, Set<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        String whitelistKey = RedisConstant.FEATURE_WHITELIST_KEY_PREFIX + featureName;
        String[] userIdStrings = userIds.stream().map(String::valueOf).toArray(String[]::new);
        stringRedisTemplate.opsForSet().remove(whitelistKey, (Object[]) userIdStrings);
    }

    @Override
    public Set<Long> getWhitelist(String featureName) {
        String whitelistKey = RedisConstant.FEATURE_WHITELIST_KEY_PREFIX + featureName;
        Set<String> members = stringRedisTemplate.opsForSet().members(whitelistKey);
        if (members == null) {
            return Collections.emptySet();
        }
        return members.stream().map(Long::valueOf).collect(Collectors.toSet());
    }

    @Override
    public void setFeaturePublic(String featureName, boolean isPublic) {
        String publicKey = RedisConstant.FEATURE_PUBLIC_KEY_PREFIX + featureName;
        if (isPublic) {
            stringRedisTemplate.opsForValue().set(publicKey, "true");
        } else {
            stringRedisTemplate.delete(publicKey);
        }
    }

    @Override
    public boolean isFeaturePublic(String featureName) {
        String publicKey = RedisConstant.FEATURE_PUBLIC_KEY_PREFIX + featureName;
        String publicValue = stringRedisTemplate.opsForValue().get(publicKey);
        return "true".equalsIgnoreCase(publicValue);
    }
}
