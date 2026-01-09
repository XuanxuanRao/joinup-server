package cn.org.joinup.api.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
public class CommandRequestDTO {
    @NotNull
    private Long executorId;
    @NotNull
    private String commandType;
    private Map<String, Object> params;
}
