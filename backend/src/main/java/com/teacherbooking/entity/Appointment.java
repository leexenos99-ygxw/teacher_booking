package com.teacherbooking.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 预约实体类
 */
@Data
@TableName("appointment")
public class Appointment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String appointmentNo;

    private Long teacherId;

    private Long parentId;

    private Long studentId;

    private Long calendarId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String status;

    private String reason;

    private Integer duration;

    private String rejectReason;

    private String cancelReason;

    private String cancelBy;

    private LocalDateTime actualStartTime;

    private LocalDateTime actualEndTime;

    private String summary;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
