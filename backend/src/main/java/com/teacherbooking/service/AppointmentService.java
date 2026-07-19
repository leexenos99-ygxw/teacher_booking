package com.teacherbooking.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teacherbooking.entity.Appointment;
import com.teacherbooking.entity.Message;

import java.util.List;

/**
 * 预约服务接口
 */
public interface AppointmentService {

    /**
     * 创建预约
     */
    Appointment createAppointment(Appointment appointment);

    /**
     * 获取预约详情
     */
    Appointment getAppointmentDetail(Long appointmentId);

    /**
     * 家长获取自己的预约列表
     */
    IPage<Appointment> getParentAppointments(int page, int size, String status);

    /**
     * 教师获取自己的预约列表
     */
    IPage<Appointment> getTeacherAppointments(int page, int size, String status);

    /**
     * 接受预约
     */
    void acceptAppointment(Long appointmentId);

    /**
     * 拒绝预约
     */
    void rejectAppointment(Long appointmentId, String rejectReason);

    /**
     * 取消预约
     */
    void cancelAppointment(Long appointmentId, String cancelReason);

    /**
     * 完成预约
     */
    void finishAppointment(Long appointmentId);

    /**
     * 获取预约消息列表
     */
    List<Message> getAppointmentMessages(Long appointmentId);

    /**
     * 发送消息
     */
    Message sendMessage(Long appointmentId, String content, String type);
}
