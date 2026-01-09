package cn.org.joinup.websocket.config;

import cn.hutool.json.JSONUtil;
import cn.org.joinup.websocket.domain.CommandExecutionResult;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class CommandExecutionResultDecoder implements Decoder.Text<CommandExecutionResult> {
    @Override
    public CommandExecutionResult decode(String s) throws DecodeException {
        try {
            CommandExecutionResult message = JSONUtil.toBean(s, CommandExecutionResult.class);
            if (message.getCommandId() == null || message.getSuccess() == null) {
                throw new DecodeException(s, "Failed to decode JSON to CommandExecutionResult");
            }
            return message;
        } catch (Exception e) {
            throw new DecodeException(s, "Failed to decode JSON to CommandExecutionResult", e);
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
