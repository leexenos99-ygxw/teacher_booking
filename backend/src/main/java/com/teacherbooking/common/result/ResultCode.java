package com.teacherbooking.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回状态码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),

    // 客户端错误 4xx
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请先登录"),
    FORBIDDEN(403, "权限不足，禁止访问"),
    NOT_FOUND(404, "请求资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),

    // 业务错误 1000-1999
    USER_NOT_EXIST(1001, "用户不存在"),
    USERNAME_EXIST(1002, "用户名已存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    USER_DISABLED(1004, "账号已被禁用"),
    TOKEN_INVALID(1005, "Token无效或已过期"),
    TOKEN_EXPIRED(1006, "Token已过期"),
    PHONE_EXIST(1007, "手机号已注册"),

    // 预约相关 2000-2999
    APPOINTMENT_NOT_EXIST(2001, "预约不存在"),
    APPOINTMENT_STATUS_ERROR(2002, "预约状态错误"),
    APPOINTMENT_TIME_CONFLICT(2003, "预约时间冲突"),
    APPOINTMENT_CANNOT_CANCEL(2004, "当前状态不可取消预约"),
    CALENDAR_NOT_AVAILABLE(2005, "该时间段不可预约"),
    CALENDAR_FULL(2006, "该时间段已满"),

    // 教师相关 3000-3999
    TEACHER_NOT_EXIST(3001, "教师不存在"),
    TEACHER_NOT_APPROVED(3002, "教师未通过审核"),
    TEACHER_AUDIT_PENDING(3003, "教师资质审核中"),

    // 评价相关 4000-4999
    EVALUATION_EXIST(4001, "已评价，不能重复评价"),
    EVALUATION_NOT_ALLOWED(4002, "当前状态不能评价");

    private final Integer code;
    private final String message;
}
