package cn.org.joinup.team.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.api.client.UserClient;
import cn.org.joinup.api.dto.UserDTO;
import cn.org.joinup.common.result.PageQuery;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.team.domain.dto.ReviewAction;
import cn.org.joinup.team.domain.po.TagApplication;
import cn.org.joinup.team.domain.vo.TagApplicationVO;
import cn.org.joinup.team.enums.TagApplicationStatus;
import cn.org.joinup.team.serivice.ITagApplicationService;
import cn.org.joinup.team.serivice.ITagService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping("/applications/list")
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
    public Result<Void> reviewTagApplication(@PathVariable Long id, @RequestBody ReviewAction reviewAction) {
        if(reviewAction.getAction() == 0) {
            return tagService.approveTagApplication(id, reviewAction.getComment());
        } else {
            return tagService.rejectTagApplication(id, reviewAction.getComment());
        }
    }

}
