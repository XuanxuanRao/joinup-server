package cn.org.joinup.websocket.controller;

import cn.org.joinup.common.result.Result;
import cn.org.joinup.websocket.websocket.ChatWebSocketServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/ws")
public class WebSocketController {
    @GetMapping("/user/online")
    public Result<Set<Long>> getOnlineUsers() {
        return Result.success(ChatWebSocketServer.getOnlineUsers());
    }
}
