package cn.org.joinup.course.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author chenxuanrao06@gmail.com
 */
@Getter
public enum SignStatus {

    SIGNED(1, "已签到"),
    UNSIGNED(0, "未签到")
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String desc;

    SignStatus(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @JsonCreator
    public static SignStatus fromValue(Integer value) {
        return Arrays.stream(values())
                .filter(type -> Objects.equals(type.value, value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid sign status value: " + value));
    }

}
