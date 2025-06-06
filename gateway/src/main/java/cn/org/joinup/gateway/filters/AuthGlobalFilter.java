package cn.org.joinup.gateway.filters;

import cn.org.joinup.common.constant.RoleConstant;
import cn.org.joinup.common.domain.JwtPayload;
import lombok.RequiredArgsConstructor;
import cn.org.joinup.common.constant.SystemConstant;
import cn.org.joinup.common.exception.UnauthorizedException;
import cn.org.joinup.gateway.config.AuthProperties;
import cn.org.joinup.gateway.util.JwtTool;
import lombok.extern.slf4j.Slf4j;
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
import java.util.regex.Pattern;

/**
 * @author chenxuanrao06@gmail.com
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final AuthProperties authProperties;

    private final JwtTool jwtTool;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String token = null;
        List<String> headers = request.getHeaders().get(SystemConstant.TOKEN_NAME);
        if (headers != null && !headers.isEmpty()) {
            token = headers.get(0);
        }

        // 4. 解析token
        JwtPayload jwtPayload;
        try {
            jwtPayload = jwtTool.parseToken(token);
            if (isNeedAdminAuth(request.getPath().toString()) && !RoleConstant.ADMIN.equals(jwtPayload.getRole())) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
        } catch (UnauthorizedException e) {
            if (isNeedAuth(request.getPath().toString())) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            } else {
                return chain.filter(exchange);
            }
        }

        // 6. 传递用户信息
        ServerWebExchange newExchange = exchange.mutate()
                .request(builder -> builder.headers(httpHeaders -> {
                    httpHeaders.set(SystemConstant.USER_ID_HEADER_NAME, jwtPayload.getUserId().toString());
                    httpHeaders.set(SystemConstant.USER_ROLE_HEADER_NAME, jwtPayload.getRole());
                }))
                .build();


        log.info("User ID: {}, Role: {}", jwtPayload.getUserId(), jwtPayload.getRole());

        return chain.filter(newExchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private boolean isNeedAuth(String path) {
        return authProperties.getExcludePaths().stream().noneMatch(pattern -> antPathMatcher.match(pattern, path));
    }

    public boolean isNeedAdminAuth(String path) {
        if (path == null) return false;

        return authProperties.getAdminAntPaths().stream()
                .anyMatch(pattern -> antPathMatcher.match(pattern, path)) ||
                authProperties.getAdminRegexPaths().stream()
                 .map(Pattern::compile)
                 .anyMatch(pattern -> pattern.matcher(path).matches());
    }
}