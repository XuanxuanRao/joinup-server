package cn.org.joinup.websocket.service;

import cn.org.joinup.api.dto.CommandRequestDTO;
import cn.org.joinup.common.exception.BadRequestException;
import cn.org.joinup.websocket.domain.CommandDTO;
import cn.org.joinup.websocket.domain.CommandExecutionResult;
import cn.org.joinup.websocket.websocket.CommandWebSocketServer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
public class CommandWebSocketProxyService {

    // 存储等待响应的请求
    private final ConcurrentHashMap<String, CompletableFuture<CommandExecutionResult>> pendingRequests =
            new ConcurrentHashMap<>();

    // 超时时间（秒）
    @Value("${websocket.timeout:5}")
    private int timeoutSeconds;

    /**
     * 发送WebSocket请求并等待响应（无感知调用）
     */
    public CommandExecutionResult sendCommand(CommandRequestDTO commandRequestDTO) {
        String commandId = commandRequestDTO.getCommandType() + "-" + UUID.randomUUID();

        try {
            // 创建CompletableFuture用于等待响应
            CompletableFuture<CommandExecutionResult> future = new CompletableFuture<>();
            pendingRequests.put(commandId, future);

            try {
                // 发送请求
                CommandWebSocketServer.sendCommandToUser(
                        commandRequestDTO.getExecutorId(),
                        CommandDTO.builder()
                                .commandId(commandId)
                                .commandType(commandRequestDTO.getCommandType())
                                .params(commandRequestDTO.getParams())
                                .build());

                // 等待响应，设置超时
                return future.get(timeoutSeconds, TimeUnit.SECONDS);

            } catch (TimeoutException e) {
                log.error("WebSocket请求超时，executorId: {}, commandId: {}", commandRequestDTO.getExecutorId(), commandId);
                throw new BadRequestException("Request timeout");
            } catch (Exception e) {
                log.error("WebSocket请求失败", e);
                throw new BadRequestException("Failed to send command", e);
            } finally {
                pendingRequests.remove(commandId);
            }

        } catch (Exception e) {
            log.error("WebSocket代理服务异常", e);
            throw new RuntimeException("WebSocket proxy error", e);
        }
    }

    /**
     * 处理WebSocket响应
     */
    public void handleResponse(CommandExecutionResult commandExecutionResult) {
        CompletableFuture<CommandExecutionResult> future = pendingRequests.get(commandExecutionResult.getCommandId());
        if (future != null) {
            future.complete(commandExecutionResult);
        } else {
            log.warn("收到未知commandId的响应: {}", commandExecutionResult.getCommandId());
        }
    }
}