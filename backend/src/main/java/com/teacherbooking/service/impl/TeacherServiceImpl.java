package com.teacherbooking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teacherbooking.common.exception.BusinessException;
import com.teacherbooking.common.result.ResultCode;
import com.teacherbooking.common.utils.SecurityUtils;
import com.teacherbooking.entity.Parent;
import com.teacherbooking.entity.Teacher;
import com.teacherbooking.entity.TeacherCalendar;
import com.teacherbooking.entity.User;
import com.teacherbooking.mapper.ParentMapper;
import com.teacherbooking.mapper.TeacherCalendarMapper;
import com.teacherbooking.mapper.TeacherMapper;
import com.teacherbooking.mapper.UserMapper;
import com.teacherbooking.service.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 教师服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final TeacherMapper teacherMapper;
    private final TeacherCalendarMapper teacherCalendarMapper;
    private final UserMapper userMapper;
    private final ParentMapper parentMapper;

    @Override
    public IPage<Teacher> getTeacherList(int page, int size, String subject, String keyword) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teacher::getAuditStatus, "APPROVED");

        if (StringUtils.hasText(subject)) {
            wrapper.eq(Teacher::getSubject, subject);
        }

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(Teacher::getRealName, keyword)
                    .or().like(Teacher::getSchool, keyword)
                    .or().like(Teacher::getSubject, keyword)
            );
        }

        wrapper.orderByDesc(Teacher::getRating);

        return teacherMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public Teacher getTeacherDetail(Long teacherId) {
        Teacher teacher = teacherMapper.selectById(teacherId);
        if (teacher == null) {
            throw new BusinessException(ResultCode.TEACHER_NOT_EXIST);
        }
        if (!"APPROVED".equals(teacher.getAuditStatus())) {
            throw new BusinessException(ResultCode.TEACHER_NOT_APPROVED);
        }
        return teacher;
    }

    @Override
    public List<TeacherCalendar> getTeacherCalendar(Long teacherId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<TeacherCalendar> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherCalendar::getTeacherId, teacherId);

        if (startDate != null) {
            wrapper.ge(TeacherCalendar::getDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(TeacherCalendar::getDate, endDate);
        }

        wrapper.orderByAsc(TeacherCalendar::getDate, TeacherCalendar::getStartTime);

        return teacherCalendarMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setTeacherCalendar(Long teacherId, List<TeacherCalendar> calendars) {
        Teacher teacher = teacherMapper.selectById(teacherId);
        if (teacher == null) {
            throw new BusinessException(ResultCode.TEACHER_NOT_EXIST);
        }

        for (TeacherCalendar calendar : calendars) {
            calendar.setTeacherId(teacherId);
            if (calendar.getId() == null) {
                teacherCalendarMapper.insert(calendar);
            } else {
                teacherCalendarMapper.updateById(calendar);
            }
        }

        log.info("教师时间表设置成功: teacherId={}, 数量={}", teacherId, calendars.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTeacherProfile(Long teacherId, Teacher teacher) {
        Teacher existing = teacherMapper.selectById(teacherId);
        if (existing == null) {
            throw new BusinessException(ResultCode.TEACHER_NOT_EXIST);
        }

        teacher.setId(teacherId);
        teacher.setUserId(null);
        teacher.setRating(null);
        teacher.setReviewCount(null);
        teacher.setAppointmentCount(null);
        teacher.setAuditStatus(null);

        teacherMapper.updateById(teacher);
        log.info("教师资料更新成功: teacherId={}", teacherId);
    }

    @Override
    public Teacher getCurrentTeacher() {
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

        return teacher;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditTeacher(Long teacherId, String auditStatus, String auditRemark) {
        Teacher teacher = teacherMapper.selectById(teacherId);
        if (teacher == null) {
            throw new BusinessException(ResultCode.TEACHER_NOT_EXIST);
        }

        teacher.setAuditStatus(auditStatus);
        teacher.setAuditRemark(auditRemark);
        teacher.setAuditTime(LocalDateTime.now());
        teacherMapper.updateById(teacher);

        log.info("教师审核完成: teacherId={}, auditStatus={}", teacherId, auditStatus);
    }
}
