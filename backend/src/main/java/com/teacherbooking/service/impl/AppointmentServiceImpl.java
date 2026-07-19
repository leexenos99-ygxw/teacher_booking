package com.teacherbooking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teacherbooking.common.exception.BusinessException;
import com.teacherbooking.common.result.ResultCode;
import com.teacherbooking.common.utils.SecurityUtils;
import com.teacherbooking.entity.*;
import com.teacherbooking.enums.AppointmentStatusEnum;
import com.teacherbooking.mapper.*;
import com.teacherbooking.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import cn.hutool.core.util.IdUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentMapper appointmentMapper;
    private final MessageMapper messageMapper;
    private final TeacherMapper teacherMapper;
    private final ParentMapper parentMapper;
    private final TeacherCalendarMapper teacherCalendarMapper;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Appointment createAppointment(Appointment appointment) {
        Long parentId = getCurrentParentId();
        Teacher teacher = teacherMapper.selectById(appointment.getTeacherId());
        if (teacher == null) {
            throw new BusinessException(ResultCode.TEACHER_NOT_EXIST);
        }
        if (!"APPROVED".equals(teacher.getAuditStatus())) {
            throw new BusinessException(ResultCode.TEACHER_NOT_APPROVED);
        }

        if (appointment.getCalendarId() != null) {
            TeacherCalendar calendar = teacherCalendarMapper.selectById(appointment.getCalendarId());
            if (calendar == null) {
                throw new BusinessException(ResultCode.CALENDAR_NOT_AVAILABLE);
            }
            if (!"AVAILABLE".equals(calendar.getStatus())) {
                throw new BusinessException(ResultCode.CALENDAR_NOT_AVAILABLE);
            }
            if (calendar.getBookedCount() >= calendar.getCapacity()) {
                throw new BusinessException(ResultCode.CALENDAR_FULL);
            }
            calendar.setBookedCount(calendar.getBookedCount() + 1);
            if (calendar.getBookedCount() >= calendar.getCapacity()) {
                calendar.setStatus("FULL");
            }
            teacherCalendarMapper.updateById(calendar);
        }

        appointment.setAppointmentNo("AP" + IdUtil.getSnowflakeNextIdStr());
        appointment.setParentId(parentId);
        appointment.setStatus(AppointmentStatusEnum.WAITING.getCode());

        if (appointment.getStartTime() != null && appointment.getEndTime() != null) {
            long minutes = Duration.between(appointment.getStartTime(), appointment.getEndTime()).toMinutes();
            appointment.setDuration((int) minutes);
        }

        appointmentMapper.insert(appointment);

        createNotification(
                appointment.getTeacherId(),
                "新的预约申请",
                "您收到一条新的预约申请，请及时处理",
                "APPOINTMENT_NEW",
                appointment.getId()
        );

        log.info("预约创建成功: appointmentId={}, appointmentNo={}",
                appointment.getId(), appointment.getAppointmentNo());

        return appointment;
    }

    @Override
    public Appointment getAppointmentDetail(Long appointmentId) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }

        checkAppointmentPermission(appointment);

        return appointment;
    }

    @Override
    public IPage<Appointment> getParentAppointments(int page, int size, String status) {
        Long parentId = getCurrentParentId();

        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Appointment::getParentId, parentId);

        if (StringUtils.hasText(status)) {
            wrapper.eq(Appointment::getStatus, status);
        }

        wrapper.orderByDesc(Appointment::getCreatedTime);

        return appointmentMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public IPage<Appointment> getTeacherAppointments(int page, int size, String status) {
        Long teacherId = getCurrentTeacherId();

        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Appointment::getTeacherId, teacherId);

        if (StringUtils.hasText(status)) {
            wrapper.eq(Appointment::getStatus, status);
        }

        wrapper.orderByDesc(Appointment::getCreatedTime);

        return appointmentMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void acceptAppointment(Long appointmentId) {
        Appointment appointment = getAppointmentAndCheckTeacher(appointmentId);

        if (!AppointmentStatusEnum.WAITING.getCode().equals(appointment.getStatus())) {
            throw new BusinessException(ResultCode.APPOINTMENT_STATUS_ERROR);
        }

        appointment.setStatus(AppointmentStatusEnum.ACCEPT.getCode());
        appointmentMapper.updateById(appointment);

        createNotification(
                appointment.getParentId(),
                "预约已接受",
                "您的预约申请已被教师接受，请准时参加沟通",
                "APPOINTMENT_ACCEPT",
                appointmentId
        );

        log.info("预约接受成功: appointmentId={}", appointmentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectAppointment(Long appointmentId, String rejectReason) {
        Appointment appointment = getAppointmentAndCheckTeacher(appointmentId);

        if (!AppointmentStatusEnum.WAITING.getCode().equals(appointment.getStatus())) {
            throw new BusinessException(ResultCode.APPOINTMENT_STATUS_ERROR);
        }

        appointment.setStatus(AppointmentStatusEnum.REJECT.getCode());
        appointment.setRejectReason(rejectReason);
        appointmentMapper.updateById(appointment);

        if (appointment.getCalendarId() != null) {
            TeacherCalendar calendar = teacherCalendarMapper.selectById(appointment.getCalendarId());
            if (calendar != null && calendar.getBookedCount() > 0) {
                calendar.setBookedCount(calendar.getBookedCount() - 1);
                if ("FULL".equals(calendar.getStatus())) {
                    calendar.setStatus("AVAILABLE");
                }
                teacherCalendarMapper.updateById(calendar);
            }
        }

        createNotification(
                appointment.getParentId(),
                "预约被拒绝",
                "您的预约申请被拒绝，拒绝原因：" + rejectReason,
                "APPOINTMENT_REJECT",
                appointmentId
        );

        log.info("预约拒绝成功: appointmentId={}", appointmentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelAppointment(Long appointmentId, String cancelReason) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }

        String currentRole = SecurityUtils.getCurrentRole();
        String cancelBy = null;

        if ("PARENT".equals(currentRole)) {
            Long parentId = getCurrentParentId();
            if (!parentId.equals(appointment.getParentId())) {
                throw new BusinessException(ResultCode.FORBIDDEN);
            }
            cancelBy = "PARENT";
        } else if ("TEACHER".equals(currentRole)) {
            Long teacherId = getCurrentTeacherId();
            if (!teacherId.equals(appointment.getTeacherId())) {
                throw new BusinessException(ResultCode.FORBIDDEN);
            }
            cancelBy = "TEACHER";
        } else if ("ADMIN".equals(currentRole)) {
            cancelBy = "ADMIN";
        }

        if (!AppointmentStatusEnum.WAITING.getCode().equals(appointment.getStatus())
                && !AppointmentStatusEnum.ACCEPT.getCode().equals(appointment.getStatus())) {
            throw new BusinessException(ResultCode.APPOINTMENT_CANNOT_CANCEL);
        }

        appointment.setStatus(AppointmentStatusEnum.CANCEL.getCode());
        appointment.setCancelReason(cancelReason);
        appointment.setCancelBy(cancelBy);
        appointmentMapper.updateById(appointment);

        if (appointment.getCalendarId() != null) {
            TeacherCalendar calendar = teacherCalendarMapper.selectById(appointment.getCalendarId());
            if (calendar != null && calendar.getBookedCount() > 0) {
                calendar.setBookedCount(calendar.getBookedCount() - 1);
                if ("FULL".equals(calendar.getStatus())) {
                    calendar.setStatus("AVAILABLE");
                }
                teacherCalendarMapper.updateById(calendar);
            }
        }

        Long notifyUserId = "PARENT".equals(cancelBy) ? appointment.getTeacherId() : appointment.getParentId();
        createNotification(
                notifyUserId,
                "预约已取消",
                "预约已被取消，取消原因：" + cancelReason,
                "APPOINTMENT_CANCEL",
                appointmentId
        );

        log.info("预约取消成功: appointmentId={}, cancelBy={}", appointmentId, cancelBy);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishAppointment(Long appointmentId) {
        Appointment appointment = getAppointmentAndCheckPermission(appointmentId);

        if (!AppointmentStatusEnum.ACCEPT.getCode().equals(appointment.getStatus())) {
            throw new BusinessException(ResultCode.APPOINTMENT_STATUS_ERROR);
        }

        appointment.setStatus(AppointmentStatusEnum.FINISH.getCode());
        appointment.setActualEndTime(LocalDateTime.now());
        appointmentMapper.updateById(appointment);

        log.info("预约完成: appointmentId={}", appointmentId);
    }

    @Override
    public List<Message> getAppointmentMessages(Long appointmentId) {
        Appointment appointment = getAppointmentAndCheckPermission(appointmentId);

        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getAppointmentId, appointmentId);
        wrapper.orderByAsc(Message::getCreatedTime);

        return messageMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message sendMessage(Long appointmentId, String content, String type) {
        Appointment appointment = getAppointmentAndCheckPermission(appointmentId);

        if (!AppointmentStatusEnum.ACCEPT.getCode().equals(appointment.getStatus())) {
            throw new BusinessException("当前预约状态不能发送消息");
        }

        Long senderId = SecurityUtils.getCurrentUserId();
        Long receiverId = senderId.equals(appointment.getParentId())
                ? appointment.getTeacherId()
                : appointment.getParentId();

        Message message = new Message();
        message.setAppointmentId(appointmentId);
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setType(type != null ? type : "TEXT");
        message.setIsRead(0);

        messageMapper.insert(message);

        return message;
    }

    private Long getCurrentParentId() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        Parent parent = parentMapper.selectOne(
                new LambdaQueryWrapper<Parent>().eq(Parent::getUserId, userId)
        );
        if (parent == null) {
            throw new BusinessException("家长信息不存在");
        }
        return parent.getId();
    }

    private Long getCurrentTeacherId() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        Teacher teacher = teacherMapper.selectOne(
                new LambdaQueryWrapper<Teacher>().eq(Teacher::getUserId, userId)
        );
        if (teacher == null) {
            throw new BusinessException(ResultCode.TEACHER_NOT_EXIST);
        }
        return teacher.getId();
    }

    private void checkAppointmentPermission(Appointment appointment) {
        String role = SecurityUtils.getCurrentRole();
        Long userId = SecurityUtils.getCurrentUserId();

        if ("ADMIN".equals(role)) {
            return;
        }

        if ("PARENT".equals(role)) {
            Parent parent = parentMapper.selectOne(
                    new LambdaQueryWrapper<Parent>().eq(Parent::getUserId, userId)
            );
            if (parent == null || !parent.getId().equals(appointment.getParentId())) {
                throw new BusinessException(ResultCode.FORBIDDEN);
            }
        } else if ("TEACHER".equals(role)) {
            Teacher teacher = teacherMapper.selectOne(
                    new LambdaQueryWrapper<Teacher>().eq(Teacher::getUserId, userId)
            );
            if (teacher == null || !teacher.getId().equals(appointment.getTeacherId())) {
                throw new BusinessException(ResultCode.FORBIDDEN);
            }
        }
    }

    private Appointment getAppointmentAndCheckTeacher(Long appointmentId) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }

        Long teacherId = getCurrentTeacherId();
        if (!teacherId.equals(appointment.getTeacherId())) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        return appointment;
    }

    private Appointment getAppointmentAndCheckPermission(Long appointmentId) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }
        checkAppointmentPermission(appointment);
        return appointment;
    }

    private void createNotification(Long userId, String title, String content, String type, Long bizId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setBizId(bizId);
        notification.setIsRead(0);
        notificationMapper.insert(notification);
    }
}
