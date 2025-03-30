package cn.org.joinup.common.constant;

/**
 * @author chenxuanrao06@gmail.com
 * @Description:
 */
public class RedisConstant {
    public static final String VERIFY_CODE_PREFIX = "verify_code:";

    /**
     * 验证码过期时间 5min
     */
    public static final Long VERIFY_CODE_EXPIRE = 5 * 60L;

}
