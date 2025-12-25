package cn.org.joinup.api.client;

import cn.org.joinup.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@FeignClient("websocket-service")
public interface WebSocketClient {

    @GetMapping("/ws/user/online")
    Result<Set<Long>> getOnlineUsers(@RequestParam String userType, @RequestParam(required = false) String appKey);

    @PostMapping("/ws/command/invoke")
    Result<Void> pushCommand(@RequestParam Long userId, @RequestParam String command);

}
