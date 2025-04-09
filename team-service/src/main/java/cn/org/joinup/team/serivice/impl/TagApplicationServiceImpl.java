package cn.org.joinup.team.serivice.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.team.domain.dto.AddTagDTO;
import cn.org.joinup.team.domain.po.Tag;
import cn.org.joinup.team.domain.po.TagApplication;
import cn.org.joinup.team.enums.TagApplicationStatus;
import cn.org.joinup.team.mapper.TagApplicationMapper;
import cn.org.joinup.team.serivice.ITagApplicationService;
import cn.org.joinup.team.serivice.ITagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
@RequiredArgsConstructor
public class TagApplicationServiceImpl extends ServiceImpl<TagApplicationMapper, TagApplication> implements ITagApplicationService {

    private final ITagService tagService;

    @Override
    public Result<Void> addTagApplication(AddTagDTO addTagDTO) {
        if (tagService.lambdaQuery().eq(Tag::getName, addTagDTO.getName()).exists()) {
            return Result.error("标签已存在");
        }

        TagApplication tagApplication = BeanUtil.copyProperties(addTagDTO, TagApplication.class);
        tagApplication.setStatus(TagApplicationStatus.PENDING);
        tagApplication.setSubmitterUserId(UserContext.getUser());
        tagApplication.setCreateTime(LocalDateTime.now());
        save(tagApplication);
        return Result.success();
    }
}
