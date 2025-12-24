package cn.org.joinup.websocket.websocket;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import cn.org.joinup.api.dto.ChatMessageDTO;
import cn.org.joinup.api.dto.ChatMessageVO;
import cn.org.joinup.websocket.config.ChatEndpointConfigurator;
import cn.org.joinup.websocket.config.ChatMessageDTOEncoder;
import cn.org.joinup.websocket.config.ClientChatMessageDecoder;
import cn.org.joinup.websocket.constant.EndpointConfigConstant;
import cn.org.joinup.websocket.domain.ClientChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author chenxuanrao06@gmail.com
 */
@Slf4j
@Component
@ServerEndpoint(
        value = "/chat",
        configurator = ChatEndpointConfigurator.class,
        encoders = ChatMessageDTOEncoder.class,
        decoders = ClientChatMessageDecoder.class
)
public class ChatWebSocketServer {
    // 维护用户ID与Session的映射，用于点对点发送和广播
    private static final Map<Long, Session> SESSION_MAP = new ConcurrentHashMap<>();
    private static RabbitTemplate rabbitTemplate;

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        ChatWebSocketServer.rabbitTemplate = rabbitTemplate;
    }

    public static Set<Long> getOnlineUsers(String userType, String appKey) {
        return SESSION_MAP.entrySet().stream()
                .filter(entry -> {
                    Session session = entry.getValue();
                    if (session == null) {
                        return false;
                    }
                    Object sessionUserType = session.getUserProperties().get(EndpointConfigConstant.USER_TYPE_PROP_NAME);
                    return userType != null && userType.equals(sessionUserType);
                })
                .filter(entry -> {
                    if (appKey == null) {
                        return true;
                    }
                    Session session = entry.getValue();
                    Object sessionAppKey = session.getUserProperties().get(EndpointConfigConstant.APP_KEY_PROP_NAME);
                    return appKey.equals(sessionAppKey);
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public static boolean forceDisconnect(Long userId, String reason) {
        Session session = SESSION_MAP.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, reason));
                log.info("主动断开用户 {} 的连接", userId);
                return true;
            } catch (IOException e) {
                log.error("主动断开用户 {} 的连接失败", userId, e);
            }
        } else {
            log.warn("尝试断开用户 {} 的连接，但 Session 不存在或已关闭", userId);
        }
        return false;
    }

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
            if (userProperties.containsKey("authError") || !userProperties.containsKey(EndpointConfigConstant.USER_ID_PROP_NAME)) {
                String errorMsg = (String) userProperties.getOrDefault("authError", "Missing user info");
                log.warn("WebSocket连接失败：握手阶段认证失败 - {}", errorMsg);
                try {
                    session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Authentication failed: " + errorMsg));
                } catch (IOException ex) {
                    log.error("关闭WebSocket连接失败", ex);
                }
                return;
            }

            Long userId = (Long) userProperties.get(EndpointConfigConstant.USER_ID_PROP_NAME);
            String appKey = (String) userProperties.get(EndpointConfigConstant.APP_KEY_PROP_NAME);
            String userType = (String) userProperties.get(EndpointConfigConstant.USER_TYPE_PROP_NAME);

            log.info("WebSocket Handshake: User ID: {}", userId);

            // 2. 认证成功，将用户ID与Session关联
            SESSION_MAP.put(userId, session);
            // 也将用户信息存储到 session 的用户属性中，方便在其他生命周期方法中访问
            session.getUserProperties().put(EndpointConfigConstant.USER_ID_PROP_NAME, userId);
            session.getUserProperties().put(EndpointConfigConstant.APP_KEY_PROP_NAME, appKey);
            session.getUserProperties().put(EndpointConfigConstant.USER_TYPE_PROP_NAME, userType);

            log.info("用户 {} 连接成功. Session ID: {}. 当前在线用户数：{}", userId, session.getId(), SESSION_MAP.size());
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
        // 从 Session 属性获取用户 ID
        Long userId = (Long) session.getUserProperties().get(EndpointConfigConstant.USER_ID_PROP_NAME);
        if (userId != null) {
            SESSION_MAP.remove(userId); // 从映射中移除 Session
            log.info("用户 {} 断开连接. Session ID: {}. 当前在线用户数：{}", userId, session.getId(), SESSION_MAP.size());
            String key = "user:at:conversation:" + userId;
            StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
            stringRedisTemplate.delete(key);
        } else {
            // 如果用户 ID 为 null，说明可能在 @OnOpen 认证前或认证失败时连接就关闭了
            log.info("未知用户断开连接. Session ID: {}", session.getId());
        }
    }

    /**
     * 收到客户端消息后调用的方法
     * @param clientChatMessage 客户端发送过来的消息
     * @param session 发送消息的 Session 对象
     */
    @OnMessage
    public void onMessage(ClientChatMessage clientChatMessage, Session session) {
        // 从 session 的用户属性中获取用户 ID 和角色
        Long userId = (Long) session.getUserProperties().get(EndpointConfigConstant.USER_ID_PROP_NAME);
        if (userId == null) {
            // 理论上，如果 @OnOpen 中认证失败，连接应该已经被关闭。如果这里收到消息，说明有未认证的 session 仍在尝试通信。
            log.warn("收到来自未认证 Session ({}) 的消息", session.getId());
            // 可以选择忽略或关闭连接
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Unauthenticated session message"));
            } catch (IOException e) {
                log.error("关闭未认证 Session 失败", e);
            }
        }

        log.info("收到用户 {} 的消息：{}", userId, clientChatMessage);
        ChatMessageDTO chatMessageDTO = BeanUtil.copyProperties(clientChatMessage, ChatMessageDTO.class);
        chatMessageDTO.setSenderId(userId);
        chatMessageDTO.setCreateTime(LocalDateTime.now());
        // 通过消息队列发送消息
        rabbitTemplate.convertAndSend("chat.message.direct", "onMessage", chatMessageDTO);
    }

    /**
     * 连接发生错误时调用的方法
     * @param session 发生错误的 Session 对象
     * @param error   发生的错误
     */
    @OnError
    public void onError(Session session, Throwable error) {
        Long userId = (Long) session.getUserProperties().get(EndpointConfigConstant.USER_ID_PROP_NAME);
        if (userId != null) {
            log.error("用户 {} 的 WebSocket 发生错误. Session ID: {}", userId, session.getId(), error);
            SESSION_MAP.remove(userId);
        } else {
            log.error("未知用户的 WebSocket 发生错误. Session ID: {}", session.getId(), error);
        }
    }

    private void sendJsonMessage(Session session, ChatMessageVO chatMessageVO) {
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(JSONUtil.toJsonStr(chatMessageVO));
                log.debug("发送 JSON 消息给 Session {}: {}", session.getId(), chatMessageVO);
            } catch (IOException e) {
                log.error("发送 JSON 消息给 Session {} 失败", session.getId(), e);
                // 发送 IO 错误通常意味着连接问题，可能需要处理断开连接或重试
            }
        } else {
            log.warn("尝试发送 JSON 消息给一个无效或已关闭的 Session");
        }
    }

    /**
     * 发送消息给指定用户 (通过用户ID) (静态方法供其他服务调用)
     * @param userId  用户ID
     * @param message 消息内容 (String)
     */
    public static void sendMessageToUser(Long userId, ChatMessageVO message) {
        Session session = SESSION_MAP.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendObject(message);
                log.debug("Sent message to user {}: {}", userId, message);
            } catch (IOException | EncodeException e) {
                log.error("发送消息给用户 {} 失败", userId, e);
            }
        } else {
            log.warn("尝试发送消息给用户 {}，但 Session 不存在或已关闭", userId);
        }
    }
}
