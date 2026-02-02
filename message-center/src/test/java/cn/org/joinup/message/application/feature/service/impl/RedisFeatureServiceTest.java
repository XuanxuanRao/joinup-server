package cn.org.joinup.message.application.feature.service.impl;

import cn.org.joinup.message.infrastructure.constant.RedisConstant;
import cn.org.joinup.message.application.feature.service.IFeatureDiscoveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RedisFeatureServiceTest {

    private RedisFeatureService featureService;

    @Mock
    private StringRedisTemplate stringRedisTemplate;
    
    @Mock
    private IFeatureDiscoveryService featureDiscoveryService;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private SetOperations<String, String> setOperations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(stringRedisTemplate.opsForSet()).thenReturn(setOperations);
        featureService = new RedisFeatureService(stringRedisTemplate, featureDiscoveryService);
    }

    @Test
    @DisplayName("Should return false when userId is null")
    void testCanAccess_NullUser() {
        assertFalse(featureService.canAccess("feature", null));
    }

    @Test
    @DisplayName("Should return true when feature is public")
    void testCanAccess_PublicFeature() {
        when(valueOperations.get(RedisConstant.FEATURE_PUBLIC_KEY_PREFIX + "feature")).thenReturn("true");
        assertTrue(featureService.canAccess("feature", 1L));
    }

    @Test
    @DisplayName("Should return true when user is in whitelist")
    void testCanAccess_WhitelistUser() {
        when(valueOperations.get(RedisConstant.FEATURE_PUBLIC_KEY_PREFIX + "feature")).thenReturn("false");
        when(setOperations.isMember(RedisConstant.FEATURE_WHITELIST_KEY_PREFIX + "feature", "1")).thenReturn(true);
        assertTrue(featureService.canAccess("feature", 1L));
    }

    @Test
    @DisplayName("Should return false when feature is not public and user not in whitelist")
    void testCanAccess_Denied() {
        when(valueOperations.get(RedisConstant.FEATURE_PUBLIC_KEY_PREFIX + "feature")).thenReturn("false");
        when(setOperations.isMember(RedisConstant.FEATURE_WHITELIST_KEY_PREFIX + "feature", "1")).thenReturn(false);
        assertFalse(featureService.canAccess("feature", 1L));
    }

    @Test
    @DisplayName("Should add users to whitelist")
    void testAddUsersToWhitelist() {
        Set<Long> userIds = Collections.singleton(1L);
        featureService.addUsersToWhitelist("feature", userIds);
        verify(setOperations).add(RedisConstant.FEATURE_WHITELIST_KEY_PREFIX + "feature", "1");
    }

    @Test
    @DisplayName("Should remove users from whitelist")
    void testRemoveUsersFromWhitelist() {
        Set<Long> userIds = Collections.singleton(1L);
        featureService.removeUsersFromWhitelist("feature", userIds);
        verify(setOperations).remove(RedisConstant.FEATURE_WHITELIST_KEY_PREFIX + "feature", "1");
    }

    @Test
    @DisplayName("Should set feature public status")
    void testSetFeaturePublic() {
        featureService.setFeaturePublic("feature", true);
        verify(valueOperations).set(RedisConstant.FEATURE_PUBLIC_KEY_PREFIX + "feature", "true");

        featureService.setFeaturePublic("feature", false);
        verify(stringRedisTemplate).delete(RedisConstant.FEATURE_PUBLIC_KEY_PREFIX + "feature");
    }
}
