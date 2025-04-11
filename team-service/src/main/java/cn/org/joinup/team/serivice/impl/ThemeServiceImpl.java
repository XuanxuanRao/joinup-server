package cn.org.joinup.team.serivice.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.org.joinup.team.constants.RedisConstant;
import cn.org.joinup.team.domain.po.Theme;
import cn.org.joinup.team.mapper.ThemeMapper;
import cn.org.joinup.team.serivice.IThemeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
@RequiredArgsConstructor
public class ThemeServiceImpl extends ServiceImpl<ThemeMapper, Theme> implements IThemeService {
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public Theme getById(Serializable id) {
        final String key = RedisConstant.THEME_KEY + id;
        String themeJSON = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(themeJSON)) {
            return JSONUtil.toBean(themeJSON, Theme.class);
        }

        // 得到"", 表示缓存空对象，数据库不存在
        if (themeJSON != null) {
            return null;
        }

        Theme theme = super.getById(id);
        if (theme == null) {
            // 将空值写入 redis，防止缓存穿透
            stringRedisTemplate.opsForValue().set(key, "", cn.org.joinup.common.constant.RedisConstant.CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }

        stringRedisTemplate.opsForValue().set(RedisConstant.THEME_KEY + id, JSONUtil.toJsonStr(theme));
        return theme;
    }


}