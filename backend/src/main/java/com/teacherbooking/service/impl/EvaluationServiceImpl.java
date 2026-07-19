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
import com.teacherbooking.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 评价服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationServiceImpl implements EvaluationService {

    private final EvaluationMapper evaluationMapper;
    private final AppointmentMapper appointmentMapper;
    private final TeacherMapper teacherMapper;
    private final ParentMapper parentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Evaluation createEvaluation(Evaluation evaluation) {
        Appointment appointment = appointmentMapper.selectById(evaluation.getAppointmentId());
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }

        if (!AppointmentStatusEnum.FINISH.getCode().equals(appointment.getStatus())) {
            throw new BusinessException(ResultCode.EVALUATION_NOT_ALLOWED);
        }

        Long parentId = getCurrentParentId();
        if (!parentId.equals(appointment.getParentId())) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        Long count = evaluationMapper.selectCount(
                new LambdaQueryWrapper<Evaluation>()
                        .eq(Evaluation::getAppointmentId, evaluation.getAppointmentId())
        );
        if (count > 0) {
            throw new BusinessException(ResultCode.EVALUATION_EXIST);
        }

        evaluation.setParentId(parentId);
        evaluation.setTeacherId(appointment.getTeacherId());
        evaluationMapper.insert(evaluation);

        updateTeacherRating(appointment.getTeacherId());

        log.info("评价创建成功: evaluationId={}, appointmentId={}",
                evaluation.getId(), evaluation.getAppointmentId());

        return evaluation;
    }

    @Override
    public Evaluation getEvaluationByAppointment(Long appointmentId) {
        return evaluationMapper.selectOne(
                new LambdaQueryWrapper<Evaluation>()
                        .eq(Evaluation::getAppointmentId, appointmentId)
        );
    }

    @Override
    public IPage<Evaluation> getTeacherEvaluations(Long teacherId, int page, int size) {
        LambdaQueryWrapper<Evaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Evaluation::getTeacherId, teacherId);
        wrapper.orderByDesc(Evaluation::getCreatedTime);

        return evaluationMapper.selectPage(new Page<>(page, size), wrapper);
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

    private void updateTeacherRating(Long teacherId) {
        Teacher teacher = teacherMapper.selectById(teacherId);
        if (teacher == null) {
            return;
        }

        List<Evaluation> evaluations = evaluationMapper.selectList(
                new LambdaQueryWrapper<Evaluation>().eq(Evaluation::getTeacherId, teacherId)
        );

        if (evaluations.isEmpty()) {
            teacher.setRating(BigDecimal.ZERO);
            teacher.setReviewCount(0);
        } else {
            double avgRating = evaluations.stream()
                    .mapToInt(Evaluation::getRating)
                    .average()
                    .orElse(0.0);
            teacher.setRating(BigDecimal.valueOf(avgRating).setScale(1, BigDecimal.ROUND_HALF_UP));
            teacher.setReviewCount(evaluations.size());
        }

        teacherMapper.updateById(teacher);
    }
}
