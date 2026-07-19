package com.teacherbooking.common.utils;

import com.teacherbooking.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类
 */
public class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * 获取当前用户认证信息
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取当前用户详情
     */
    public static CustomUserDetails getCurrentUser() {
        Authentication authentication = getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        CustomUserDetails user = getCurrentUser();
        return user != null ? user.getUserId() : null;
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        CustomUserDetails user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }

    /**
     * 获取当前用户角色
     */
    public static String getCurrentRole() {
        CustomUserDetails user = getCurrentUser();
        return user != null ? user.getRole() : null;
    }

    /**
     * 判断是否为管理员
     */
    public static boolean isAdmin() {
        return "ADMIN".equals(getCurrentRole());
    }

    /**
     * 判断是否为教师
     */
    public static boolean isTeacher() {
        return "TEACHER".equals(getCurrentRole());
    }

    /**
     * 判断是否为家长
     */
    public static boolean isParent() {
        return "PARENT".equals(getCurrentRole());
    }
}
