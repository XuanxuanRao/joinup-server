package cn.org.joinup.gateway.filters;

import cn.org.joinup.common.domain.JwtPayload;
import cn.org.joinup.gateway.config.AuthProperties;
import cn.org.joinup.gateway.util.JwtTool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.Objects;

import static org.mockito.Mockito.*;

class AuthGlobalFilterTest {

    private AuthGlobalFilter authGlobalFilter;

    @Mock
    private AuthProperties authProperties;

    @Mock
    private JwtTool jwtTool;

    @Mock
    private GatewayFilterChain chain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authGlobalFilter = new AuthGlobalFilter(authProperties, jwtTool);
        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("Should pass when path is excluded")
    void testFilter_ExcludedPath() {
        // Arrange
        when(authProperties.getExcludePaths()).thenReturn(Collections.singletonList("/public/**"));
        MockServerHttpRequest request = MockServerHttpRequest.get("/public/login").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        
        // Mock exception from parseToken to simulate no/invalid token
        when(jwtTool.parseToken(any())).thenThrow(new cn.org.joinup.common.exception.UnauthorizedException("Missing token"));

        // Act
        Mono<Void> result = authGlobalFilter.filter(exchange, chain);

        // Assert
        StepVerifier.create(result).verifyComplete();
        verify(jwtTool).parseToken(any());
        verify(chain).filter(exchange);
    }

    @Test
    @DisplayName("Should fail when token is missing")
    void testFilter_MissingToken() {
        // Arrange
        when(authProperties.getExcludePaths()).thenReturn(Collections.emptyList());
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/private").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // Mock exception for missing token
        when(jwtTool.parseToken(any())).thenThrow(new cn.org.joinup.common.exception.UnauthorizedException("Missing token"));

        // Act
        Mono<Void> result = authGlobalFilter.filter(exchange, chain);

        // Assert
        StepVerifier.create(result).verifyComplete(); // Filter completes by setting response status
        assert Objects.requireNonNull(exchange.getResponse().getStatusCode()).value() == 401;
        verify(chain, never()).filter(exchange);
    }

    @Test
    @DisplayName("Should pass when token is valid")
    void testFilter_ValidToken() {
        // Arrange
        when(authProperties.getExcludePaths()).thenReturn(Collections.emptyList());
        String token = "valid-token";
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/private")
                .header("authorization", token)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        JwtPayload payload = new JwtPayload(1L, "user", "app", "1");
        when(jwtTool.parseToken(token)).thenReturn(payload);

        // Act
        Mono<Void> result = authGlobalFilter.filter(exchange, chain);

        // Assert
        StepVerifier.create(result).verifyComplete();
        verify(jwtTool).parseToken(token);
        verify(chain).filter(any(ServerWebExchange.class));
    }
}
