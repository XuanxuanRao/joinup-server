package cn.org.joinup.common.constant;

/**
 * @author chenxuanrao06@gmail.com
 */
public interface RedisConstant {
    String VERIFY_CODE_PREFIX = "verify_code:";

    /**
     * 验证码过期时间 5min
     */
    Long VERIFY_CODE_EXPIRE = 5 * 60L;

    /**
     * 空对象缓存时间 2min
     */
    Long CACHE_NULL_TTL = 2L;

    String INTEREST_ID_PREFIX = "interest:id:";
    String INTEREST_LIST_PREFIX = "interest:list";
    String INTEREST_LIST_ALL = "interest:list:all";
}
