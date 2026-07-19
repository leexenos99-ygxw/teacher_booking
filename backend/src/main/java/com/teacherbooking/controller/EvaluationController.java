package com.teacherbooking.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teacherbooking.common.result.Result;
import com.teacherbooking.entity.Evaluation;
import com.teacherbooking.service.EvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 评价控制器
 */
@RestController
@RequestMapping("/parent/evaluations")
@RequiredArgsConstructor
@Tag(name = "评价管理", description = "家长评价教师服务接口")
public class EvaluationController {

    private final EvaluationService evaluationService;

    @PostMapping
    @Operation(summary = "创建评价")
    public Result<Evaluation> createEvaluation(@RequestBody Evaluation evaluation) {
        return Result.success(evaluationService.createEvaluation(evaluation));
    }

    @GetMapping("/appointment/{appointmentId}")
    @Operation(summary = "获取预约评价")
    public Result<Evaluation> getEvaluationByAppointment(@PathVariable Long appointmentId) {
        return Result.success(evaluationService.getEvaluationByAppointment(appointmentId));
    }
}
