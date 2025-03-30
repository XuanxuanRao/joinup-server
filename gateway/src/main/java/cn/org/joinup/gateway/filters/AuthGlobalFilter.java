package cn.org.joinup.gateway.filters;

import lombok.RequiredArgsConstructor;
import cn.org.joinup.common.constant.SystemConstant;
import cn.org.joinup.common.exception.UnauthorizedException;
import cn.org.joinup.gateway.config.AuthProperties;
import cn.org.joinup.gateway.util.JwtTool;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author chenxuanrao06@gmail.com
 * @Description:
 */
@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final AuthProperties authProperties;

    private final JwtTool jwtTool;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (!isNeedAuth(request.getPath().toString())) {
            return chain.filter(exchange);
        }

        String token = null;
        List<String> headers = request.getHeaders().get(SystemConstant.TOKEN_NAME);
        if (headers != null && !headers.isEmpty()) {
            token = headers.get(0);
        }

        Long userId;
        try {
            userId = jwtTool.parseToken(token);
        } catch (UnauthorizedException e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 通过请求头传递用户信息
        ServerWebExchange newExchange = exchange.mutate()
                .request(builder -> builder.header(SystemConstant.USER_ID_NAME, userId.toString()))
                .build();

        return chain.filter(newExchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private boolean isNeedAuth(String path) {
        return authProperties.getExcludePaths().stream().noneMatch(pattern -> antPathMatcher.match(pattern, path));
    }

}