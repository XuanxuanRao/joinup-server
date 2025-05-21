package cn.org.joinup.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author chenxuanrao06@gmail.com
 */
@Getter
public enum ChatMessageType {
    TEXT(1, "TEXT"),
    IMAGE(2, "IMAGE"),
    FILE(3, "FILE"),
    TEAM_SHARE(4, "TEAM_SHARE")
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String desc;

    ChatMessageType(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @JsonCreator
    public static ChatMessageType fromDesc(String desc) {
        return Arrays.stream(values())
                .filter(type -> type.desc.equals(desc))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid chat message type desc: " + desc));
    }
}
