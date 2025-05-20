package cn.org.joinup.api.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TeamStatus {
    /**
     * 已解散
     */
    DISBANDED(0, "已解散"),
    /**
     * 正常
     */
    NORMAL(1, "正常"),
    /**
     * 已封禁
     */
    BANNED(2, "已封禁")
    ;

    private final Integer value;
    @JsonValue
    private final String desc;
}
