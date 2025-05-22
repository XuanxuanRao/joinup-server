package cn.org.joinup.user.constant;

public interface RedisConstant {
    String USER_INFO_KEY_PREFIX = "user:info:";
    long USER_INFO_TTL = 60 * 60;   // 1 hour
}
