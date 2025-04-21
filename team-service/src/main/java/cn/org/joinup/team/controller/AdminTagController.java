package cn.org.joinup.team.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.api.client.UserClient;
import cn.org.joinup.api.dto.UserDTO;
import cn.org.joinup.common.result.PageQuery;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.team.domain.dto.AddTagDTO;
import cn.org.joinup.team.domain.dto.TagReviewAction;
import cn.org.joinup.team.domain.po.Tag;
import cn.org.joinup.team.domain.po.TagApplication;
import cn.org.joinup.team.domain.vo.TagApplicationVO;
import cn.org.joinup.team.enums.TagApplicationStatus;
import cn.org.joinup.team.serivice.IAdminTagService;
import cn.org.joinup.team.serivice.ITagApplicationService;
import cn.org.joinup.team.serivice.ITagService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/admin/tag")
@RequiredArgsConstructor
@Api(tags = "管理员标签接口")
public class AdminTagController {
    private final ITagService tagService;
    private final ITagApplicationService tagApplicationService;
    private final UserClient userClient;
    private final IAdminTagService iAdminTagService;

    @PostMapping("/applications/list")
    public Result<PageResult<TagApplicationVO>> getTagApplications(
            @RequestParam(required = false) TagApplicationStatus status,
            @RequestBody PageQuery pageQuery) {
        Page<TagApplication> page = tagApplicationService.page(
                pageQuery.toMpPage("create_time", true),
                new QueryWrapper<TagApplication>().eq(status != null, "status", status)
        );

        List<TagApplicationVO> list = page.getRecords().stream().map(tagApplication -> {
            TagApplicationVO vo = BeanUtil.copyProperties(tagApplication, TagApplicationVO.class);
            UserDTO userInfo = userClient.queryUser(tagApplication.getSubmitterUserId()).getData();
            vo.setSubmitterUserName(userInfo.getUsername());
            vo.setSubmitterUserAvatar(userInfo.getAvatar());
            return vo;
        }).collect(Collectors.toList());

        return Result.success(new PageResult<>(page.getTotal(), page.getPages(), list));
    }

    @PostMapping("/applications/{id}/review")
    public Result<Void> reviewTagApplication(@PathVariable Long id, @RequestBody TagReviewAction tagReviewAction) {
        if(tagReviewAction.getAction() == 0) {
            return tagService.approveTagApplication(id, tagReviewAction.getComment());
        } else {
            return tagService.rejectTagApplication(id, tagReviewAction.getComment());
        }
    }

    @GetMapping("/count")
    public Result<Long> count() {
        Long count = iAdminTagService.count();
        return Result.success(count);
    }

    // 分页加载数据
    @GetMapping("/list")
    public IPage<Tag> list(Pageable pageable) {
        return iAdminTagService.getPageTags(pageable);
    }

    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestBody Map<String, Long> body) {
        Long id = body.get("id");
        iAdminTagService.removeById(id);
        return Result.success();
    }

    @DeleteMapping("/delete/batch")
    public Result<Void> deleteBatch(@RequestBody Map<String, List<Long>> body) {
        List<Long> ids = body.get("ids");
        iAdminTagService.removeByIds(ids);
        return Result.success();
    }

    // 新增标签
    @PostMapping("/add")
    public Result<Void> add(@RequestBody AddTagDTO addTagDTO) {
        Tag tag = BeanUtil.copyProperties(addTagDTO, Tag.class);
        tag.setCreateTime(LocalDateTime.now());
        iAdminTagService.save(tag);
        return Result.success();
    }

    // 更新标签
    @PutMapping("/update/{id}")
    public Result<Void> update(@PathVariable Integer id, @RequestBody AddTagDTO addTagDTO) {
        Tag tag = BeanUtil.copyProperties(addTagDTO, Tag.class);
        tag.setId(id);
        iAdminTagService.updateById(tag);
        return Result.success();
    }

    // 根据标签名称模糊查询
    @GetMapping("/querySearch")
    public Result<List<Tag>> querySearch(@RequestParam String name) {
        List<Tag> tags = iAdminTagService.list();
        List<Tag> result = new ArrayList<>();
        for (Tag tag : tags) {
            if (tag.getName().contains(name)) {
                result.add(tag);
            }
        }

        return Result.success(result);
    }

    // 返回模糊查询数量
    @GetMapping("/searchCount")
    public Result<Long> searchCount(@RequestParam String name) {
        List<Tag> tags = iAdminTagService.list();
        Long count = 0L;
        for (Tag tag : tags) {
            if (tag.getName().contains(name)) {
                count++;
            }
        }
        return Result.success(count);
    }

    // 分页显示模糊查询结果
    @GetMapping("/search")
    public IPage<Tag> search(@RequestParam String name,
                             @RequestParam("page") int page, @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return iAdminTagService.getPageTagsSearch(name, pageable);
    }



}