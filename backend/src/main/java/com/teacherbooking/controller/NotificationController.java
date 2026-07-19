package com.teacherbooking.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teacherbooking.common.result.Result;
import com.teacherbooking.entity.Notification;
import com.teacherbooking.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 通知控制器
 */
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "通知管理", description = "系统通知相关接口")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/list")
    @Operation(summary = "获取通知列表")
    public Result<IPage<Notification>> getNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean isRead) {
        return Result.success(notificationService.getUserNotifications(page, size, isRead));
    }

    @GetMapping("/unread/count")
    @Operation(summary = "获取未读通知数量")
    public Result<Long> getUnreadCount() {
        return Result.success(notificationService.getUnreadCount());
    }

    @PutMapping("/{notificationId}/read")
    @Operation(summary = "标记通知为已读")
    public Result<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return Result.success("已读", null);
    }

    @PutMapping("/read-all")
    @Operation(summary = "全部标记为已读")
    public Result<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return Result.success("全部已读", null);
    }
}
