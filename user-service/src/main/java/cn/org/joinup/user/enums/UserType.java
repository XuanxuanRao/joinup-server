package cn.org.joinup.user.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum UserType {
    INTERNAL(0, "INTERNAL"),
    EXTERNAL(1, "EXTERNAL");

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String desc;

    UserType(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
