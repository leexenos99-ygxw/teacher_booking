package com.teacherbooking.controller;

import com.teacherbooking.common.result.Result;
import com.teacherbooking.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AI智能功能控制器
 */
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Tag(name = "AI智能功能", description = "AI沟通总结、教师智能推荐等接口")
public class AiController {

    private final AiService aiService;

    @PostMapping("/chat-summary/{appointmentId}")
    @Operation(summary = "AI生成沟通总结")
    public Result<Map<String, Object>> generateChatSummary(@PathVariable Long appointmentId) {
        return Result.success(aiService.generateChatSummary(appointmentId));
    }

    @GetMapping("/recommend-teachers")
    @Operation(summary = "AI智能推荐教师")
    public Result<Map<String, Object>> recommendTeachers(
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String problemDescription) {
        return Result.success(aiService.recommendTeachers(grade, subject, problemDescription));
    }
}
