package com.teacherbooking.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 预约状态枚举
 */
@Getter
@AllArgsConstructor
public enum AppointmentStatusEnum {

    WAITING("WAITING", "待确认"),
    ACCEPT("ACCEPT", "已接受"),
    REJECT("REJECT", "已拒绝"),
    FINISH("FINISH", "已完成"),
    CANCEL("CANCEL", "已取消");

    private final String code;
    private final String desc;

    public static AppointmentStatusEnum getByCode(String code) {
        for (AppointmentStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
