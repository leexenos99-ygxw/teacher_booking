package com.teacherbooking.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 家长实体类
 */
@Data
@TableName("parent")
public class Parent {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String realName;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
