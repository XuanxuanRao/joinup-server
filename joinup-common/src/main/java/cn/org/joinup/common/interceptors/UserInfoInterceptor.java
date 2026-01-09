package cn.org.joinup.common.interceptors;

import cn.hutool.core.util.StrUtil;
import cn.org.joinup.common.constant.SystemConstant;
import cn.org.joinup.common.util.UserContext;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author chenxuanrao06@gmail.com
 */
public class UserInfoInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader(SystemConstant.USER_ID_HEADER_NAME);
        String appKey = request.getHeader(SystemConstant.APP_KEY_HEADER_NAME);
        String role = request.getHeader(SystemConstant.USER_ROLE_HEADER_NAME);
        String userType = request.getHeader(SystemConstant.USER_TYPE_HEADER_NAME);
        if (StrUtil.isNotBlank(userId)) {
            UserContext.setUser(Long.parseLong(userId), appKey, role, userType);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.removeUser();
    }
}
