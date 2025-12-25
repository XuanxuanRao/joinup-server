package cn.org.joinup.websocket.domain;

import lombok.Data;

import java.util.Map;

@Data
public class CommandDTO {
    private Long executorId;
    private String commandType;
    private Map<String, Object> params;
}
