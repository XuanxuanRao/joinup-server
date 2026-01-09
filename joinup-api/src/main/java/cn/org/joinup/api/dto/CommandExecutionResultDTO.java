package cn.org.joinup.api.dto;

import lombok.Data;

@Data
public class CommandExecutionResultDTO<T> {
    private String commandId;
    private Boolean success;
    private String message;
    private T data;
}
