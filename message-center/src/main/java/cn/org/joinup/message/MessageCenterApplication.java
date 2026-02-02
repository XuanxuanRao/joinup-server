package cn.org.joinup.message;

import cn.org.joinup.api.config.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author chenxuanrao06@gmail.com
 */
@SpringBootApplication
@MapperScan("cn.org.joinup.message.infrastructure.repository")
@EnableFeignClients(basePackages = "cn.org.joinup.api.client", defaultConfiguration = DefaultFeignConfig.class)
@EnableConfigurationProperties
@EnableScheduling
public class MessageCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageCenterApplication.class, args);
    }

}
