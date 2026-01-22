package cn.org.joinup.common.exception;

public class RateLimitException extends CommonException {

    public RateLimitException(String message) {
        super(message, 429);
    }

    public RateLimitException(String message, Throwable cause) {
        super(message, cause, 429);
    }
}
