package cn.org.joinup.api.config;

import cn.org.joinup.api.client.fallback.UserFallBackFactory;
import cn.org.joinup.api.client.fallback.EmailFallBackFactory;
import cn.org.joinup.common.constant.SystemConstant;
import cn.org.joinup.common.util.UserContext;
import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

/**
 * @author chenxuanrao06@gmail.com
 * @Description:
 */
public class DefaultFeignConfig {
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor userInfoRequestInterceptor() {
        return requestTemplate -> {
            Long userId = UserContext.getUser();
            if (userId != null) {
                requestTemplate.header(SystemConstant.USER_ID_NAME, userId.toString());
            }
        };
    }


    @Bean
    public EmailFallBackFactory emailFallBackFactory() {
        return new EmailFallBackFactory();
    }

    @Bean
    public UserFallBackFactory userFallBackFactory() {
        return new UserFallBackFactory();
    }

}
