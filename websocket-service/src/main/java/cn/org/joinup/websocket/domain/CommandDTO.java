package cn.org.joinup.websocket.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandDTO {
    @NotNull
    private String commandId;
    @NotNull
    private String commandType;
    private Map<String, Object> params;
}
