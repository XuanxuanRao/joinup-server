package cn.org.joinup.message.controller.admin;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.message.domain.dto.AddAnnouncementDTO;
import cn.org.joinup.message.domain.po.Announcement;
import cn.org.joinup.message.service.IAdminAnnouncementService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/admin/message/announcement")
@RequiredArgsConstructor
@Api(tags = "管理员公告接口")
public class AdminAnnouncementController {

    private final IAdminAnnouncementService iAdminAnnouncementService;

    @ApiOperation("管理员发布系统公告")
    @PostMapping("/add")
    public Result<Long> addAnnouncement(@RequestBody @Validated AddAnnouncementDTO addAnnouncementDTO) {
        Announcement announcement = BeanUtil.copyProperties(addAnnouncementDTO, Announcement.class);
        announcement.setPosterUserId(UserContext.getUserId());
        announcement.setUpdateTime(LocalDateTime.now());
        if (!iAdminAnnouncementService.save(announcement)) {
            return Result.error("公告发布失败，请稍后再试");
        }

        return Result.success(announcement.getId());
    }

    @GetMapping("/count")
    public Result<Long> count() {
        Long count = iAdminAnnouncementService.count();
        return Result.success(count);
    }

    // 分页加载数据
    @GetMapping("/list")
    public IPage<Announcement> list(Pageable pageable) {
        return iAdminAnnouncementService.getPageAnnouncements(pageable);
    }

    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestBody Map<String, Long> body) {
        Long id = body.get("id");
        iAdminAnnouncementService.removeById(id);
        return Result.success();
    }

    @DeleteMapping("/delete/batch")
    public Result<Void> deleteBatch(@RequestBody Map<String, List<Long>> body) {
        List<Long> ids = body.get("ids");
        iAdminAnnouncementService.removeByIds(ids);
        return Result.success();
    }

//    // 新增主题
//    @PostMapping("/add")
//    public Result<Void> add(@RequestBody AddAnnouncementDTO addAnnouncementDTO) {
//        Announcement announcement = BeanUtil.copyProperties(addAnnouncementDTO, Announcement.class);
//        announcement.setCreateTime(LocalDateTime.now());
//        iAdminAnnouncementService.save(announcement);
//        return Result.success();
//    }

    // 更新主题
    @PutMapping("/update/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody AddAnnouncementDTO addAnnouncementDTO) {
        Announcement announcement = BeanUtil.copyProperties(addAnnouncementDTO, Announcement.class);
        announcement.setId(id);
        iAdminAnnouncementService.updateById(announcement);
        return Result.success();
    }

    // 根据主题名称模糊查询
    @GetMapping("/querySearch")
    public Result<List<Announcement>> querySearch(@RequestParam String name) {
        List<Announcement> announcements = iAdminAnnouncementService.list();
        List<Announcement> result = new ArrayList<>();
        for (Announcement announcement : announcements) {
            if (announcement.getTitle().contains(name)) {
                result.add(announcement);
            }
        }

        return Result.success(result);
    }

    // 返回模糊查询数量
    @GetMapping("/searchCount")
    public Result<Long> searchCount(@RequestParam String name) {
        List<Announcement> announcements = iAdminAnnouncementService.list();
        Long count = 0L;
        for (Announcement announcement : announcements) {
            if (announcement.getTitle().contains(name)) {
                count++;
            }
        }
        return Result.success(count);
    }

    // 分页显示模糊查询结果
    @GetMapping("/search")
    public IPage<Announcement> search(@RequestParam String name,
                               @RequestParam("page") int page, @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return iAdminAnnouncementService.getPageAnnouncementsSearch(name, pageable);
    }

}
