package cn.org.joinup.common.ratelimit.annotation;

import cn.org.joinup.common.ratelimit.enums.LimitType;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * Resource key (default: method signature)
     */
    String key() default "";

    /**
     * Time window in seconds
     */
    int time() default 60;

    /**
     * Max requests allowed in the time window
     */
    int count() default 100;

    /**
     * Limit strategy type
     */
    LimitType limitType() default LimitType.DEFAULT;
    
    /**
     * Error message when limit exceeded
     */
    String message() default "Too many requests, please try again later.";
}
