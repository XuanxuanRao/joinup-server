package cn.org.joinup.message.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum NotifyType {
    TEAM(0, "组队"),
    COURSE(1, "课程"),
    BOYA(2, "博雅")
    ;

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String desc;

    NotifyType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static NotifyType fromValue(Integer value) {
        return Arrays.stream(values())
                .filter(type -> type.code.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid notify type value: " + value));
    }


}
