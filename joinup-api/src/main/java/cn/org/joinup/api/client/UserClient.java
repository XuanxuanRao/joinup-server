package cn.org.joinup.api.client;

import cn.org.joinup.api.dto.UserDTO;
import cn.org.joinup.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("/user/{id}")
    Result<UserDTO> queryUser(@PathVariable Long id);

    @GetMapping("/user/info")
    Result<UserDTO> getUserInfo();
}
