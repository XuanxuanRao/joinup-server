package cn.org.joinup.message.service.impl;

import cn.org.joinup.message.domain.po.MessageTemplate;
import cn.org.joinup.message.mapper.MessageTemplateMapper;
import cn.org.joinup.message.service.IMessageTemplateService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
