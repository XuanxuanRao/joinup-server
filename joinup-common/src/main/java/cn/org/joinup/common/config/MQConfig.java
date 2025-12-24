package cn.org.joinup.common.config;

import cn.org.joinup.common.util.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author chenxuanrao06@gmail.com
 */
@Component
@ConditionalOnClass(RabbitTemplate.class)
public class MQConfig {

    @Bean
    public MessageConverter messageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return new AuthMessageConverter(new Jackson2JsonMessageConverter(objectMapper));
    }

    static class AuthMessageConverter implements MessageConverter {
        private final MessageConverter delegate;

        public AuthMessageConverter(MessageConverter delegate) {
            this.delegate = delegate;
        }

        @Override
        public Message toMessage(Object o, MessageProperties messageProperties) throws MessageConversionException {
            Map<String, Object> headers = messageProperties.getHeaders();
            Long userId = UserContext.getUserId();
            if (userId != null) {
                headers.put("user-id", userId);
            }
            headers.put("user-role", UserContext.getUserRole());
            headers.put("app-key", UserContext.getAppKey());
            headers.put("user-type", UserContext.getUserType());
            return delegate.toMessage(o, messageProperties);
        }

        @Override
        public Object fromMessage(Message message) throws MessageConversionException {
            Object userId = message.getMessageProperties().getHeader("user-id");
            String appKey = message.getMessageProperties().getHeader("app-key");
            String role = message.getMessageProperties().getHeader("user-role");
            String userType = message.getMessageProperties().getHeader("user-type");
            if (userId != null) {
                UserContext.setUser((Long) userId, appKey, role, userType);
            }
            return delegate.fromMessage(message);
        }
    }
}
