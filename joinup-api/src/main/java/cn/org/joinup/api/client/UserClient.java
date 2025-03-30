package cn.org.joinup.api.client;

import cn.org.joinup.api.client.fallback.UserFallBackFactory;
import cn.org.joinup.api.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", fallbackFactory = UserFallBackFactory.class)
public interface UserClient {
    @GetMapping("/user/{id}")
    UserDTO queryUser(@PathVariable Long id);

    @GetMapping("/user/info")
    UserDTO getUserInfo();
}
