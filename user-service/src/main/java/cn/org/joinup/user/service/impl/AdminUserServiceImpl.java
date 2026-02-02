package cn.org.joinup.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.org.joinup.api.client.WebSocketClient;
import cn.org.joinup.api.dto.UserDTO;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.user.domain.dto.request.QueryUserInfoDTO;
import cn.org.joinup.user.domain.po.User;
import cn.org.joinup.user.domain.vo.AdminUserInfoVO;
import cn.org.joinup.user.enums.UserType;
import cn.org.joinup.user.mapper.UserMapper;
import cn.org.joinup.user.service.IAdminUserService;
import cn.org.joinup.user.service.IUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminUserServiceImpl extends ServiceImpl<UserMapper, User> implements IAdminUserService {

    private final WebSocketClient webSocketClient;

    private final IUserService userService;

    // 分页获取用户
    @Override
    public PageResult<AdminUserInfoVO> getPageUsers(QueryUserInfoDTO queryUserDTO) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(queryUserDTO.getUsername()), User::getUsername, queryUserDTO.getUsername());
        wrapper.like(StrUtil.isNotBlank(queryUserDTO.getEmail()), User::getEmail, queryUserDTO.getEmail());
        wrapper.eq(StrUtil.isNotBlank(queryUserDTO.getAppKey()), User::getAppKey, queryUserDTO.getAppKey());
        wrapper.eq(queryUserDTO.getVerified() != null, User::getVerified, queryUserDTO.getVerified());
        wrapper.eq(queryUserDTO.getUserType() != null, User::getUserType, queryUserDTO.getUserType());
        wrapper.orderByAsc(User::getCreateTime);

        Page<User> page = page(new Page<>(queryUserDTO.getPageNum(), queryUserDTO.getPageSize()), wrapper);
        return PageResult.of(page, (user) -> BeanUtil.copyProperties(user, AdminUserInfoVO.class));
    }

    @Override
    public List<UserDTO> onlineUsers() {
        return webSocketClient.getOnlineUsers(UserType.INTERNAL.getDesc(), "").getData()
                .stream()
                .map(userId -> {
                    User user = userService.getUserById(userId);
                    return BeanUtil.copyProperties(user, UserDTO.class);
                })
                .collect(Collectors.toList());
    }

    @Override
    public IPage<User> onlineUsersList(Pageable pageable) {
        Set<Long> onlineIds = webSocketClient.getOnlineUsers(UserType.INTERNAL.getDesc(), "").getData();
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

    @Override
    public boolean removeOnlineUser(Long userId, String connectType) {
        return Objects.equals(webSocketClient.removeOnlineUser(userId, connectType).getCode(), Result.SUCCESS);
    }

}
