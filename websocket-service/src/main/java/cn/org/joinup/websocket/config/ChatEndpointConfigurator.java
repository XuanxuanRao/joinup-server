package cn.org.joinup.websocket.config;

import cn.org.joinup.common.constant.SystemConstant;
import cn.org.joinup.websocket.constant.EndpointConfigConstant;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.List;
import java.util.Map;

/**
 * @author chenxuanrao06@gmail.com
 */
@Slf4j
public class ChatEndpointConfigurator extends ServerEndpointConfig.Configurator {

    /**
     * 在握手之前被调用，可以访问握手请求的 Header。
     */
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        log.info("WebSocket Handshake: modifyHandshake called. URI: {}", request.getRequestURI());
        log.info("WebSocket Handshake: Received Headers: {}", request.getHeaders());

        // 1. 从 HTTP 握手请求头中提取 Gateway 添加的用户信息
        Map<String, List<String>> headers = request.getHeaders();
        List<String> userIdHeaders = headers.get(SystemConstant.USER_ID_HEADER_NAME);

        String userIdStr = (userIdHeaders != null && !userIdHeaders.isEmpty()) ? userIdHeaders.get(0) : null;

        log.info("Extracted headers in configurator: {}: {}", SystemConstant.USER_ID_HEADER_NAME, userIdStr);

        // 2. 校验用户信息 Header 是否存在且有效
        if (userIdStr == null || userIdStr.isEmpty()) {
            log.warn("WebSocket Handshake: Missing or empty user info headers from Gateway in configurator. Authentication failed.");
            // 在 Configurator 中主要通过不设置用户属性并在 @OnOpen 中判断来拒绝连接
            // 可以在响应Header里加个标志，但不影响最终握手失败由容器处理。
            response.getHeaders().put("X-Auth-Error", List.of("Authentication Required"));
        } else {
            // 3. 将用户信息存储到 ServerEndpointConfig 的用户属性中
            // 这些属性会在 Session 对象中传递到 @OnOpen 方法
            try {
                Long userId = Long.parseLong(userIdStr);
                sec.getUserProperties().put(EndpointConfigConstant.USER_ID_PROP_NAME, userId);
                log.info("User info stored in handshake properties: userId={}", userId);
            } catch (NumberFormatException e) {
                log.error("WebSocket Handshake: Invalid userId format in configurator: {}", userIdStr, e);
                sec.getUserProperties().put("authError", "Invalid userId format"); // 存储错误标志
                response.getHeaders().put("X-Auth-Error", List.of("Invalid userId format"));
            } catch (Exception e) {
                log.error("WebSocket Handshake: An unexpected error occurred in configurator.", e);
                sec.getUserProperties().put("authError", "Internal error"); // 存储错误标志
                response.getHeaders().put("X-Auth-Error", List.of("Internal server error"));
            }
        }
    }

    /**
     * 返回 WebSocketEndpoint 实例。
     * 使用 SpringContextHolder 从 Spring 容器获取实例，以实现依赖注入。
     */
//    @Override
//    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
//        // 从 Spring 容器中获取 @Component 标注的 SimpleWebSocketServer 实例
//        // 这样 SimpleWebSocketServer 中的 @Autowired 才能生效
//        try {
//            return SpringContextHolder.getBean(endpointClass);
//        } catch (Exception e) {
//            log.error("Failed to get WebSocket endpoint instance from Spring context.", e);
//            // 如果无法获取 Bean，容器无法创建 Endpoint 实例，握手会失败
//            throw new InstantiationException("Could not get endpoint instance from Spring context: " + e.getMessage());
//        }
//    }
}
