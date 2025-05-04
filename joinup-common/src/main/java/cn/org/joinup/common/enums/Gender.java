package cn.org.joinup.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
public enum Gender {
    UNKNOWN(0, "未知"),
    MALE(1, "男"),
    FEMALE(2, "女")
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String desc;

    Gender(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @JsonCreator
    public static Gender fromDesc(String desc) {
        return Arrays.stream(values())
                .filter(type -> Objects.equals(type.desc, desc))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid gender value: " + desc));
    }
}
