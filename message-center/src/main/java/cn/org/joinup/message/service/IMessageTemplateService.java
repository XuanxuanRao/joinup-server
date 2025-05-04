package cn.org.joinup.message.service;

import cn.org.joinup.message.domain.po.MessageTemplate;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IMessageTemplateService extends IService<MessageTemplate> {
    /**
     * 获取模板
     *
     * @param templateCode 模板编码
     * @return 模板内容
     */
    MessageTemplate findByTemplateCode(String templateCode);
}
