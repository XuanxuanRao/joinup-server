package cn.org.joinup.common.ratelimit.aspect;

import cn.hutool.extra.servlet.ServletUtil;
import cn.org.joinup.common.exception.RateLimitException;
import cn.org.joinup.common.ratelimit.annotation.RateLimit;
import cn.org.joinup.common.ratelimit.enums.LimitType;
import cn.org.joinup.common.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class RateLimitAspect {

    private final StringRedisTemplate stringRedisTemplate;
    
    // Lua script for sliding window rate limiting
    // ARGV[1]: limit count
    // ARGV[2]: window time in seconds
    private static final String LUA_SCRIPT = 
            "local key = KEYS[1]\n" +
            "local limit = tonumber(ARGV[1])\n" +
            "local window = tonumber(ARGV[2])\n" +
            "local current = redis.call('INCR', key)\n" +
            "if current == 1 then\n" +
            "    redis.call('EXPIRE', key, window)\n" +
            "end\n" +
            "return current";

    @Before("@annotation(rateLimit)")
    public void doBefore(JoinPoint point, RateLimit rateLimit) throws RateLimitException {
        int time = rateLimit.time();
        int count = rateLimit.count();

        String combineKey = getCombineKey(rateLimit, point);
        List<String> keys = Collections.singletonList(combineKey);
        
        try {
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(LUA_SCRIPT, Long.class);
            Long number = stringRedisTemplate.execute(redisScript, keys, String.valueOf(count), String.valueOf(time));
            
            if (number > count) {
                log.warn("Rate limit exceeded for key: {}, limit: {}, current: {}", combineKey, count, number);
                throw new RateLimitException(rateLimit.message());
            }
            log.debug("Rate limit check passed for key: {}, limit: {}, current: {}", combineKey, count, number);
        } catch (RateLimitException e) {
            throw e;
        } catch (Exception e) {
            log.error("Rate limit check failed", e);
            // In case of Redis error, we allow the request to proceed
        }
    }

    public String getCombineKey(RateLimit rateLimit, JoinPoint point) {
        StringBuilder stringBuffer = new StringBuilder(rateLimit.key());
        if (rateLimit.limitType() == LimitType.IP) {
            stringBuffer.append(getIpAddress()).append("-");
        } else if (rateLimit.limitType() == LimitType.USER) {
            Long userId = UserContext.getUserId();
            if (userId == null) {
                // Fallback to IP if user is not logged in
                stringBuffer.append(getIpAddress()).append("-");
            } else {
                stringBuffer.append(userId).append("-");
            }
        }
        
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> targetClass = method.getDeclaringClass();
        stringBuffer.append(targetClass.getName()).append("-").append(method.getName());
        
        return "rate_limit:" + stringBuffer;
    }

    private String getIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return ServletUtil.getClientIP(request);
            }
        } catch (Exception e) {
            // ignore
        }
        return "unknown";
    }
}
