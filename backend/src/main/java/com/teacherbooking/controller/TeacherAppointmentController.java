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
 * 教师端预约控制器
 */
@RestController
@RequestMapping("/teacher/appointments")
@RequiredArgsConstructor
@Tag(name = "教师预约管理", description = "教师端预约相关接口")
public class TeacherAppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/list")
    @Operation(summary = "获取我的预约列表")
    public Result<IPage<Appointment>> getAppointments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        return Result.success(appointmentService.getTeacherAppointments(page, size, status));
    }

    @GetMapping("/{appointmentId}")
    @Operation(summary = "获取预约详情")
    public Result<Appointment> getAppointmentDetail(@PathVariable Long appointmentId) {
        return Result.success(appointmentService.getAppointmentDetail(appointmentId));
    }

    @PostMapping("/{appointmentId}/accept")
    @Operation(summary = "接受预约")
    public Result<Void> acceptAppointment(@PathVariable Long appointmentId) {
        appointmentService.acceptAppointment(appointmentId);
        return Result.success("接受成功", null);
    }

    @PostMapping("/{appointmentId}/reject")
    @Operation(summary = "拒绝预约")
    public Result<Void> rejectAppointment(
            @PathVariable Long appointmentId,
            @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.get("reason") : null;
        appointmentService.rejectAppointment(appointmentId, reason);
        return Result.success("已拒绝", null);
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

    @PostMapping("/{appointmentId}/finish")
    @Operation(summary = "完成预约")
    public Result<Void> finishAppointment(@PathVariable Long appointmentId) {
        appointmentService.finishAppointment(appointmentId);
        return Result.success("已完成", null);
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
