package cn.org.joinup.websocket.config;

import cn.hutool.json.JSONUtil;
import cn.org.joinup.api.dto.ChatMessageVO;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * @author chenxuanrao06@gmail.com
 */
public class ChatMessageDTOEncoder implements Encoder.Text<ChatMessageVO> {
    @Override
    public String encode(ChatMessageVO chatMessageVO) throws EncodeException {
        try {
            return JSONUtil.toJsonStr(chatMessageVO);
        } catch (Exception e) {
            throw new EncodeException(chatMessageVO, "Failed to encode ChatMessageDTO to JSON", e);
        }
    }

    @Override
    public void init(EndpointConfig endpointConfig) {}

    @Override
    public void destroy() {}
}
