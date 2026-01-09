package cn.org.joinup.user.service.impl;

import cn.org.joinup.api.client.WebSocketClient;
import cn.org.joinup.api.dto.UserDTO;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.user.domain.po.User;
import cn.org.joinup.user.mapper.UserMapper;
import cn.org.joinup.user.service.IUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceUnitTest {

    @Mock
    private WebSocketClient webSocketClient;
    @Mock
    private IUserService userService;
    @Mock
    private UserMapper userMapper;

    private AdminUserServiceImpl adminUserService;

    @BeforeEach
    void setUp() {
        adminUserService = spy(new AdminUserServiceImpl(webSocketClient, userService));
        ReflectionTestUtils.setField(adminUserService, "baseMapper", userMapper);
    }

    @Test
    void testGetPageUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> pageResult = new Page<>();
        pageResult.setRecords(Collections.emptyList());

        doReturn(pageResult).when(adminUserService).page(any(Page.class));

        IPage<User> result = adminUserService.getPageUsers(pageable);

        assertNotNull(result);
        verify(adminUserService).page(any(Page.class));
    }

    @Test
    void testGetPageUsersSearchUsername() {
        String name = "test";
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> pageResult = new Page<>();
        pageResult.setRecords(Collections.singletonList(new User()));

        when(userMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(pageResult);

        IPage<User> result = adminUserService.getPageUsersSearchUsername(name, pageable);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        verify(userMapper).selectPage(any(Page.class), any(QueryWrapper.class));
    }

    @Test
    void testOnlineUsers() {
        // Arrange
        Long userId = 1L;
        Set<Long> onlineUsers = Collections.singleton(userId);
        Result<Set<Long>> webSocketResult = Result.success(onlineUsers);

        when(webSocketClient.getOnlineUsers(anyString(), anyString())).thenReturn(webSocketResult);

        User user = new User();
        user.setId(userId);
        user.setUsername("testUser");
        when(userService.getUserById(userId)).thenReturn(user);

        // Act
        List<UserDTO> result = adminUserService.onlineUsers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testUser", result.get(0).getUsername());
    }

    @Test
    void testRemoveOnlineUser() {
        Long userId = 1L;
        String connectType = "web";
        when(webSocketClient.removeOnlineUser(userId, connectType)).thenReturn(Result.success());

        boolean result = adminUserService.removeOnlineUser(userId, connectType);

        assertTrue(result);
    }
}
