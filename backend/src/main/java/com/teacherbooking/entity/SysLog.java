package com.teacherbooking.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 操作日志实体类
 */
@Data
@TableName("sys_log")
public class SysLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String username;

    private String module;

    private String operation;

    private String method;

    private String params;

    private String result;

    private String ip;

    private String location;

    private Integer status;

    private String errorMsg;

    private Long time;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}
