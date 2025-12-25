package cn.org.joinup.websocket.websocket;

import cn.org.joinup.websocket.constant.EndpointConfigConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;

/**
 * WebSocket 服务基类，提取公共属性和方法
 */
@Slf4j
@SuppressWarnings("resource")
public abstract class BaseWebSocketServer {

    protected static RabbitTemplate rabbitTemplate;

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        BaseWebSocketServer.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 获取具体的 Session Map，由子类实现
     * @return Map<Long, Session>
     */
    protected abstract Map<Long, Session> getSessionMap();

    /**
     * 连接建立成功调用的通用逻辑
     * @param session 当前连接的 Session 对象
     * @param config  端点配置对象
     */
    protected void doOpen(Session session, EndpointConfig config) {
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
            getSessionMap().put(userId, session);
            // 也将用户信息存储到 session 的用户属性中，方便在其他生命周期方法中访问
            session.getUserProperties().put(EndpointConfigConstant.USER_ID_PROP_NAME, userId);
            session.getUserProperties().put(EndpointConfigConstant.APP_KEY_PROP_NAME, appKey);
            session.getUserProperties().put(EndpointConfigConstant.USER_TYPE_PROP_NAME, userType);

            log.info("用户 {} 连接成功. Session ID: {}. 当前在线用户数：{}", userId, session.getId(), getSessionMap().size());
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
     * 连接关闭调用的通用逻辑
     * @param session 当前关闭的 Session 对象
     */
    protected void doClose(Session session) {
        // 从 Session 属性获取用户 ID
        Long userId = (Long) session.getUserProperties().get(EndpointConfigConstant.USER_ID_PROP_NAME);
        if (userId != null) {
            getSessionMap().remove(userId); // 从映射中移除 Session
            log.info("用户 {} 断开连接. Session ID: {}. 当前在线用户数：{}", userId, session.getId(), getSessionMap().size());
        } else {
            // 如果用户 ID 为 null，说明可能在 @OnOpen 认证前或认证失败时连接就关闭了
            log.info("未知用户断开连接. Session ID: {}", session.getId());
        }
    }

    /**
     * 连接发生错误时调用的通用逻辑
     * @param session 发生错误的 Session 对象
     * @param error   发生的错误
     */
    protected void doError(Session session, Throwable error) {
        Long userId = (Long) session.getUserProperties().get(EndpointConfigConstant.USER_ID_PROP_NAME);
        if (userId != null) {
            log.error("用户 {} 的 WebSocket 发生错误. Session ID: {}", userId, session.getId(), error);
            getSessionMap().remove(userId);
        } else {
            log.error("未知用户的 WebSocket 发生错误. Session ID: {}", session.getId(), error);
        }
    }
}
