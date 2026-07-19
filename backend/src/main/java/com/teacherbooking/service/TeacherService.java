package com.teacherbooking.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teacherbooking.entity.Teacher;
import com.teacherbooking.entity.TeacherCalendar;

import java.time.LocalDate;
import java.util.List;

/**
 * 教师服务接口
 */
public interface TeacherService {

    /**
     * 分页查询教师列表
     */
    IPage<Teacher> getTeacherList(int page, int size, String subject, String keyword);

    /**
     * 获取教师详情
     */
    Teacher getTeacherDetail(Long teacherId);

    /**
     * 获取教师时间表
     */
    List<TeacherCalendar> getTeacherCalendar(Long teacherId, LocalDate startDate, LocalDate endDate);

    /**
     * 设置教师可预约时间
     */
    void setTeacherCalendar(Long teacherId, List<TeacherCalendar> calendars);

    /**
     * 更新教师个人资料
     */
    void updateTeacherProfile(Long teacherId, Teacher teacher);

    /**
     * 获取当前登录教师信息
     */
    Teacher getCurrentTeacher();

    /**
     * 审核教师
     */
    void auditTeacher(Long teacherId, String auditStatus, String auditRemark);
}
