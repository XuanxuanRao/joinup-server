package cn.org.joinup.message.sender;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.message.domain.po.SiteMessage;
import cn.org.joinup.message.enums.PushChannel;
import cn.org.joinup.message.service.ISiteMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author chenxuanrao06@gmail.com
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SiteMessageSender implements MessageChannelSender<SiteMessageContext> {
    private final ISiteMessageService siteMessageService;
    @Override
    public PushChannel getChannel() {
        return PushChannel.SITE;
    }

    @Override
    public void send(SiteMessageContext messageContext) {
        SiteMessage siteMessage = BeanUtil.copyProperties(messageContext, SiteMessage.class);
        siteMessage.setRead(false);
        siteMessage.setDeleted(false);
        siteMessage.setCreateTime(LocalDateTime.now());
        siteMessage.setReadTime(null);
        siteMessageService.save(siteMessage);
    }
}
