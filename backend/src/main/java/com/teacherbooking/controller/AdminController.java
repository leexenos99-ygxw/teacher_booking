package com.teacherbooking.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teacherbooking.common.result.Result;
import com.teacherbooking.entity.Teacher;
import com.teacherbooking.entity.User;
import com.teacherbooking.mapper.TeacherMapper;
import com.teacherbooking.mapper.UserMapper;
import com.teacherbooking.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "管理后台", description = "管理员后台管理接口")
public class AdminController {

    private final UserMapper userMapper;
    private final TeacherMapper teacherMapper;
    private final TeacherService teacherService;

    @GetMapping("/users")
    @Operation(summary = "用户列表")
    public Result<IPage<User>> getUserList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String keyword) {

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (role != null) {
            wrapper.eq(User::getRole, role);
        }
        if (keyword != null) {
            wrapper.and(w -> w
                    .like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword)
                    .or().like(User::getPhone, keyword)
            );
        }
        wrapper.orderByDesc(User::getCreatedTime);

        return Result.success(userMapper.selectPage(new Page<>(page, size), wrapper));
    }

    @PutMapping("/users/{userId}/status")
    @Operation(summary = "修改用户状态")
    public Result<Void> updateUserStatus(
            @PathVariable Long userId,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setStatus(status);
        userMapper.updateById(user);
        return Result.success("状态已更新", null);
    }

    @GetMapping("/teachers/audit")
    @Operation(summary = "获取待审核教师列表")
    public Result<IPage<Teacher>> getAuditTeachers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String auditStatus) {

        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(auditStatus)) {
            wrapper.eq(Teacher::getAuditStatus, auditStatus);
        }
        wrapper.orderByDesc(Teacher::getCreatedTime);

        return Result.success(teacherMapper.selectPage(new Page<>(page, size), wrapper));
    }

    @PostMapping("/teachers/{teacherId}/audit")
    @Operation(summary = "审核教师")
    public Result<Void> auditTeacher(
            @PathVariable Long teacherId,
            @RequestBody Map<String, String> body) {
        String auditStatus = body.get("auditStatus");
        String auditRemark = body.get("auditRemark");
        teacherService.auditTeacher(teacherId, auditStatus, auditRemark);
        return Result.success("审核完成", null);
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取统计数据")
    public Result<Map<String, Object>> getStatistics() {
        long userCount = userMapper.selectCount(null);
        long teacherCount = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getRole, "TEACHER")
        );
        long parentCount = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getRole, "PARENT")
        );

        Map<String, Object> stats = Map.of(
                "userCount", userCount,
                "teacherCount", teacherCount,
                "parentCount", parentCount
        );

        return Result.success(stats);
    }
}
