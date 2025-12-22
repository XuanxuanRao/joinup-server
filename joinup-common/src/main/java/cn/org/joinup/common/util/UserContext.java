package cn.org.joinup.common.util;

public class UserContext {
    private static final ThreadLocal<Long> userId = new ThreadLocal<>();
    private static final ThreadLocal<String> appKey = new ThreadLocal<>();
    private static final ThreadLocal<String> role = new ThreadLocal<>();

    /**
     * 保存当前登录用户信息到ThreadLocal
     * @param userId 用户id
     * @param appKey 应用key
     * @param role 用户角色
     */
    public static void setUser(Long userId, String appKey, String role) {
        UserContext.userId.set(userId);
        UserContext.appKey.set(appKey);
        UserContext.role.set(role);
    }

    /**
     * 获取当前登录用户信息
     * @return 用户id
     */
    public static Long getUserId() {
        return userId.get();
    }

    /**
     * 获取当前登录用户所属的应用Key
     * @return 应用Key
     */
    public static String getAppKey() {
        return appKey.get();
    }

    /**
     * 获取当前登录用户角色
     * @return 用户角色
     */
    public static String getUserRole() {
        return role.get();
    }

    /**
     * 移除当前登录用户信息
     */
    public static void removeUser(){
        userId.remove();
        appKey.remove();
        role.remove();
    }
}
