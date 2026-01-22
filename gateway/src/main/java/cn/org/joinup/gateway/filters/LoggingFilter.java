package cn.org.joinup.gateway.filters;

import cn.hutool.extra.servlet.ServletUtil;
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
import java.util.List;
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
            log.setIp(getClientIp(request));
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

    /**
     * 获取客户端真实IP
     * 优先从代理头获取，其次获取直接连接IP
     */
    private String getClientIp(ServerHttpRequest request) {
        ServletUtil.getClientIP(request);
        // 1. 尝试从 X-Forwarded-For 头获取（最常用的代理头）
        List<String> xForwardedFor = request.getHeaders().get("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // X-Forwarded-For 格式：用户IP, 代理1IP, 代理2IP
            return xForwardedFor.get(0).split(",")[0].trim();
        }

        // 2. 尝试从 X-Real-IP 头获取
        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp.trim();
        }

        // 3. 尝试从 Proxy-Client-IP 头获取（某些代理使用）
        String proxyClientIp = request.getHeaders().getFirst("Proxy-Client-IP");
        if (proxyClientIp != null && !proxyClientIp.isEmpty()) {
            return proxyClientIp.trim();
        }

        // 4. 最后获取直接连接的IP（作为 fallback）
        return request.getRemoteAddress() != null ? request.getRemoteAddress().getHostString() : "unknown";
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
