package com.teacherbooking.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teacherbooking.entity.Notification;

/**
 * 通知服务接口
 */
public interface NotificationService {

    /**
     * 获取用户通知列表
     */
    IPage<Notification> getUserNotifications(int page, int size, Boolean isRead);

    /**
     * 获取未读通知数量
     */
    long getUnreadCount();

    /**
     * 标记为已读
     */
    void markAsRead(Long notificationId);

    /**
     * 全部标记为已读
     */
    void markAllAsRead();
}
