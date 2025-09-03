package cn.org.joinup.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.toggle.register")
public class UserRegisterProperties {
    private Boolean wxEnabled;
    private Boolean emailEnabled;
}
