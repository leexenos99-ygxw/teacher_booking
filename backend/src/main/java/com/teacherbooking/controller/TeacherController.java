package com.teacherbooking.controller;

import com.teacherbooking.common.result.Result;
import com.teacherbooking.entity.Teacher;
import com.teacherbooking.entity.TeacherCalendar;
import com.teacherbooking.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师端控制器
 */
@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
@Tag(name = "教师端管理", description = "教师个人资料、时间表管理等接口")
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping("/profile")
    @Operation(summary = "获取当前教师个人资料")
    public Result<Teacher> getTeacherProfile() {
        return Result.success(teacherService.getCurrentTeacher());
    }

    @PutMapping("/profile")
    @Operation(summary = "更新教师个人资料")
    public Result<Void> updateTeacherProfile(@RequestBody Teacher teacher) {
        Teacher current = teacherService.getCurrentTeacher();
        teacherService.updateTeacherProfile(current.getId(), teacher);
        return Result.success("更新成功", null);
    }

    @GetMapping("/calendar")
    @Operation(summary = "获取教师时间表")
    public Result<List<TeacherCalendar>> getCalendar(
            @RequestParam(required = false) java.time.LocalDate startDate,
            @RequestParam(required = false) java.time.LocalDate endDate) {
        Teacher current = teacherService.getCurrentTeacher();
        return Result.success(teacherService.getTeacherCalendar(current.getId(), startDate, endDate));
    }

    @PostMapping("/calendar")
    @Operation(summary = "设置教师可预约时间")
    public Result<Void> setCalendar(@RequestBody List<TeacherCalendar> calendars) {
        Teacher current = teacherService.getCurrentTeacher();
        teacherService.setTeacherCalendar(current.getId(), calendars);
        return Result.success("设置成功", null);
    }
}
