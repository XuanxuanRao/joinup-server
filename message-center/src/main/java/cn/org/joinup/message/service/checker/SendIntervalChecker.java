package cn.org.joinup.message.service.checker;

import cn.hutool.extra.spring.SpringUtil;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * @author chenxuanrao06@gmail.com
 */
public class SendIntervalChecker implements ElementMatcher<CheckerContext> {
    @Override
    public boolean matches(CheckerContext checkerContext) {
        return true;
    }
}
