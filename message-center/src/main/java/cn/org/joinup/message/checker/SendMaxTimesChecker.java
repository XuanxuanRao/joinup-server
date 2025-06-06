package cn.org.joinup.message.checker;

import cn.hutool.extra.spring.SpringUtil;
import cn.org.joinup.message.service.IVerifyLogService;
import net.bytebuddy.matcher.ElementMatcher;

import java.time.LocalDate;

/**
 * @author chenxuanrao06@gmail.com
 */
public class SendMaxTimesChecker implements ElementMatcher<CheckerContext> {
    @Override
    public boolean matches(CheckerContext checkerContext) {
        Integer sendCount = SpringUtil.getBean(IVerifyLogService.class).getSendCount(checkerContext.getAccount(), LocalDate.now());
        return sendCount < checkerContext.getSendMaxTimes();
    }
}
