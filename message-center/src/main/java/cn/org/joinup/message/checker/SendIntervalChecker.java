package cn.org.joinup.message.checker;

import cn.hutool.extra.spring.SpringUtil;
import cn.org.joinup.message.service.IVerifyLogService;
import net.bytebuddy.matcher.ElementMatcher;

import java.time.LocalDateTime;

/**
 * @author chenxuanrao06@gmail.com
 */
public class SendIntervalChecker implements ElementMatcher<CheckerContext> {
    @Override
    public boolean matches(CheckerContext checkerContext) {
        LocalDateTime lastSendTime = SpringUtil.getBean(IVerifyLogService.class).lastSendTime(checkerContext.getAccount());
        return lastSendTime == null || lastSendTime.plusSeconds(checkerContext.getSendInterval()).isBefore(LocalDateTime.now());
    }
}
