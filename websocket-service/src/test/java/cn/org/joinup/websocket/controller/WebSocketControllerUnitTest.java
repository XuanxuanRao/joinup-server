package cn.org.joinup.websocket.controller;

import cn.org.joinup.api.dto.CommandRequestDTO;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.websocket.constant.WebSocketServiceTypeConstant;
import cn.org.joinup.websocket.domain.CommandExecutionResult;
import cn.org.joinup.websocket.service.CommandWebSocketProxyService;
import cn.org.joinup.websocket.websocket.ChatWebSocketServer;
import cn.org.joinup.websocket.websocket.CommandWebSocketServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebSocketControllerUnitTest {

    @Mock
    private CommandWebSocketProxyService commandWebSocketProxyService;

    private WebSocketController controller;
    private MockedStatic<ChatWebSocketServer> chatServerMock;
    private MockedStatic<CommandWebSocketServer> commandServerMock;

    @BeforeEach
    void setUp() {
        controller = new WebSocketController(commandWebSocketProxyService);
        chatServerMock = Mockito.mockStatic(ChatWebSocketServer.class);
        commandServerMock = Mockito.mockStatic(CommandWebSocketServer.class);
    }

    @AfterEach
    void tearDown() {
        chatServerMock.close();
        commandServerMock.close();
    }

    @Test
    void testGetOnlineUsers() {
        // Arrange
        String userType = "INTERNAL";
        String appKey = "app1";
        
        Set<Long> chatUsers = new HashSet<>();
        chatUsers.add(1L);
        chatUsers.add(2L);
        
        Set<Long> commandUsers = new HashSet<>();
        commandUsers.add(2L);
        commandUsers.add(3L);

        chatServerMock.when(() -> ChatWebSocketServer.getOnlineUsers(userType, appKey)).thenReturn(chatUsers);
        commandServerMock.when(() -> CommandWebSocketServer.getOnlineUsers(userType, appKey)).thenReturn(commandUsers);

        // Act
        Result<Set<Long>> result = controller.getOnlineUsers(userType, appKey);

        // Assert
        assertEquals(Result.SUCCESS, result.getCode());
        Set<Long> users = result.getData();
        assertEquals(3, users.size()); // 1, 2, 3
        assertTrue(users.contains(1L));
        assertTrue(users.contains(2L));
        assertTrue(users.contains(3L));
    }

    @Test
    void testKickUserConnection_Chat() {
        // Arrange
        Long userId = 1L;
        String connectType = WebSocketServiceTypeConstant.CHAT;
        
        chatServerMock.when(() -> ChatWebSocketServer.forceDisconnect(eq(userId), anyString())).thenReturn(true);

        // Act
        Result<Void> result = controller.kickUserConnection(userId, connectType);

        // Assert
        assertEquals(Result.SUCCESS, result.getCode());
        chatServerMock.verify(() -> ChatWebSocketServer.forceDisconnect(eq(userId), anyString()));
    }

    @Test
    void testKickUserConnection_Command() {
        // Arrange
        Long userId = 1L;
        String connectType = WebSocketServiceTypeConstant.COMMAND;
        
        commandServerMock.when(() -> CommandWebSocketServer.forceDisconnect(eq(userId), anyString())).thenReturn(true);

        // Act
        Result<Void> result = controller.kickUserConnection(userId, connectType);

        // Assert
        assertEquals(Result.SUCCESS, result.getCode());
        commandServerMock.verify(() -> CommandWebSocketServer.forceDisconnect(eq(userId), anyString()));
    }
    
    @Test
    void testKickUserConnection_NotFound() {
        // Arrange
        Long userId = 1L;
        String connectType = WebSocketServiceTypeConstant.CHAT;
        
        chatServerMock.when(() -> ChatWebSocketServer.forceDisconnect(eq(userId), anyString())).thenReturn(false);

        // Act
        Result<Void> result = controller.kickUserConnection(userId, connectType);

        // Assert
        assertEquals(Result.ERROR, result.getCode());
        assertTrue(result.getMsg().contains("is not online"));
    }

    @Test
    void testPushCommand() {
        // Arrange
        CommandRequestDTO request = new CommandRequestDTO();
        CommandExecutionResult expectedResult = new CommandExecutionResult();
        expectedResult.setSuccess(true);

        when(commandWebSocketProxyService.sendCommand(request)).thenReturn(expectedResult);

        // Act
        Result<CommandExecutionResult> result = controller.pushCommand(request);

        // Assert
        assertEquals(Result.SUCCESS, result.getCode());
        verify(commandWebSocketProxyService).sendCommand(request);
    }
}
