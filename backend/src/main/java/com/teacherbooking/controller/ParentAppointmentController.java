package com.teacherbooking.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teacherbooking.common.result.Result;
import com.teacherbooking.entity.Appointment;
import com.teacherbooking.entity.Message;
import com.teacherbooking.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 家长端预约控制器
 */
@RestController
@RequestMapping("/parent/appointments")
@RequiredArgsConstructor
@Tag(name = "家长预约管理", description = "家长端预约相关接口")
public class ParentAppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @Operation(summary = "创建预约申请")
    public Result<Appointment> createAppointment(@RequestBody Appointment appointment) {
        return Result.success(appointmentService.createAppointment(appointment));
    }

    @GetMapping("/list")
    @Operation(summary = "获取我的预约列表")
    public Result<IPage<Appointment>> getAppointments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        return Result.success(appointmentService.getParentAppointments(page, size, status));
    }

    @GetMapping("/{appointmentId}")
    @Operation(summary = "获取预约详情")
    public Result<Appointment> getAppointmentDetail(@PathVariable Long appointmentId) {
        return Result.success(appointmentService.getAppointmentDetail(appointmentId));
    }

    @PostMapping("/{appointmentId}/cancel")
    @Operation(summary = "取消预约")
    public Result<Void> cancelAppointment(
            @PathVariable Long appointmentId,
            @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.get("reason") : null;
        appointmentService.cancelAppointment(appointmentId, reason);
        return Result.success("取消成功", null);
    }

    @GetMapping("/{appointmentId}/messages")
    @Operation(summary = "获取预约沟通消息列表")
    public Result<List<Message>> getMessages(@PathVariable Long appointmentId) {
        return Result.success(appointmentService.getAppointmentMessages(appointmentId));
    }

    @PostMapping("/{appointmentId}/messages")
    @Operation(summary = "发送消息")
    public Result<Message> sendMessage(
            @PathVariable Long appointmentId,
            @RequestBody Map<String, String> body) {
        String content = body.get("content");
        String type = body.getOrDefault("type", "TEXT");
        return Result.success(appointmentService.sendMessage(appointmentId, content, type));
    }
}
