package cn.org.joinup.websocket.websocket;

import cn.org.joinup.websocket.config.ChatEndpointConfigurator;
import cn.org.joinup.websocket.config.CommandDTOEncoder;
import cn.org.joinup.websocket.config.CommandExecutionResultDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    @Override
    protected Map<Long, Session> getSessionMap() {
        return SESSION_MAP;
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
}
