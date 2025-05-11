package cn.org.joinup.gateway.filters;

import cn.org.joinup.common.constant.SystemConstant;
import cn.org.joinup.common.domain.JwtPayload;
import cn.org.joinup.common.exception.UnauthorizedException;
import cn.org.joinup.gateway.util.JwtTool;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;


/**
 * @author chenxuanrao06@gmail.com
 */
@Component
public class WebSocketGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {
    private final JwtTool jwtTool;

    public WebSocketGatewayFilterFactory(JwtTool jwtTool) {
        super(Object.class);
        this.jwtTool = jwtTool;
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            String token;
            JwtPayload jwtPayload;
            try {
                token = request.getURI().toString().split("token=")[1];
                jwtPayload = jwtTool.parseToken(token);
            } catch (UnauthorizedException e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            ServerWebExchange newExchange = exchange.mutate()
                    .request(builder -> builder.header(SystemConstant.USER_ID_HEADER_NAME, jwtPayload.getUserId().toString()).header(SystemConstant.USER_ROLE_HEADER_NAME, jwtPayload.getRole()))
                    .build();

            return chain.filter(newExchange);
        };
    }

}
