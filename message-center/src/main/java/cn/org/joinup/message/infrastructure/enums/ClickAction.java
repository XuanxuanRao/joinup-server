package cn.org.joinup.message.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClickAction {
    NONE(0, "NONE"),
    JUMP_URL(1, "JUMP_URL"),
    JUMP_PAGE(2, "JUMP_PAGE"),
    ;

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String desc;

}
