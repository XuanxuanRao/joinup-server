package cn.org.joinup.message.constant;

public interface RedisConstant {
    String CONVERSATION_PARTICIPANTS_KEY_PREFIX = "conversation:participants:";
    long CONVERSATION_PARTICIPANTS_TTL = 60 * 60;   // 1 hour
    String USER_CONVERSATIONS_KEY_PREFIX = "user:conversations:";
    long USER_CONVERSATIONS_TTL = 60 * 60;          // 1 hour
    String USER_CONVERSATION_UNREAD_MESSAGE_KEY_PREFIX = "user:conversation:message:unread:";
    String CONVERSATION_LAST_MESSAGE_KEY_PREFIX = "conversation:message:last:";
    String CONVERSATION_KEY_PREFIX = "conversation:";
    long CONVERSATION_TTL = 60 * 60;                // 1 hour
    long CACHE_NULL_TTL = 60; // 1 minute
}
