package cn.org.joinup.team.serivice.impl;

import cn.hutool.core.util.StrUtil;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.team.domain.po.Tag;
import cn.org.joinup.team.domain.po.TagApplication;
import cn.org.joinup.team.enums.TagApplicationStatus;
import cn.org.joinup.team.mapper.TagMapper;
import cn.org.joinup.team.serivice.ITagApplicationService;
import cn.org.joinup.team.serivice.ITagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
@RequiredArgsConstructor
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements ITagService {

    private final ITagApplicationService tagApplicationService;

    // todo: 不是线程安全的，在高并发情况下会出现两个同名标签同时创建的情况
    // 数据库对name字段加唯一索引，虽然不优雅但是可以解决问题，后续考虑redis
    @Override
    @Transactional
    public Result<Void> approveTagApplication(Long id, String comment) {
        TagApplication tagApplication = tagApplicationService.getById(id);

        tagApplication.setStatus(TagApplicationStatus.PASSED);
        tagApplication.setReviewerComment(StrUtil.isBlank(comment) ? "审核通过" : comment);
        tagApplication.setFinishTime(LocalDateTime.now());
        tagApplicationService.updateById(tagApplication);

        if (lambdaQuery().eq(Tag::getName, tagApplication.getName()).exists()) {
            return Result.error("标签已存在，已自动通过审核");
        }

        Tag tag = new Tag();
        tag.setName(tagApplication.getName());
        tag.setDescription(tagApplication.getDescription());
        tag.setCreateTime(LocalDateTime.now());
        tag.setUpdateTime(LocalDateTime.now());
        save(tag);

        return Result.success();
    }

    @Override
    public Result<Void> rejectTagApplication(Long id, String comment) {
        TagApplication tagApplication = tagApplicationService.getById(id);
        tagApplication.setStatus(TagApplicationStatus.REJECTED);
        tagApplication.setReviewerComment(StrUtil.isBlank(comment) ? "审核未通过" : comment);
        tagApplication.setFinishTime(LocalDateTime.now());
        tagApplicationService.updateById(tagApplication);

        return Result.success();
    }
}
