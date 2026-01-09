package cn.org.joinup.websocket.service;

import cn.org.joinup.api.dto.CommandRequestDTO;
import cn.org.joinup.common.exception.BadRequestException;
import cn.org.joinup.websocket.domain.CommandDTO;
import cn.org.joinup.websocket.domain.CommandExecutionResult;
import cn.org.joinup.websocket.websocket.CommandWebSocketServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class CommandWebSocketProxyServiceUnitTest {

    private CommandWebSocketProxyService service;
    private MockedStatic<CommandWebSocketServer> commandWebSocketServerMock;

    @BeforeEach
    void setUp() {
        service = new CommandWebSocketProxyService();
        // Set timeout to a small value for testing
        ReflectionTestUtils.setField(service, "timeoutSeconds", 2);
        
        commandWebSocketServerMock = Mockito.mockStatic(CommandWebSocketServer.class);
    }

    @AfterEach
    void tearDown() {
        commandWebSocketServerMock.close();
    }

    @Test
    void testSendCommand_Success() {
        // Arrange
        Long executorId = 123L;
        String commandType = "TEST_CMD";
        CommandRequestDTO request = new CommandRequestDTO();
        request.setExecutorId(executorId);
        request.setCommandType(commandType);
        request.setParams(new HashMap<>());

        CommandExecutionResult expectedResult = new CommandExecutionResult();
        expectedResult.setSuccess(true);
        expectedResult.setMessage("Success");

        // Mock sendCommandToUser to immediately trigger handleResponse
        commandWebSocketServerMock.when(() -> CommandWebSocketServer.sendCommandToUser(anyLong(), any(CommandDTO.class)))
                .thenAnswer(invocation -> {
                    CommandDTO cmd = invocation.getArgument(1);
                    // Construct result with the same commandId
                    CommandExecutionResult result = new CommandExecutionResult();
                    result.setCommandId(cmd.getCommandId());
                    result.setSuccess(true);
                    result.setMessage("Success");
                    
                    // Simulate async response handling
                    service.handleResponse(result);
                    return null;
                });

        // Act
        CommandExecutionResult result = service.sendCommand(request);

        // Assert
        assertNotNull(result);
        assertEquals(true, result.getSuccess());
        assertEquals("Success", result.getMessage());
        
        commandWebSocketServerMock.verify(() -> CommandWebSocketServer.sendCommandToUser(anyLong(), any(CommandDTO.class)));
    }

    @Test
    void testSendCommand_Timeout() {
        // Arrange
        Long executorId = 123L;
        CommandRequestDTO request = new CommandRequestDTO();
        request.setExecutorId(executorId);
        request.setCommandType("TIMEOUT_CMD");
        
        // Reduce timeout for faster test
        ReflectionTestUtils.setField(service, "timeoutSeconds", 1);

        // Do nothing when sendCommandToUser is called (simulate no response)
        commandWebSocketServerMock.when(() -> CommandWebSocketServer.sendCommandToUser(anyLong(), any(CommandDTO.class)))
                .thenAnswer(invocation -> null);

        // Act & Assert
        // The service wraps BadRequestException into RuntimeException in the outer catch block
        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.sendCommand(request));
        assertEquals("WebSocket proxy error", exception.getMessage());
        assertInstanceOf(BadRequestException.class, exception.getCause());
        assertEquals("Request timeout", exception.getCause().getMessage());
    }

    @Test
    void testHandleResponse_UnknownId() {
        // Arrange
        CommandExecutionResult result = new CommandExecutionResult();
        result.setCommandId("unknown-id");

        // Act & Assert
        // Should simply log a warning and not throw exception
        assertDoesNotThrow(() -> service.handleResponse(result));
    }
}
