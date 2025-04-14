package cn.org.joinup.api.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MessageType {
    VERIFY(0, "验证码"),
    NOTICE(1, "通知")
    ;

    @JsonValue
    private final Integer code;
    private final String desc;

    MessageType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static MessageType fromValue(Integer value) {
        return Arrays.stream(values())
                .filter(type -> type.code.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid message type value: " + value));
    }
}
