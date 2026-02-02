package cn.org.joinup.user.controller.admin;

import cn.org.joinup.api.dto.UserDTO;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.user.domain.dto.AdminUpdateUserDTO;
import cn.org.joinup.user.domain.dto.request.QueryUserInfoDTO;
import cn.org.joinup.user.domain.po.User;
import cn.org.joinup.user.domain.vo.AdminUserInfoVO;
import cn.org.joinup.user.service.IAdminUserService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final IAdminUserService adminUserService;

    // 计算数据数量
    @GetMapping("/count")
    public Result<Long> count() {
        Long count = adminUserService.count();
        return Result.success(count);
    }

    // 分页加载数据
    @GetMapping("/list")
    public Result<PageResult<AdminUserInfoVO>> list(QueryUserInfoDTO queryUserDTO) {
        try {
            return Result.success(adminUserService.getPageUsers(queryUserDTO));
        } catch (Exception e) {
            log.error("admin list users error", e);
            return Result.error(e.getMessage());
        }
    }

    // TODO 取消认证需要通知课程服务
    // 更新用户
    @PutMapping("/update/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody AdminUpdateUserDTO adminUpdateUserDTO) {
        UpdateWrapper<User> uw = new UpdateWrapper<>();
        uw.eq("id", id)
                .set("username", adminUpdateUserDTO.getUsername())
                .set("verified", adminUpdateUserDTO.getVerified())
                .set("update_time", LocalDateTime.now());  // 假设你也记录更新时间

        adminUserService.update(null, uw);
        return Result.success();
    }

    @GetMapping("/online")
    public Result<List<UserDTO>> getOnlineUsers() {
        return Result.success(adminUserService.onlineUsers());
    }

    @GetMapping("/online/list")
    public IPage<User> listOnlineUsers(Pageable pageable) {
        return adminUserService.onlineUsersList(pageable);

    }

    @GetMapping("/online/count")
    public Result<Long> countOnlineUsers() {
        return Result.success((long) adminUserService.onlineUsers().size());
    }

    @DeleteMapping("/online/{userId}")
    public Result<Void> disconnect(@PathVariable Long userId,
                                   @RequestParam(required = false, defaultValue = "chat") String connectType) {
        if (adminUserService.removeOnlineUser(userId, connectType)) {
            return Result.success();
        }
        return Result.error("用户不存在或未连接");
    }


}
