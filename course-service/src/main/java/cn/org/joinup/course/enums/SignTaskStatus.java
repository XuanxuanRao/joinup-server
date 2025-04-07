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
public enum SignTaskStatus {
    CANCELED(0, "已取消"),
    RUNNING(1, "运行中"),
    FORBIDDEN(2, "已禁用"),
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String desc;

    SignTaskStatus(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @JsonCreator
    public static SignTaskStatus fromValue(Integer value) {
        return Arrays.stream(values())
                .filter(type -> Objects.equals(type.value, value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid task status value: " + value));
    }

}
