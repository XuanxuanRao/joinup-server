package cn.org.joinup.course.converter;

import cn.org.joinup.course.enums.SignTaskStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author chenxuanrao06@gmail.com
 */
@Component
public class StringToSignTaskStatusConverter implements Converter<String, SignTaskStatus> {

    @Override
    public SignTaskStatus convert(String source) {
        if (source.trim().isEmpty()) {
            return null;
        }
        try {
            Integer intValue = Integer.parseInt(source);
            // 调用枚举中现有的工厂方法
            return SignTaskStatus.fromValue(intValue);
        } catch (NumberFormatException e) {
            // 如果字符串不能解析为整数，也视为非法参数
            throw new IllegalArgumentException("Invalid task status value format: " + source, e);
        } catch (IllegalArgumentException e) {
            // 捕获 SignTaskStatus.fromValue 可能抛出的异常
            throw new IllegalArgumentException("Invalid task status value: " + source, e);
        }
    }
}