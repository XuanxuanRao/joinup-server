package cn.org.joinup.websocket.controller;

import cn.org.joinup.api.dto.CommandRequestDTO;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.websocket.domain.CommandDTO;
import cn.org.joinup.websocket.domain.CommandExecutionResult;
import cn.org.joinup.websocket.service.CommandWebSocketProxyService;
import cn.org.joinup.websocket.websocket.ChatWebSocketServer;
import cn.org.joinup.websocket.websocket.CommandWebSocketServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/ws")
@Slf4j
@RequiredArgsConstructor
public class WebSocketController {

    private final CommandWebSocketProxyService  commandWebSocketProxyService;

    @GetMapping("/user/online")
    public Result<Set<Long>> getOnlineUsers(String userType, String appKey) {
        if (userType == null) {
            return Result.error("userType must not be null");
        }
        Set<Long> onlineUsers = new HashSet<>();
        onlineUsers.addAll(ChatWebSocketServer.getOnlineUsers(userType, appKey));
        onlineUsers.addAll(CommandWebSocketServer.getOnlineUsers(userType, appKey));
        return Result.success(onlineUsers);
    }

    @DeleteMapping("/user/{userId}")
    public Result<Void> kickUserConnection(@PathVariable Long userId) {
        boolean success = ChatWebSocketServer.forceDisconnect(userId, "Admin killed the connection for dev");
        if (success) {
            log.info("User with ID {} has been kicked from WebSocket connection.", userId);
            return Result.success();
        } else {
            return Result.error("User with ID " + userId + " is not online or does not exist.");
        }
    }

    @PostMapping("/command/execute")
    public Result<CommandExecutionResult> pushCommand(@RequestBody @Validated CommandRequestDTO commandRequestDTO) {
        try {
            return Result.success(commandWebSocketProxyService.sendCommand(commandRequestDTO));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    private CommandDTO convertToCommandDTO(CommandRequestDTO commandRequestDTO) {
        CommandDTO commandDTO = new CommandDTO();
        commandDTO.setCommandType(commandRequestDTO.getCommandType());
        commandDTO.setParams(commandRequestDTO.getParams());
        return commandDTO;
    }
}
