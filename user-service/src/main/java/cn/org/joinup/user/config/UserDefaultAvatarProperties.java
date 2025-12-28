package cn.org.joinup.user.config;

import cn.org.joinup.user.enums.UserType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 *
 * @author chenxuanrao06@gmail.com
 */
@Data
@Component
@ConfigurationProperties(prefix = "user.avatar")
public class UserDefaultAvatarProperties {
    private Map<String, String> externalConfig;
    private String defaultAvatar;

    public String getAvatar(UserType userType, String appKey) {
        if (UserType.INTERNAL == userType) {
            return defaultAvatar;
        } else {
            return externalConfig.getOrDefault(appKey, defaultAvatar);
        }
    }
}
