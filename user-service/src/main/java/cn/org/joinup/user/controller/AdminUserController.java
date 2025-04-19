package cn.org.joinup.user.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.user.domain.dto.AdminUpdateUserDTO;
import cn.org.joinup.user.domain.po.User;
import cn.org.joinup.user.service.IAdminUserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final IAdminUserService iAdminUserService;

    // 计算数据数量
    @GetMapping("/count")
    public Result<Long> count() {
        Long count = iAdminUserService.count();
        return Result.success(count);
    }

    // 分页加载数据
    @GetMapping("/list")
    public IPage<User> list(Pageable pageable) {
        return iAdminUserService.getPageUsers(pageable);
    }

    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestBody Map<String, Long> body) {
        Long id = body.get("id");
        iAdminUserService.removeById(id);
        return Result.success();
    }

    @DeleteMapping("/delete/batch")
    public Result<Void> deleteBatch(@RequestBody Map<String, List<Long>> body) {
        List<Long> ids = body.get("ids");
        iAdminUserService.removeByIds(ids);
        return Result.success();
    }

    // 更新用户
    @PutMapping("/update/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody AdminUpdateUserDTO adminUpdateUserDTO) {
        User user = BeanUtil.copyProperties(adminUpdateUserDTO, User.class);
        user.setId(id);
        iAdminUserService.updateById(user);
        return Result.success();
    }

    // 根据主题名称模糊查询（用户名）
    @GetMapping("/querySearch")
    public Result<List<User>> querySearch(@RequestParam String username) {
        List<User> users = iAdminUserService.list();
        List<User> result = new ArrayList<>();
        for (User user : users) {
            if (user.getUsername().contains(username)) {
                result.add(user);
            }
        }

        return Result.success(result);
    }

    // 返回模糊查询数量（用户名）
    @GetMapping("/searchCountUsername")
    public Result<Long> searchCount(@RequestParam String username) {
        List<User> users = iAdminUserService.list();
        Long count = 0L;
        for (User user : users) {
            if (user.getUsername().contains(username)) {
                count++;
            }
        }
        return Result.success(count);
    }

    // 返回模糊查询数量（学号）
    @GetMapping("/searchCountStudentId")
    public Result<Long> searchCountStudentId(@RequestParam String studentId) {
        List<User> users = iAdminUserService.list();
        Long count = 0L;
        for (User user : users) {
            if (user.getStudentId()!= null && user.getStudentId().contains(studentId)) {
                count++;
            }
        }
        return Result.success(count);
    }

    // 分页显示模糊查询结果（用户名）
    @GetMapping("/searchUsername")
    public IPage<User> search(@RequestParam String username,
                               @RequestParam("page") int page, @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return iAdminUserService.getPageUsersSearchUsername(username, pageable);
    }

    // 分页显示模糊查询结果（学号）
    @GetMapping("/searchStudentId")
    public IPage<User> searchStudentId(@RequestParam String studentId,
                                        @RequestParam("page") int page, @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return iAdminUserService.getPageUsersSearchStudentId(studentId, pageable);
    }



}
