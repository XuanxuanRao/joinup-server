package cn.org.joinup.api.client;

import cn.org.joinup.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Set;

@FeignClient("websocket-service")
public interface WebSocketClient {

    @GetMapping("/ws/user/online")
    Result<Set<Long>> getOnlineUsers();

}
