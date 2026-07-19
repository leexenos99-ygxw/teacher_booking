package com.teacherbooking.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 教师实体类
 */
@Data
@TableName("teacher")
public class Teacher {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String realName;

    private String subject;

    private String school;

    private String title;

    private String description;

    private Integer teachingYears;

    private BigDecimal rating;

    private Integer reviewCount;

    private Integer appointmentCount;

    private String auditStatus;

    private String auditRemark;

    private LocalDateTime auditTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
