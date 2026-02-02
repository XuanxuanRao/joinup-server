package cn.org.joinup.message.application.annoucement.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.api.client.UserClient;
import cn.org.joinup.api.dto.UserDTO;
import cn.org.joinup.common.constant.SystemConstant;
import cn.org.joinup.common.exception.BadRequestException;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.message.domain.announcement.entity.Announcement;
import cn.org.joinup.message.interfaces.vo.AnnouncementVO;
import cn.org.joinup.message.infrastructure.repository.AnnouncementMapper;
import cn.org.joinup.message.application.annoucement.service.IAnnouncementService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements IAnnouncementService {

    private final UserClient userClient;

    @Override
    public List<Announcement> getBriefList() {
        boolean isLogin = UserContext.getUserId() != null;

        return this.lambdaQuery()
                .eq(Announcement::getDeleted, false)
                // 如果未登录，只能看到 UserType 为 null 的公告
                .eq(!isLogin, Announcement::getTargetUserType, null)
                .eq(isLogin, Announcement::getTargetUserType, UserContext.getUserType())
                // 如果是 EXTERNAL，只能看到 TargetAppKeys 包含 UserContext.getAppKey() 的公告
                .apply(isLogin && Objects.equals(UserContext.getUserType(), SystemConstant.EXTERNAL_USER_TYPE),
                        "{0} = ANY(target_app_keys)", UserContext.getAppKey())
                .select(Announcement::getId, Announcement::getTitle, Announcement::getCover, Announcement::getCreateTime, Announcement::getUpdateTime)
                .orderByDesc(false, Announcement::getCreateTime)
                .list();
    }

    @Override
    public AnnouncementVO getDetail(Long announcementId) {
        boolean isLogin = UserContext.getUserId() != null;

        Announcement announcement = lambdaQuery()
                .eq(Announcement::getId, announcementId)
                .eq(Announcement::getDeleted, false)
                .eq(!isLogin, Announcement::getTargetUserType, null)
                .eq(isLogin, Announcement::getTargetUserType, UserContext.getUserType())
                // 如果是 EXTERNAL，只能看到 TargetAppKeys 包含 UserContext.getAppKey() 的公告
                .apply(isLogin && Objects.equals(UserContext.getUserType(), SystemConstant.EXTERNAL_USER_TYPE),
                        "{0} = ANY(target_app_keys)", UserContext.getAppKey())
                .one();

        if (announcement == null) {
            throw new BadRequestException("公告不存在或无权访问");
        }

        UserDTO info = userClient.queryUser(announcement.getPosterUserId()).getData();
        AnnouncementVO announcementVO = BeanUtil.copyProperties(announcement, AnnouncementVO.class);
        announcementVO.setPosterUsername(info.getUsername());
        announcementVO.setPosterAvatar(info.getAvatar());
        return announcementVO;
    }
}
