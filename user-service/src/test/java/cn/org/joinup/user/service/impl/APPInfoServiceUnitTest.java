package cn.org.joinup.user.service.impl;

import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.user.domain.dto.request.AdminUpdateAPPInfoRequestDTO;
import cn.org.joinup.user.domain.po.APPInfo;
import cn.org.joinup.user.domain.po.User;
import cn.org.joinup.user.domain.vo.APPInfoVO;
import cn.org.joinup.user.mapper.APPInfoMapper;
import cn.org.joinup.user.service.IUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class APPInfoServiceUnitTest {

    @Mock
    private IUserService userService;
    @Mock
    private APPInfoMapper appInfoMapper;

    private APPInfoServiceImpl appInfoService;

    @BeforeEach
    void setUp() {
        appInfoService = spy(new APPInfoServiceImpl(userService));
        ReflectionTestUtils.setField(appInfoService, "baseMapper", appInfoMapper);
    }

    @Test
    void testGetActiveAPPInfo_Found() {
        String appKey = "testAppKey";
        APPInfo appInfo = new APPInfo();
        appInfo.setAppKey(appKey);
        appInfo.setEnabled(true);
        appInfo.setDeleted(false);

        // Mock lambdaQuery chain
        LambdaQueryChainWrapper<APPInfo> queryChainWrapper = mock(LambdaQueryChainWrapper.class);
        doReturn(queryChainWrapper).when(appInfoService).lambdaQuery();
        when(queryChainWrapper.eq(any(), any())).thenReturn(queryChainWrapper);
        when(queryChainWrapper.one()).thenReturn(appInfo);

        Optional<APPInfo> result = appInfoService.getActiveAPPInfo(appKey);

        assertTrue(result.isPresent());
        assertEquals(appKey, result.get().getAppKey());
    }

    @Test
    void testGetActiveAPPInfo_NotFound() {
        String appKey = "nonExistent";

        // Mock lambdaQuery chain
        LambdaQueryChainWrapper<APPInfo> queryChainWrapper = mock(LambdaQueryChainWrapper.class);
        doReturn(queryChainWrapper).when(appInfoService).lambdaQuery();
        when(queryChainWrapper.eq(any(), any())).thenReturn(queryChainWrapper);
        when(queryChainWrapper.one()).thenReturn(null);

        Optional<APPInfo> result = appInfoService.getActiveAPPInfo(appKey);

        assertTrue(result.isEmpty());
    }

    @Test
    void testList() {
        // Arrange
        APPInfo appInfo = new APPInfo();
        appInfo.setAppKey("testApp");
        appInfo.setCreateTime(LocalDateTime.now());
        
        Page<APPInfo> pageResult = new Page<>();
        pageResult.setRecords(Collections.singletonList(appInfo));
        pageResult.setTotal(1);

        // Mock page method
        doReturn(pageResult).when(appInfoService).page(any(Page.class), any(LambdaQueryWrapper.class));

        // Mock userService count
        LambdaQueryChainWrapper<User> userQueryChainWrapper = mock(LambdaQueryChainWrapper.class);
        when(userService.lambdaQuery()).thenReturn(userQueryChainWrapper);
        when(userQueryChainWrapper.eq(any(), any())).thenReturn(userQueryChainWrapper);
        when(userQueryChainWrapper.count()).thenReturn(10L);

        // Act
        PageResult<APPInfoVO> result = appInfoService.list(true, 1, 10);

        // Assert
        assertNotNull(result);
//        assertEquals(1, result.getRows().size());
//        assertEquals(10L, result.getRows().get(0).getUsersCount());
    }

    @Test
    void testUpdateAPPInfo_Success() {
        String appKey = "testApp";
        AdminUpdateAPPInfoRequestDTO updateDTO = new AdminUpdateAPPInfoRequestDTO();

        APPInfo existingApp = new APPInfo();
        existingApp.setAppKey(appKey);

        // Mock lambdaQuery chain to find app
        LambdaQueryChainWrapper<APPInfo> queryChainWrapper = mock(LambdaQueryChainWrapper.class);
        doReturn(queryChainWrapper).when(appInfoService).lambdaQuery();
        when(queryChainWrapper.eq(any(), any())).thenReturn(queryChainWrapper);
        when(queryChainWrapper.one()).thenReturn(existingApp);

        // Mock updateById
        doReturn(true).when(appInfoService).updateById(any(APPInfo.class));

        APPInfo result = appInfoService.updateAPPInfo(appKey, updateDTO);

        assertNotNull(result);
        verify(appInfoService).updateById(existingApp);
    }
    
    @Test
    void testUpdateAPPInfo_NotFound() {
        String appKey = "nonExistent";
        AdminUpdateAPPInfoRequestDTO updateDTO = new AdminUpdateAPPInfoRequestDTO();

        // Mock lambdaQuery chain to find nothing
        LambdaQueryChainWrapper<APPInfo> queryChainWrapper = mock(LambdaQueryChainWrapper.class);
        doReturn(queryChainWrapper).when(appInfoService).lambdaQuery();
        when(queryChainWrapper.eq(any(), any())).thenReturn(queryChainWrapper);
        when(queryChainWrapper.one()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> 
            appInfoService.updateAPPInfo(appKey, updateDTO)
        );
    }
}
