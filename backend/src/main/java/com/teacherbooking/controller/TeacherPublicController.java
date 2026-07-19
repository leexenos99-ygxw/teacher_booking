package com.teacherbooking.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teacherbooking.common.result.Result;
import com.teacherbooking.entity.Teacher;
import com.teacherbooking.entity.TeacherCalendar;
import com.teacherbooking.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 教师控制器 - 公共接口
 */
@RestController
@RequestMapping("/public/teachers")
@RequiredArgsConstructor
@Tag(name = "教师管理", description = "教师信息查询、时间表查询等接口")
public class TeacherPublicController {

    private final TeacherService teacherService;

    @GetMapping("/list")
    @Operation(summary = "分页查询教师列表")
    public Result<IPage<Teacher>> getTeacherList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String keyword) {
        return Result.success(teacherService.getTeacherList(page, size, subject, keyword));
    }

    @GetMapping("/{teacherId}")
    @Operation(summary = "获取教师详情")
    public Result<Teacher> getTeacherDetail(@PathVariable Long teacherId) {
        return Result.success(teacherService.getTeacherDetail(teacherId));
    }

    @GetMapping("/{teacherId}/calendar")
    @Operation(summary = "获取教师可预约时间表")
    public Result<List<TeacherCalendar>> getTeacherCalendar(
            @PathVariable Long teacherId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(teacherService.getTeacherCalendar(teacherId, startDate, endDate));
    }
}
