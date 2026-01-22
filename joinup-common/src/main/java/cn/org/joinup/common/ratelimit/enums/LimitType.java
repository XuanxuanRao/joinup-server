package cn.org.joinup.common.ratelimit.enums;

public enum LimitType {
    /**
     * Default (Global limit for the interface)
     */
    DEFAULT,

    /**
     * Limit by IP address
     */
    IP,

    /**
     * Limit by User ID (requires login)
     */
    USER,

    /**
     * Custom key (SpEL support)
     */
    CUSTOM
}
