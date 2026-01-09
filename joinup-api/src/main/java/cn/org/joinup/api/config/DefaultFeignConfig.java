package cn.org.joinup.api.config;

import cn.org.joinup.common.constant.SystemConstant;
import cn.org.joinup.common.util.UserContext;
import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

/**
 * @author chenxuanrao06@gmail.com
 */
public class DefaultFeignConfig {
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    public RequestInterceptor userInfoRequestInterceptor() {
        return requestTemplate -> {
            Long userId = UserContext.getUserId();
            if (userId != null) {
                requestTemplate.header(SystemConstant.USER_ID_HEADER_NAME, userId.toString());
            }
            requestTemplate.header(SystemConstant.USER_ROLE_HEADER_NAME, String.valueOf(UserContext.getUserRole()));
            requestTemplate.header(SystemConstant.APP_KEY_HEADER_NAME, UserContext.getAppKey());
            requestTemplate.header(SystemConstant.USER_TYPE_HEADER_NAME, String.valueOf(UserContext.getUserType()));
        };
    }

}
