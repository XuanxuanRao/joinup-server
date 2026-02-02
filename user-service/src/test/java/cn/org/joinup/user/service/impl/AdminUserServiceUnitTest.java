package cn.org.joinup.user.service.impl;

import cn.org.joinup.api.client.WebSocketClient;
import cn.org.joinup.api.dto.UserDTO;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.user.domain.dto.request.QueryUserInfoDTO;
import cn.org.joinup.user.domain.po.User;
import cn.org.joinup.user.domain.vo.AdminUserInfoVO;
import cn.org.joinup.user.enums.UserType;
import cn.org.joinup.user.service.IUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AdminUserService单元测试
 * @author chenxuanrao06@gmail.com
 */
@ExtendWith(MockitoExtension.class)
class AdminUserServiceUnitTest {

    @Mock
    private WebSocketClient webSocketClient;
    
    @Mock
    private IUserService userService;
    
    private AdminUserServiceImpl adminUserService;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 初始化测试用户
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        testUser.setAppKey("test-app-key");
        testUser.setVerified(true);
        testUser.setUserType(UserType.INTERNAL);
        
        // 初始化adminUserService为spy对象
        adminUserService = org.mockito.Mockito.spy(new AdminUserServiceImpl(webSocketClient, userService));
    }

    @Test
    @DisplayName("分页获取用户列表 - 基本场景")
    void testGetPageUsers_Basic() {
        // 准备测试数据
        QueryUserInfoDTO queryDTO = new QueryUserInfoDTO();
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        // 模拟分页结果
        Page<User> pageResult = new Page<>();
        pageResult.setRecords(Collections.singletonList(testUser));
        pageResult.setTotal(1L);
        pageResult.setSize(10L);
        pageResult.setCurrent(1L);

        // 模拟方法调用
        doReturn(pageResult).when(adminUserService).page(any(Page.class), any(LambdaQueryWrapper.class));

        // 执行测试
        PageResult<AdminUserInfoVO> result = adminUserService.getPageUsers(queryDTO);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getList().size());
        assertEquals(testUser.getUsername(), result.getList().get(0).getUsername());
        verify(adminUserService).page(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("分页获取用户列表 - 带条件搜索")
    void testGetPageUsers_WithConditions() {
        // 准备测试数据
        QueryUserInfoDTO queryDTO = new QueryUserInfoDTO();
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);
        queryDTO.setUsername("test");
        queryDTO.setEmail("example");
        queryDTO.setAppKey("test-app-key");
        queryDTO.setVerified(true);
        queryDTO.setUserType(UserType.INTERNAL);

        // 模拟分页结果
        Page<User> pageResult = new Page<>();
        pageResult.setRecords(Collections.singletonList(testUser));
        pageResult.setTotal(1L);

        // 模拟方法调用
        doReturn(pageResult).when(adminUserService).page(any(Page.class), any(LambdaQueryWrapper.class));

        // 执行测试
        PageResult<AdminUserInfoVO> result = adminUserService.getPageUsers(queryDTO);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        verify(adminUserService).page(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("分页获取用户列表 - 空结果")
    void testGetPageUsers_EmptyResult() {
        // 准备测试数据
        QueryUserInfoDTO queryDTO = new QueryUserInfoDTO();
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        // 模拟分页结果
        Page<User> pageResult = new Page<>();
        pageResult.setRecords(Collections.emptyList());
        pageResult.setTotal(0L);

        // 模拟方法调用
        doReturn(pageResult).when(adminUserService).page(any(Page.class), any(LambdaQueryWrapper.class));

        // 执行测试
        PageResult<AdminUserInfoVO> result = adminUserService.getPageUsers(queryDTO);

        // 验证结果
        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertTrue(result.getList().isEmpty());
        verify(adminUserService).page(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("获取在线用户列表")
    void testOnlineUsers() {
        // 准备测试数据
        Long userId = 1L;
        Set<Long> onlineUsers = Collections.singleton(userId);
        Result<Set<Long>> webSocketResult = Result.success(onlineUsers);

        // 模拟WebSocket客户端调用
        when(webSocketClient.getOnlineUsers(anyString(), anyString())).thenReturn(webSocketResult);

        // 模拟用户服务调用
        when(userService.getUserById(userId)).thenReturn(testUser);

        // 执行测试
        List<UserDTO> result = adminUserService.onlineUsers();

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getUsername(), result.get(0).getUsername());
        verify(webSocketClient).getOnlineUsers(anyString(), anyString());
        verify(userService).getUserById(userId);
    }

    @Test
    @DisplayName("获取在线用户列表 - 空结果")
    void testOnlineUsers_EmptyResult() {
        // 模拟WebSocket客户端调用返回空结果
        Result<Set<Long>> webSocketResult = Result.success(Collections.emptySet());
        when(webSocketClient.getOnlineUsers(anyString(), anyString())).thenReturn(webSocketResult);

        // 执行测试
        List<UserDTO> result = adminUserService.onlineUsers();

        // 验证结果
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(webSocketClient).getOnlineUsers(anyString(), anyString());
        verify(userService, never()).getUserById(anyLong());
    }

    @Test
    @DisplayName("分页获取在线用户列表")
    void testOnlineUsersList() {
        // 准备测试数据
        Long userId = 1L;
        Set<Long> onlineUsers = Collections.singleton(userId);
        Result<Set<Long>> webSocketResult = Result.success(onlineUsers);

        Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);

        // 模拟WebSocket客户端调用
        when(webSocketClient.getOnlineUsers(anyString(), anyString())).thenReturn(webSocketResult);

        // 模拟分页结果
        Page<User> pageResult = new Page<>();
        pageResult.setRecords(Collections.singletonList(testUser));
        pageResult.setTotal(1L);

        // 模拟方法调用
        doReturn(pageResult).when(adminUserService).page(any(Page.class), any(LambdaQueryWrapper.class));

        // 执行测试
        IPage<User> result = adminUserService.onlineUsersList(pageable);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        verify(webSocketClient).getOnlineUsers(anyString(), anyString());
        verify(adminUserService).page(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("分页获取在线用户列表 - 无在线用户")
    void testOnlineUsersList_NoOnlineUsers() {
        // 模拟WebSocket客户端调用返回空结果
        Result<Set<Long>> webSocketResult = Result.success(Collections.emptySet());
        when(webSocketClient.getOnlineUsers(anyString(), anyString())).thenReturn(webSocketResult);

        Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);

        // 执行测试
        IPage<User> result = adminUserService.onlineUsersList(pageable);

        // 验证结果
        assertNotNull(result);
        assertEquals(0, result.getTotal());
        verify(webSocketClient).getOnlineUsers(anyString(), anyString());
        verify(adminUserService, never()).page(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("移除在线用户 - 成功")
    void testRemoveOnlineUser_Success() {
        // 准备测试数据
        Long userId = 1L;
        String connectType = "web";

        // 模拟WebSocket客户端调用返回成功
        when(webSocketClient.removeOnlineUser(userId, connectType)).thenReturn(Result.success());

        // 执行测试
        boolean result = adminUserService.removeOnlineUser(userId, connectType);

        // 验证结果
        assertTrue(result);
        verify(webSocketClient).removeOnlineUser(userId, connectType);
    }

    @Test
    @DisplayName("移除在线用户 - 失败")
    void testRemoveOnlineUser_Failure() {
        // 准备测试数据
        Long userId = 1L;
        String connectType = "web";

        // 模拟WebSocket客户端调用返回失败
        when(webSocketClient.removeOnlineUser(userId, connectType)).thenReturn(Result.error("Failed to remove user"));

        // 执行测试
        boolean result = adminUserService.removeOnlineUser(userId, connectType);

        // 验证结果
        assertFalse(result);
        verify(webSocketClient).removeOnlineUser(userId, connectType);
    }
}
