package cn.org.joinup.user.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ScanLoginStatus {
    WAITING("等待扫码"),
    SCANNED("已扫码，等待确认"),
    CONFIRM("已确认，登录成功"),
    EXPIRED("二维码已过期")
    ;

    @JsonValue
    private final String value = name().toLowerCase();
    private final String desc;

    @JsonCreator
    public static ScanLoginStatus fromValue(String value) {
        return Arrays.stream(values())
                .filter(type -> type.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid ScanLoginStatus value: " + value));
    }

}
