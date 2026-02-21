package cn.org.joinup.message.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum CurrencyCode {
    CNY("CNY", "人民币"),
    JPY("JPY", "日元"),
    USD("USD", "美元")
    ;

    @EnumValue
    @JsonValue
    private final String code;
    private final String desc;

    public static Map<String, String> getCodeToNameMap() {
        return Arrays.stream(CurrencyCode.values())
                .collect(Collectors.toMap(CurrencyCode::getCode, CurrencyCode::getDesc));
    }

    public static String getCurrencyNameFromCode(String code) {
        return getCodeToNameMap().get(code);
    }

}
