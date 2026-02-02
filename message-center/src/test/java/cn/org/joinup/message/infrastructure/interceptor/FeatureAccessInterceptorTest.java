package cn.org.joinup.message.infrastructure.interceptor;

import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.message.infrastructure.annotation.SkipFeatureCheck;
import cn.org.joinup.message.application.feature.service.IFeatureService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FeatureAccessInterceptorTest {

    private FeatureAccessInterceptor interceptor;

    @Mock
    private IFeatureService featureService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HandlerMethod handlerMethod;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        interceptor = new FeatureAccessInterceptor(featureService, objectMapper);
    }

    @AfterEach
    void tearDown() {
        UserContext.removeUser();
    }

    @Test
    @DisplayName("Should pass when handler has @SkipFeatureCheck annotation")
    void testPreHandle_SkipFeatureCheck() throws Exception {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        when(handlerMethod.hasMethodAnnotation(SkipFeatureCheck.class)).thenReturn(true);

        // Act
        boolean result = interceptor.preHandle(request, response, handlerMethod);

        // Assert
        assertTrue(result);
        verifyNoInteractions(featureService);
    }

    @Test
    @DisplayName("Should pass when URL does not match feature pattern")
    void testPreHandle_NotFeatureUrl() throws Exception {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/message/other/endpoint");
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        when(handlerMethod.hasMethodAnnotation(SkipFeatureCheck.class)).thenReturn(false);

        // Act
        boolean result = interceptor.preHandle(request, response, handlerMethod);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Should fail when user is not logged in")
    void testPreHandle_NoUser() throws Exception {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/message/feature/rate-monitor/test");
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        when(handlerMethod.hasMethodAnnotation(SkipFeatureCheck.class)).thenReturn(false);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"code\":500,\"msg\":\"Login required\"}");

        // Act
        boolean result = interceptor.preHandle(request, response, handlerMethod);

        // Assert
        assertFalse(result);
        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
    }

    @Test
    @DisplayName("Should fail when access is denied by service")
    void testPreHandle_AccessDenied() throws Exception {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/message/feature/rate-monitor/test");
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        UserContext.setUser(1L, "app", "user", "1");
        
        when(handlerMethod.hasMethodAnnotation(SkipFeatureCheck.class)).thenReturn(false);
        when(featureService.canAccess("rate-monitor", 1L)).thenReturn(false);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"code\":403,\"msg\":\"Access denied\"}");

        // Act
        boolean result = interceptor.preHandle(request, response, handlerMethod);

        // Assert
        assertFalse(result);
        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
        verify(featureService).canAccess("rate-monitor", 1L);
    }

    @Test
    @DisplayName("Should pass when access is granted by service")
    void testPreHandle_AccessGranted() throws Exception {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/message/feature/rate-monitor/test");
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        UserContext.setUser(1L, "app", "user", "1");
        
        when(handlerMethod.hasMethodAnnotation(SkipFeatureCheck.class)).thenReturn(false);
        when(featureService.canAccess("rate-monitor", 1L)).thenReturn(true);

        // Act
        boolean result = interceptor.preHandle(request, response, handlerMethod);

        // Assert
        assertTrue(result);
        verify(featureService).canAccess("rate-monitor", 1L);
    }
}
