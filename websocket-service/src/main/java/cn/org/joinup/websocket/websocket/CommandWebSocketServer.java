package cn.org.joinup.websocket.websocket;

import cn.org.joinup.websocket.config.ChatEndpointConfigurator;
import cn.org.joinup.websocket.config.CommandDTOEncoder;
import cn.org.joinup.websocket.config.CommandExecutionResultDecoder;
import cn.org.joinup.websocket.constant.EndpointConfigConstant;
import cn.org.joinup.websocket.domain.CommandDTO;
import cn.org.joinup.websocket.domain.CommandExecutionResult;
import cn.org.joinup.websocket.service.CommandWebSocketProxyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
@ServerEndpoint(
        value = "/push/command",
        configurator = ChatEndpointConfigurator.class,
        encoders = CommandDTOEncoder.class,
        decoders = CommandExecutionResultDecoder.class
)
public class CommandWebSocketServer extends BaseWebSocketServer {
    // 维护用户ID与Session的映射，用于点对点发送和广播
    private static final Map<Long, Session> SESSION_MAP = new ConcurrentHashMap<>();

    private static CommandWebSocketProxyService commandWebSocketProxyService;

    @Autowired
    public void setRabbitTemplate(CommandWebSocketProxyService commandWebSocketProxyService) {
        CommandWebSocketServer.commandWebSocketProxyService = commandWebSocketProxyService;
    }

    @Override
    protected Map<Long, Session> getSessionMap() {
        return SESSION_MAP;
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

    /**
     * 连接建立成功调用的方法
     * @param session 当前连接的 Session 对象
     * @param config  端点配置对象，包含了 Configurator 传递的用户属性
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        super.doOpen(session, config);
    }

    @OnClose
    public void onClose(Session session) {
        super.doClose(session);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        super.doError(session, error);
    }

    public static void sendCommandToUser(Long userId, CommandDTO command) {
        Session session = SESSION_MAP.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendObject(command);
                log.debug("Sent command to user {}: {}", userId, command);
            } catch (IOException | EncodeException e) {
                log.error("发送命令给用户 {} 失败", userId, e);
            }
        } else {
            log.warn("尝试发送命令给用户 {}，但 Session 不存在或已关闭", userId);
        }
    }

    @OnMessage
    public void onMessage(CommandExecutionResult commandResult, Session session) {
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
        // 处理命令执行结果
        log.info("Received command execution result for user {}: {}", userId, commandResult);
        commandWebSocketProxyService.handleResponse(commandResult);
    }
}
