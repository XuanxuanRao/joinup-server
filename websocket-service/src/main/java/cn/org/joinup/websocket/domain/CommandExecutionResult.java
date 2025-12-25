package cn.org.joinup.websocket.domain;

import lombok.Data;

@Data
public class CommandExecutionResult {
    private String commandId;
    private Boolean success;
    private String message;
    private Object data;
}
