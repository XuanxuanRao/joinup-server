package cn.org.joinup.websocket.websocket;

import cn.org.joinup.websocket.config.ChatEndpointConfigurator;
import cn.org.joinup.websocket.config.ChatMessageDTOEncoder;
import cn.org.joinup.websocket.config.ClientChatMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.server.ServerEndpoint;

@Slf4j
@Component
@ServerEndpoint(
        value = "/push/command",
        configurator = ChatEndpointConfigurator.class,
        encoders = ChatMessageDTOEncoder.class,
        decoders = ClientChatMessageDecoder.class
)
public class CommandWebSocketServer {
    
}
