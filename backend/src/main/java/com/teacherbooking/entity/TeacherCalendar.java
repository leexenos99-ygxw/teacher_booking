package com.teacherbooking.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 教师时间表实体类
 */
@Data
@TableName("teacher_calendar")
public class TeacherCalendar {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long teacherId;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    private String status;

    private Integer capacity;

    private Integer bookedCount;

    private Integer duration;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
