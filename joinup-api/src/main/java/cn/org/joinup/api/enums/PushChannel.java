package cn.org.joinup.api.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PushChannel {
    EMAIL(0, "邮箱"),
    SITE(1, "站内信"),
    WECHAT(2, "微信"),
    ;

    @JsonValue
    private final Integer value;
    private final String desc;

    PushChannel(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static PushChannel fromValue(Integer value) {
        return Arrays.stream(values())
                .filter(type -> type.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid push channel value: " + value));
    }

}
