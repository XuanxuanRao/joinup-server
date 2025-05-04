package cn.org.joinup.team.serivice;

import cn.org.joinup.common.result.Result;
import cn.org.joinup.team.domain.dto.AddTagDTO;
import cn.org.joinup.team.domain.po.TagApplication;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ITagApplicationService extends IService<TagApplication> {
    Result<Void> addTagApplication(AddTagDTO addTagDTO);
}
