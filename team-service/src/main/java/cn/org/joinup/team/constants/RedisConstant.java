package cn.org.joinup.team.constants;

/**
 * @author chenxuanrao06@gmail.com
 */
public interface RedisConstant {
    String THEME_KEY = "team:theme:";
    String JOIN_TEAM_KEY_PREFIX = "team:join:";
    String CREATE_TEAM_KEY_PREFIX = "team:create:";

    /**
     * 缓存过期时间：12h
     */
    Long CACHE_TTL = 12 * 60 * 60L;
}
