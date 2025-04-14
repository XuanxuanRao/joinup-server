package cn.org.joinup.message.service.checker;

import net.bytebuddy.matcher.ElementMatcher;

/**
 * @author chenxuanrao06@gmail.com
 */
public class SendMaxTimesChecker implements ElementMatcher<CheckerContext> {
    @Override
    public boolean matches(CheckerContext checkerContext) {
        return false;
    }
}
