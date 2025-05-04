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

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String desc;

    TeamStatus(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @JsonCreator
    public static TeamStatus fromValue(Integer value) {
        return Arrays.stream(values())
                .filter(type -> type.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid team status value: " + value));
    }


}
