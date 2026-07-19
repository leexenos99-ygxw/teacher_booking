package com.teacherbooking.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teacherbooking.entity.Evaluation;

/**
 * 评价服务接口
 */
public interface EvaluationService {

    /**
     * 创建评价
     */
    Evaluation createEvaluation(Evaluation evaluation);

    /**
     * 获取预约评价
     */
    Evaluation getEvaluationByAppointment(Long appointmentId);

    /**
     * 获取教师评价列表
     */
    IPage<Evaluation> getTeacherEvaluations(Long teacherId, int page, int size);
}
