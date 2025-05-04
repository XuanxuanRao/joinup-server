package cn.org.joinup.gateway.filters;

import cn.org.joinup.common.constant.SystemConstant;
import cn.org.joinup.gateway.constants.MQConstant;
import cn.org.joinup.gateway.domain.LogEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 用于记录接口的请求日志
 * @author chenxuanrao06@gmail.com
 */
@Component
@RequiredArgsConstructor
public class LoggingFilter implements GlobalFilter, Ordered {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        LocalDateTime start = LocalDateTime.now();

        return chain.filter(exchange).doFinally(signalType -> {
            long duration = java.time.Duration.between(start, LocalDateTime.now()).toMillis();

            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            LogEntry log = new LogEntry();
            log.setPath(request.getURI().getPath());
            log.setMethod(request.getMethodValue());
            log.setIp(request.getRemoteAddress() != null ? request.getRemoteAddress().getHostString() : "unknown");
            log.setStatus(response.getStatusCode() != null ? response.getStatusCode().value() : 500);
            log.setDuration(duration);
            log.setCreateTime(LocalDateTime.now());
            Optional.ofNullable(exchange.getRequest().getHeaders().getFirst(SystemConstant.USER_ID_HEADER_NAME))
                    .ifPresent(userId -> {
                        try {
                            log.setUserId(Long.parseLong(userId));
                        } catch (NumberFormatException ignored) {}
                    });

            // 异步发送到消息队列
            if (!response.getStatusCode().is4xxClientError()) {
                rabbitTemplate.convertAndSend(MQConstant.LOG_EXCHANGE, MQConstant.LOG_INSERT_KEY, log);
            }
        });
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
