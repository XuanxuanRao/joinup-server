package cn.org.joinup.message.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.message.domain.EmailSendModel;
import cn.org.joinup.message.domain.SiteSendModel;
import cn.org.joinup.message.domain.po.MessageRecord;
import cn.org.joinup.message.domain.SendMessageModel;
import cn.org.joinup.message.domain.po.MessageTemplate;
import cn.org.joinup.message.events.MessageRecordEvents;
import cn.org.joinup.message.sender.EmailMessageContext;
import cn.org.joinup.message.sender.MessageContext;
import cn.org.joinup.message.sender.SiteMessageContext;
import cn.org.joinup.message.sender.WechatMessageContext;
import cn.org.joinup.message.service.IMessageRecordService;
import cn.org.joinup.message.service.IMessageTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;
import java.util.Objects;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
@RequiredArgsConstructor
public class MessageRecordServiceImpl implements IMessageRecordService {

    private final IMessageTemplateService messageTemplateService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final TemplateEngine templateEngine;

    @Override
    public void sendMessage(SendMessageModel sendMessageModel) {
        MessageTemplate template = messageTemplateService.findByTemplateCode(sendMessageModel.getTemplateCode());
        String content = parseTemplate(template, sendMessageModel.getParams());
        MessageRecord messageRecord = BeanUtil.copyProperties(sendMessageModel, MessageRecord.class);
        messageRecord.setContent(content);
        applicationEventPublisher.publishEvent(new MessageRecordEvents.MessageRecordCreateEvent(messageRecord, buildContext(sendMessageModel, content, template.getTitle())));
    }

    private String parseTemplate(MessageTemplate messageTemplate, Map<String, Object> params) {
        if (Objects.equals(messageTemplate.getEncoding(), "HTML")) {
            // 使用 thymeleaf 解析
            Context context = new Context();
            params.forEach(context::setVariable);
            return templateEngine.process(messageTemplate.getResourcePath(), context);
        } else {
            String content = messageTemplate.getTemplate();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                content = content.replace("{{" + entry.getKey() + "}}", entry.getValue().toString());
            }
            return content;
        }
    }

    private MessageContext buildContext(SendMessageModel model, String content, String title) {
        switch (model.getChannel()) {
            case EMAIL:
                EmailMessageContext emailContext = new EmailMessageContext();
                EmailSendModel emailSendModel = (EmailSendModel) model;
                emailContext.setTo(emailSendModel.getEmail());
                emailContext.setSubject(title);
                emailContext.setContent(content);
                return emailContext;
            case SITE:
                SiteSendModel siteSendModel = (SiteSendModel) model;
                SiteMessageContext siteMessageContext = BeanUtil.copyProperties(siteSendModel, SiteMessageContext.class);
                siteMessageContext.setTitle(title);
                siteMessageContext.setContent(content);
                return siteMessageContext;
            case WECHAT:
                // 组装微信上下文
                return new WechatMessageContext();
            default:
                throw new UnsupportedOperationException("不支持的发送渠道");
        }
    }

}
