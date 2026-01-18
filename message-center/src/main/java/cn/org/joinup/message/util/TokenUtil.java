package cn.org.joinup.message.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author chenxuanrao06@gmail.com
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TokenUtil {

    private final StringRedisTemplate stringRedisTemplate;
    private static final String TOKEN_KEY_PREFIX = "message-center:token:";

    public String generateToken(String businessCode, Map<String, String> params, Long expireSeconds) {
        String token = java.util.UUID.randomUUID().toString().replace("-", "");
        String key = TOKEN_KEY_PREFIX + businessCode + ":" + token;
        StringBuilder valueBuilder = new StringBuilder();
        params.forEach((k, v) -> valueBuilder.append(k).append("=").append(v).append("&"));
        if (valueBuilder.length() > 0) {
            valueBuilder.setLength(valueBuilder.length() - 1); // Remove trailing '&'
        }
        stringRedisTemplate.opsForValue().set(key, valueBuilder.toString(), expireSeconds, TimeUnit.SECONDS);
        return token;
    }

    public Map<String, String> validateAndConsumeToken(String businessCode, String token) {
        String key = TOKEN_KEY_PREFIX + businessCode + ":" + token;
        String value = stringRedisTemplate.opsForValue().get(key);
        if (value == null) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        // Consume the token
        stringRedisTemplate.delete(key);
        // Parse the value into a map, handling malformed segments safely
        return java.util.Arrays.stream(value.split("&"))
                .map(part -> {
                    if (part == null || part.isEmpty()) {
                        return null;
                    }
                    int idx = part.indexOf('=');
                    if (idx < 0) {
                        log.warn("Ignoring malformed token segment without '=': {}", part);
                        return null;
                    }
                    String k = part.substring(0, idx);
                    if (k.isEmpty()) {
                        log.warn("Ignoring malformed token segment with empty key: {}", part);
                        return null;
                    }
                    String v = (idx == part.length() - 1) ? "" : part.substring(idx + 1);
                    return new String[]{k, v};
                })
                .filter(kv -> kv != null)
                .collect(java.util.stream.Collectors.toMap(kv -> kv[0], kv -> kv[1]));
    }

}
