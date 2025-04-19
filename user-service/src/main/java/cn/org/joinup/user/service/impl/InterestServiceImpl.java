package cn.org.joinup.user.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.org.joinup.common.constant.RedisConstant;
import cn.org.joinup.user.domain.po.Interest;
import cn.org.joinup.user.mapper.InterestMapper;
import cn.org.joinup.user.service.IInterestService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
@RequiredArgsConstructor
public class InterestServiceImpl extends ServiceImpl<InterestMapper, Interest> implements IInterestService {
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public Interest getInterestById(Serializable id) {
        final String key = RedisConstant.INTEREST_ID_PREFIX + id;
        String interestJSON = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(interestJSON)) {
            return JSONUtil.toBean(interestJSON, Interest.class);
        }

        // 得到"", 表示缓存空对象，数据库不存在
        if (interestJSON != null) {
            return null;
        }

        Interest interest = super.getById(id);
        if (interest == null) {
            // 将空值写入 redis，防止缓存穿透
            stringRedisTemplate.opsForValue().set(key, "", RedisConstant.CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }

        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(interest));
        return interest;
    }

    @Override
    public List<Interest> getInterests() {
        return getInterests(null);
    }

    @Override
    public List<Interest> getInterests(Long parentId) {
        final String key = parentId == null
                ? RedisConstant.INTEREST_LIST_ALL
                : RedisConstant.INTEREST_LIST_PREFIX + parentId;

        String json = stringRedisTemplate.opsForValue().get(key);

        if (StrUtil.isNotBlank(json)) {
            return JSONUtil.toList(json, Interest.class);
        }

        if (json != null) {
            return new ArrayList<>();
        }

        List<Interest> interests;
        if (parentId == null) {
            interests = list();
        } else {
            interests = lambdaQuery().eq(Interest::getParentId, parentId).list();
        }

        if (interests.isEmpty()) {
            stringRedisTemplate.opsForValue().set(
                    key,
                    JSONUtil.toJsonStr(new ArrayList<>()),
                    RedisConstant.CACHE_NULL_TTL,
                    TimeUnit.MINUTES
            );
            return new ArrayList<>();
        }

        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(interests));

        return interests;

    }

    @Override
    public Boolean isLeaf(Long id) {
        return getInterestById(id) != null && lambdaQuery().eq(Interest::getParentId, id).count() == 0;
    }
}
