package com.teacherbooking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teacherbooking.common.exception.BusinessException;
import com.teacherbooking.common.result.ResultCode;
import com.teacherbooking.common.utils.SecurityUtils;
import com.teacherbooking.entity.Notification;
import com.teacherbooking.mapper.NotificationMapper;
import com.teacherbooking.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 通知服务实现类
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    @Override
    public IPage<Notification> getUserNotifications(int page, int size, Boolean isRead) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId);

        if (isRead != null) {
            wrapper.eq(Notification::getIsRead, isRead ? 1 : 0);
        }

        wrapper.orderByDesc(Notification::getCreatedTime);

        return notificationMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public long getUnreadCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return 0;
        }

        return notificationMapper.selectCount(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .eq(Notification::getIsRead, 0)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long notificationId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null || !notification.getUserId().equals(userId)) {
            return;
        }

        notification.setIsRead(1);
        notification.setReadTime(LocalDateTime.now());
        notificationMapper.updateById(notification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        Notification update = new Notification();
        update.setIsRead(1);
        update.setReadTime(LocalDateTime.now());

        notificationMapper.update(update,
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .eq(Notification::getIsRead, 0)
        );
    }
}
