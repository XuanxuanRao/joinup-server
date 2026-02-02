package cn.org.joinup.message.application.message.service.impl;

import cn.org.joinup.message.domain.message.entity.MessageTemplate;
import cn.org.joinup.message.infrastructure.repository.MessageTemplateMapper;
import cn.org.joinup.message.application.message.service.IMessageTemplateService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
public class MessageTemplateServiceImpl extends ServiceImpl<MessageTemplateMapper, MessageTemplate> implements IMessageTemplateService {

    @Override
    public MessageTemplate findByTemplateCode(String templateCode) {
        return getOne(new LambdaQueryWrapper<MessageTemplate>()
                .eq(MessageTemplate::getCode, templateCode));
    }

}
