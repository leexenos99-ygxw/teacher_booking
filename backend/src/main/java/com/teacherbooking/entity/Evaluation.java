package com.teacherbooking.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 评价实体类
 */
@Data
@TableName("evaluation")
public class Evaluation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long appointmentId;

    private Long parentId;

    private Long teacherId;

    private Integer rating;

    private String content;

    private String tags;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}
