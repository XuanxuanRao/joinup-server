package cn.org.joinup.message.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.message.domain.dto.AddAnnouncementDTO;
import cn.org.joinup.message.domain.po.Announcement;
import cn.org.joinup.message.service.IAnnouncementService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/admin/message/announcement")
@RequiredArgsConstructor
@Api(tags = "管理员公告接口")
public class AdminAnnouncementController {

    private final IAnnouncementService announcementService;

    @ApiOperation("管理员发布系统公告")
    @PostMapping("/add")
    public Result<Long> addAnnouncement(@RequestBody @Validated AddAnnouncementDTO addAnnouncementDTO) {
        Announcement announcement = BeanUtil.copyProperties(addAnnouncementDTO, Announcement.class);
        announcement.setPosterUserId(UserContext.getUser());
        if (!announcementService.save(announcement)) {
            return Result.error("公告发布失败，请稍后再试");
        }

        return Result.success(announcement.getId());
    }

    @ApiOperation("管理员删除系统公告")
    @DeleteMapping("/{announcementId}")
    public Result<Void> deleteAnnouncement(@PathVariable Long announcementId) {
        Announcement announcement = announcementService.getById(announcementId);
        if (announcement == null) {
            return Result.error("公告不存在");
        }
        announcement.setDeleted(true);
        if (!announcementService.updateById(announcement)) {
            return Result.error("公告删除失败，请稍后再试");
        }
        return Result.success();
    }

    @ApiOperation("恢复已删除的公告")
    @PutMapping("/recover/{announcementId}")
    public Result<Void> recoverAnnouncement(@PathVariable Long announcementId) {
        Announcement announcement = announcementService.getById(announcementId);
        if (announcement == null) {
            return Result.error("公告不存在");
        } else if (!announcement.getDeleted()) {
            return Result.error("公告未删除");
        }

        announcement.setDeleted(false);
        LambdaUpdateWrapper<Announcement> updateWrapper = new LambdaUpdateWrapper<Announcement>()
                .eq(Announcement::getId, announcementId)
                .eq(Announcement::getDeleted, false);

        if (!announcementService.update(announcement, updateWrapper)) {
            return Result.error("公告恢复失败，请稍后再试");
        }
        return Result.success();
    }

    @ApiOperation("管理员修改系统公告")
    @PutMapping("/{announcementId}")
    public Result<Long> updateAnnouncement(@PathVariable Long announcementId, @RequestBody @Validated AddAnnouncementDTO addAnnouncementDTO) {
        Announcement announcement = BeanUtil.copyProperties(addAnnouncementDTO, Announcement.class);
        announcement.setId(announcementId);

        if (!announcementService.updateById(announcement)) {
            return Result.error("公告修改失败，请稍后再试");
        }

        return Result.success(announcement.getId());
    }

    @ApiOperation("管理员查看系统公告")
    @GetMapping("/{announcementId}")
    public Result<Announcement> getAnnouncement(@PathVariable Long announcementId) {
        Announcement announcement = announcementService.getById(announcementId);
        if (announcement == null) {
            return Result.error("公告不存在");
        }
        return Result.success(announcement);
    }

    @ApiOperation("管理员查看系统公告列表")
    @GetMapping("/list")
    public Result<List<Announcement>> getAnnouncementList() {
        List<Announcement> announcements = announcementService.lambdaQuery()
                .orderByDesc(Announcement::getCreateTime)
                .list();
        return Result.success(announcements);
    }
}
