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
public enum TeamJoinApplicationStatus {
    PENDING(0, "待处理"),
    ACCEPTED(1, "已接受"),
    REJECTED(2, "已拒绝"),
    CANCELED(3, "已取消");

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String desc;

    TeamJoinApplicationStatus(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @JsonCreator
    public static TeamJoinApplicationStatus fromValue(Integer value) {
        return Arrays.stream(values())
                .filter(type -> type.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid team join application status value: " + value));
    }
}
