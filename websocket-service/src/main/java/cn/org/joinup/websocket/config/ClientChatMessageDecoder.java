package cn.org.joinup.websocket.config;

import cn.hutool.json.JSONUtil;
import cn.org.joinup.websocket.domain.ClientChatMessage;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

/**
 * @author chenxuanrao06@gmail.com
 */
public class ClientChatMessageDecoder implements Decoder.Text<ClientChatMessage> {
    @Override
    public ClientChatMessage decode(String s) throws DecodeException {
        try {
            ClientChatMessage message = JSONUtil.toBean(s, ClientChatMessage.class);
            if (message.getType() == null || message.getConversationId() == null || message.getContent().isEmpty()) {
                throw new DecodeException(s, "Failed to decode JSON to ClientChatMessage");
            }
            return message;
        } catch (Exception e) {
            throw new DecodeException(s, "Failed to decode JSON to ClientChatMessage", e);
        }
    }

    @Override
    public boolean willDecode(String s) {
        return (s != null && s.trim().startsWith("{") && s.trim().endsWith("}"));
    }

    @Override
    public void init(EndpointConfig endpointConfig) {}

    @Override
    public void destroy() {}
}
