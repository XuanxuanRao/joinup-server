package cn.org.joinup.message.service.impl;

import cn.org.joinup.common.result.Result;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.message.domain.po.SiteMessage;
import cn.org.joinup.message.enums.NotifyType;
import cn.org.joinup.message.mapper.SiteMessageMapper;
import cn.org.joinup.message.service.ISiteMessageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
public class SiteMessageServiceImpl extends ServiceImpl<SiteMessageMapper, SiteMessage> implements ISiteMessageService {

    @Override
    public Page<SiteMessage> pageQuery(Boolean read, NotifyType type, Integer pageSize, Integer pageNumber) {
        LambdaQueryWrapper<SiteMessage> queryWrapper = new LambdaQueryWrapper<>();
        if (read != null) {
            queryWrapper.eq(SiteMessage::getRead, read);
        }
        if (type != null) {
            queryWrapper.eq(SiteMessage::getNotifyType, type);
        }
        queryWrapper.eq(SiteMessage::getReceiverUserId, UserContext.getUser());
        queryWrapper.orderByDesc(SiteMessage::getCreateTime);
        return page(new Page<>(pageNumber, pageSize), queryWrapper);
    }

    @Override
    public Result<Void> setReadStatus(Long id, boolean read) {
        Result<SiteMessage> validateResult = validateOperation(id);
        if (Objects.equals(validateResult.getCode(), Result.ERROR)) {
            return Result.error(validateResult.getMsg());
        }

        SiteMessage siteMessage = validateResult.getData();
        siteMessage.setRead(read);
        updateById(siteMessage);
        return Result.success();
    }

    @Override
    public Result<Void> deleteMessage(Long id) {
        Result<SiteMessage> validateResult = validateOperation(id);
        if (Objects.equals(validateResult.getCode(), Result.ERROR)) {
            return Result.error(validateResult.getMsg());
        }

        SiteMessage siteMessage = validateResult.getData();
        siteMessage.setDeleted(true);
        updateById(siteMessage);
        return Result.success();
    }

    private Result<SiteMessage> validateOperation(Long id) {
        SiteMessage siteMessage = lambdaQuery()
                .eq(SiteMessage::getId, id)
                .eq(SiteMessage::getReceiverUserId, UserContext.getUser())
                .one();

        if (siteMessage == null || siteMessage.getDeleted()) {
            return Result.error("站内信不存在");
        }

        return Result.success(siteMessage);
    }
}
