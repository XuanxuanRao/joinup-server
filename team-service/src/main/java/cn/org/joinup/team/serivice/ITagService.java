package cn.org.joinup.team.serivice;

import cn.org.joinup.common.result.Result;
import cn.org.joinup.team.domain.po.Tag;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ITagService extends IService<Tag> {
    Result<Void> approveTagApplication(Long id, String comment);

    Result<Void> rejectTagApplication(Long id, String comment);
}
