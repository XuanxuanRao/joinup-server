package cn.org.joinup.team.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author chenxuanrao06@gmail.com
 */
@Getter
public enum TagApplicationStatus {
    /**
     * 待审核
     */
    PENDING(0, "待审核"),
    /**
     * 已通过
     */
    PASSED(1, "已通过"),
    /**
     * 已拒绝
     */
    REJECTED(2, "已拒绝"),
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String desc;

    TagApplicationStatus(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @JsonCreator
    public static TagApplicationStatus fromValue(Integer value) {
        return Arrays.stream(values())
                .filter(type -> type.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid tag application status value: " + value));
    }
}
