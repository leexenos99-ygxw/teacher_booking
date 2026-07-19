package com.teacherbooking.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户角色枚举
 */
@Getter
@AllArgsConstructor
public enum RoleEnum {

    ADMIN("ADMIN", "管理员"),
    TEACHER("TEACHER", "教师"),
    PARENT("PARENT", "家长");

    private final String code;
    private final String desc;

    public static RoleEnum getByCode(String code) {
        for (RoleEnum role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return null;
    }
}
