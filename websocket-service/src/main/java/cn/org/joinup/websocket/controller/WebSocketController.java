package cn.org.joinup.websocket.controller;

import cn.org.joinup.common.result.Result;
import cn.org.joinup.websocket.websocket.ChatWebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/ws")
@Slf4j
public class WebSocketController {
    @GetMapping("/user/online")
    public Result<Set<Long>> getOnlineUsers(String userType, String appKey) {
        if (userType == null) {
            return Result.error("userType must not be null");
        }
        return Result.success(ChatWebSocketServer.getOnlineUsers(userType, appKey));
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

    @PostMapping("/command/invoke")
    public Result<Void> pushCommand(Long userId, @RequestParam String command) {
        return Result.error("NOT IMPLEMENTED YET");
    }
}
