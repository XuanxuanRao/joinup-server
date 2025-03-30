package cn.org.joinup.api.client.fallback;

import cn.org.joinup.api.client.UserClient;
import cn.org.joinup.api.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * @author chenxuanrao06@gmail.com
 * @Description:
 */
@Slf4j
public class UserFallBackFactory implements FallbackFactory<UserClient> {
    @Override
    public UserClient create(Throwable cause) {
        return new UserClient() {
            @Override
            public UserDTO queryUser(Long id) {
                log.error("query failed, reason was: {}", cause.getMessage());
                return null;
            }

            @Override
            public UserDTO getUserInfo() {
                log.error("query failed, reason was: {}", cause.getMessage());
                return null;
            }
        };
    }
}
