package cn.org.joinup.websocket.config;

import cn.hutool.json.JSONUtil;
import cn.org.joinup.websocket.domain.CommandDTO;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class CommandDTOEncoder implements Encoder.Text<CommandDTO> {

    @Override
    public String encode(CommandDTO commandDTO) throws EncodeException {
        try {
            return JSONUtil.toJsonStr(commandDTO);
        } catch (Exception e) {
            throw new EncodeException(commandDTO, "Failed to encode CommandDTO to JSON", e);
        }
    }

    @Override
    public void init(EndpointConfig endpointConfig) {}

    @Override
    public void destroy() {}

}
