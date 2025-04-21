package cn.org.joinup.message.service;

import cn.org.joinup.common.result.Result;
import cn.org.joinup.message.domain.po.SiteMessage;
import cn.org.joinup.message.enums.NotifyType;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ISiteMessageService extends IService<SiteMessage> {

    Page<SiteMessage> pageQuery(Boolean read, NotifyType type, Integer pageSize, Integer pageNumber);

    Result<Void> setReadStatus(Long id, boolean read);

    Result<Void> deleteMessage(Long id);

}
