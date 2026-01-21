package cn.org.joinup.message.interceptor;

import cn.org.joinup.common.result.Result;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.message.service.IFeatureService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Component
@Slf4j
@RequiredArgsConstructor
public class FeatureAccessInterceptor implements HandlerInterceptor {

    private final IFeatureService featureService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        
        // Expected pattern: /message/feature/{featureName}/...
        // We can split by "/"
        String[] parts = requestURI.split("/");
        
        // Find "feature" and take the next part
        String featureName = null;
        for (int i = 0; i < parts.length - 1; i++) {
            if ("feature".equals(parts[i])) {
                featureName = parts[i + 1];
                break;
            }
        }

        if (featureName == null) {
            // Should not happen if configured correctly with addPathPatterns("/message/feature/**")
            return true;
        }

        Long userId = UserContext.getUserId();
        if (userId == null) {
            // User not logged in (or context not set). 
            // If the endpoint requires auth, UserInfoInterceptor or Security should have handled it.
            // If it's public but feature-gated, we might deny anonymous users for experimental features.
            return denyAccess(response, "Login required for experimental features");
        }

        if (!featureService.canAccess(featureName, userId)) {
            return denyAccess(response, "Access denied to experimental feature: " + featureName);
        }

        return true;
    }

    private boolean denyAccess(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.write(objectMapper.writeValueAsString(Result.error(message)));
        }
        return false;
    }
}
