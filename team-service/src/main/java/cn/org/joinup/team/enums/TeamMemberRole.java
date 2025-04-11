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
public enum TeamMemberRole {
    CREATOR(0, "创建者"),
    MEMBER(1, "成员")
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String desc;

    TeamMemberRole(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @JsonCreator
    public static TeamMemberRole fromValue(Integer value) {
        return Arrays.stream(values())
                .filter(type -> type.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid team member role value: " + value));
    }

}
