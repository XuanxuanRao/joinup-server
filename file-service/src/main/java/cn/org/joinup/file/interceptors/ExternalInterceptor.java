package cn.org.joinup.file.interceptors;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author chenxuanrao06@gmail.com
 */
@Component
public class ExternalInterceptor implements HandlerInterceptor {

    @Value("${service.external.api-key}")
    private String externalApiKey;

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        String apiKey = request.getHeader("X-API-KEY");
        if (Objects.equals(apiKey, externalApiKey)) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Unauthorized: Invalid API Key");
        return false;
    }
}
