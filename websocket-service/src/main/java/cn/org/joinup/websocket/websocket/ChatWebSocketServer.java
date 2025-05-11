package cn.org.joinup.websocket.websocket;

import cn.org.joinup.websocket.config.ChatEndpointConfigurator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chenxuanrao06@gmail.com
 */
@Slf4j
@Component
@ServerEndpoint(value = "/chat", configurator = ChatEndpointConfigurator.class)
public class ChatWebSocketServer {
    // 维护用户ID与Session的映射，用于点对点发送和广播
    private static final Map<Long, Session> SESSION_MAP = new ConcurrentHashMap<>();


    /**
     * 连接建立成功调用的方法
     * @param session 当前连接的 Session 对象
     * @param config  端点配置对象，包含了 Configurator 传递的用户属性
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        log.info("WebSocket Handshake: @OnOpen called. Session ID: {}", session.getId());

        try {
            // 1. 从 session 的用户属性中获取 Configurator 传递的用户信息或认证错误
            Map<String, Object> userProperties = config.getUserProperties();

            // 检查 Configurator 中是否发生了认证错误或未成功设置用户信息
            if (userProperties.containsKey("authError") || !userProperties.containsKey("userId")) {
                String errorMsg = (String) userProperties.getOrDefault("authError", "Missing user info");
                log.warn("WebSocket连接失败：握手阶段认证失败 - {}", errorMsg);
                try {
                    session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Authentication failed: " + errorMsg));
                } catch (IOException ex) {
                    log.error("关闭WebSocket连接失败", ex);
                }
                return;
            }

            Long userId = (Long) userProperties.get("userId");

            log.info("WebSocket Handshake: User ID: {}", userId);

            // 2. 认证成功，将用户ID与Session关联
            SESSION_MAP.put(userId, session);
            // 也将用户信息存储到 session 的用户属性中，方便在其他生命周期方法中访问
            session.getUserProperties().put("userId", userId);

            log.info("用户 {} 连接成功. Session ID: {}. 当前在线用户数：{}", userId, session.getId(), SESSION_MAP.size());

            sendTextMessage(session, "Welcome, " + userId);

        } catch (Exception e) {
            log.error("WebSocket @OnOpen 发生异常", e);
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, "Internal server error"));
            } catch (IOException ex) {
                log.error("关闭WebSocket连接失败", ex);
            }
        }
    }

    /**
     * 连接关闭调用的方法
     * @param session 当前关闭的 Session 对象
     */
    @OnClose
    public void onClose(Session session) {
        Long userId = (Long) session.getUserProperties().get("userId"); // 从 Session 属性获取用户 ID
        if (userId != null) {
            SESSION_MAP.remove(userId); // 从映射中移除 Session
            log.info("用户 {} 断开连接. Session ID: {}. 当前在线用户数：{}", userId, session.getId(), SESSION_MAP.size());
        } else {
            // 如果用户 ID 为 null，说明可能在 @OnOpen 认证前或认证失败时连接就关闭了
            log.info("未知用户断开连接. Session ID: {}", session.getId());
        }
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息 (String 或 byte[])
     * @param session 发送消息的 Session 对象
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        // 从 session 的用户属性中获取用户 ID 和角色
        Long userId = (Long) session.getUserProperties().get("userId");

        if (userId != null) {
            log.info("收到用户 {} 的消息：{}", userId, message);
            // 示例：简单回显消息给发送者
            // todo: 处理消息逻辑
            sendTextMessage(session, "Echo from server: " + message);
        } else {
            // 理论上，如果 @OnOpen 中认证失败，连接应该已经被关闭。如果这里收到消息，说明有未认证的 session 仍在尝试通信。
            log.warn("收到来自未认证 Session ({}) 的消息：{}", session.getId(), message);
            // 可以选择忽略或关闭连接
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Unauthenticated session message"));
            } catch (IOException e) {
                log.error("关闭未认证 Session 失败", e);
            }
        }
    }

    /**
     * 连接发生错误时调用的方法
     * @param session 发生错误的 Session 对象
     * @param error   发生的错误
     */
    @OnError
    public void onError(Session session, Throwable error) {
        Long userId = (Long) session.getUserProperties().get("userId");
        if (userId != null) {
            log.error("用户 {} 的 WebSocket 发生错误. Session ID: {}", userId, session.getId(), error);
            SESSION_MAP.remove(userId);
        } else {
            log.error("未知用户的 WebSocket 发生错误. Session ID: {}", session.getId(), error);
        }
    }

    /**
     * 向单个 Session 发送文本消息 (私有方法，通常在当前 Endpoint 实例内部调用)
     * @param session 要发送消息的 Session
     * @param message 消息内容
     */
    private void sendTextMessage(Session session, String message) {
        if (session != null && session.isOpen()) {
            try {
                // BasicRemote 是同步发送
                session.getBasicRemote().sendText(message);
                // AsyncRemote 是异步发送
                // session.getAsyncRemote().sendText(message);
            } catch (IOException e) {
                log.error("发送消息给 Session {} 失败", session.getId(), e);
            }
        }
    }

    /**
     * 发送消息给指定用户 (通过用户ID) (静态方法供其他服务调用)
     * @param userId  用户ID
     * @param message 消息内容 (String)
     */
    public static void sendMessageToUser(Long userId, String message) {
        Session session = SESSION_MAP.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
                log.debug("Sent message to user {}: {}", userId, message);
            } catch (IOException e) {
                log.error("发送消息给用户 {} 失败", userId, e);
            }
        } else {
            log.warn("尝试发送消息给用户 {}，但 Session 不存在或已关闭", userId);
        }
    }

    /**
     * 广播消息给所有在线用户 (静态方法供其他服务调用)
     * @param message 消息内容 (String)
     */
    public static void broadcastMessage(String message) {
        log.info("Broadcasting message: {}", message);
        // 使用 stream 并行发送可以提高性能
        SESSION_MAP.values().parallelStream().forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    log.error("广播消息失败给 Session {}", session.getId(), e);
                }
            }
        });
    }
}
