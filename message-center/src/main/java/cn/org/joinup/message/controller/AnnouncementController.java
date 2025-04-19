package cn.org.joinup.message.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.api.client.UserClient;
import cn.org.joinup.api.dto.UserDTO;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.message.domain.po.Announcement;
import cn.org.joinup.message.domain.vo.AnnouncementVO;
import cn.org.joinup.message.service.IAnnouncementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/message/announcement")
@RequiredArgsConstructor
@Api(tags = "用户获取公告接口")
public class AnnouncementController {

    private final IAnnouncementService announcementService;
    private final UserClient userClient;

    @ApiOperation("获取公告列表")
    @GetMapping("/list")
    public List<Announcement> list() {
        return announcementService.getBriefList();
    }

    @ApiOperation("根据id获取公告详情")
    @GetMapping("/{announcementId}")
    public Result<AnnouncementVO> getAnnouncement(@PathVariable Long announcementId) {
        Announcement announcement = announcementService.lambdaQuery()
                .eq(Announcement::getId, announcementId)
                .eq(Announcement::getDeleted, false)
                .one();

        if (announcement == null) {
            return Result.error("公告不存在");
        }

        UserDTO info = userClient.queryUser(announcement.getPosterUserId()).getData();
        AnnouncementVO announcementVO = BeanUtil.copyProperties(announcement, AnnouncementVO.class);
        announcementVO.setUsername(info.getUsername());
        announcementVO.setAvatar(info.getAvatar());
        return Result.success(announcementVO);
    }

}
