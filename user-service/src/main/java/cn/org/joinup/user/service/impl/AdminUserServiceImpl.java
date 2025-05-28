package cn.org.joinup.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.api.client.WebSocketClient;
import cn.org.joinup.api.dto.UserDTO;
import cn.org.joinup.user.domain.po.User;
import cn.org.joinup.user.mapper.UserMapper;
import cn.org.joinup.user.service.IAdminUserService;
import cn.org.joinup.user.service.IUserService;
import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl extends ServiceImpl<UserMapper, User> implements IAdminUserService {

    private final WebSocketClient webSocketClient;

    private final IUserService userService;

    // 分页获取用户
    @Override
    public IPage<User> getPageUsers(Pageable pageable) {
        Page<User> page = new Page<>(pageable.getPageNumber(), pageable.getPageSize());
        return this.page(page);
    }

    @Override
    public IPage<User> getPageUsersSearchUsername(String name, Pageable pageable) {
        Page<User> page = new Page<>(pageable.getPageNumber(), pageable.getPageSize());

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), "username", name);

        return this.baseMapper.selectPage(page, wrapper);
    }

    @Override
    public IPage<User> getPageUsersSearchStudentId(String studentId, Pageable pageable) {
        Page<User> page = new Page<>(pageable.getPageNumber(), pageable.getPageSize());

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(studentId), "student_id", studentId);

        return this.baseMapper.selectPage(page, wrapper);
    }

    @Override
    public List<UserDTO> onlineUsers() {
        return webSocketClient.getOnlineUsers().getData()
                .stream()
                .map(userId -> {
                    User user = userService.getUserById(userId);
                    return BeanUtil.copyProperties(user, UserDTO.class);
                })
                .collect(Collectors.toList());
    }

    @Override
    public IPage<User> onlineUsersList(Pageable pageable) {
        Set<Long> onlineIds = webSocketClient.getOnlineUsers().getData();
        if (onlineIds == null || onlineIds.isEmpty()) {
            // MyBatis-Plus 的 Page 当前页从 1 开始，因此 +1
            return new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize(), 0);
        }

        Page<User> page = new Page<>(
                pageable.getPageNumber(),      // current
                pageable.getPageSize()             // size
        );

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(User::getId, onlineIds);

        return page(page, wrapper);

    }
}
