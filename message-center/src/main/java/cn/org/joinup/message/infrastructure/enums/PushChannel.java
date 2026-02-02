package cn.org.joinup.message.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PushChannel {
    EMAIL(0, "邮箱"),
    SITE(1, "站内信"),
    WECHAT(2, "微信"),
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String desc;

    PushChannel(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @JsonCreator
    public static PushChannel fromValue(Integer value) {
        return Arrays.stream(values())
                .filter(type -> type.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid push channel value: " + value));
    }

}
