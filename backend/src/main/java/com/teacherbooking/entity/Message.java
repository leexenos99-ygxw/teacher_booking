package com.teacherbooking.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 消息实体类
 */
@Data
@TableName("message")
public class Message {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long appointmentId;

    private Long senderId;

    private Long receiverId;

    private String content;

    private String type;

    private Integer isRead;

    private LocalDateTime readTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}
