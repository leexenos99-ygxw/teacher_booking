package com.teacherbooking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teacherbooking.entity.Appointment;
import com.teacherbooking.entity.Message;
import com.teacherbooking.entity.Teacher;
import com.teacherbooking.mapper.AppointmentMapper;
import com.teacherbooking.mapper.MessageMapper;
import com.teacherbooking.mapper.TeacherMapper;
import com.teacherbooking.service.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AI服务实现类
 * 说明：本模块预留AI大模型接入接口，可根据配置的API Key和URL接入真实大模型
 * 当前实现为模拟版本，用于演示流程
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final MessageMapper messageMapper;
    private final AppointmentMapper appointmentMapper;
    private final TeacherMapper teacherMapper;

    @Value("${system.ai.enable:false}")
    private boolean aiEnabled;

    @Value("${system.ai.api-key:}")
    private String apiKey;

    @Value("${system.ai.api-url:}")
    private String apiUrl;

    @Override
    public Map<String, Object> generateChatSummary(Long appointmentId) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new RuntimeException("预约不存在");
        }

        List<Message> messages = messageMapper.selectList(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getAppointmentId, appointmentId)
                        .orderByAsc(Message::getCreatedTime)
        );

        if (aiEnabled && !apiKey.isEmpty() && !apiUrl.isEmpty()) {
            return callAiForSummary(messages);
        }

        return generateMockSummary(messages, appointment);
    }

    @Override
    public Map<String, Object> recommendTeachers(String grade, String subject, String problemDescription) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teacher::getAuditStatus, "APPROVED");

        if (subject != null && !subject.isEmpty()) {
            wrapper.eq(Teacher::getSubject, subject);
        }

        wrapper.orderByDesc(Teacher::getRating, Teacher::getReviewCount);
        List<Teacher> teachers = teacherMapper.selectList(wrapper);

        List<Teacher> recommendedTeachers;
        if (aiEnabled && !apiKey.isEmpty() && !apiUrl.isEmpty()) {
            recommendedTeachers = callAiForRecommendation(teachers, grade, subject, problemDescription);
        } else {
            recommendedTeachers = teachers.stream().limit(5).collect(Collectors.toList());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("teachers", recommendedTeachers);
        result.put("aiEnabled", aiEnabled);
        result.put("recommendationReason", aiEnabled
                ? "AI智能推荐：根据您的问题描述和需求，为您匹配以下教师"
                : "系统推荐：按评分和评价数量排序推荐");

        return result;
    }

    /**
     * 调用AI大模型生成沟通总结（预留接口）
     */
    private Map<String, Object> callAiForSummary(List<Message> messages) {
        // TODO: 接入真实大模型API
        log.info("调用AI生成总结，消息数量: {}", messages.size());

        Map<String, Object> result = new HashMap<>();
        result.put("studentProblems", Arrays.asList(
                "需要通过真实大模型分析学生问题点",
                "请配置AI API Key以启用智能总结功能"
        ));
        result.put("teacherSuggestions", Arrays.asList(
                "建议配置AI API Key",
                "配置后将自动生成个性化改进建议"
        ));
        result.put("followUpPlan", Arrays.asList(
                "配置AI功能后自动生成后续沟通计划",
                "支持根据学生情况动态调整"
        ));
        result.put("isAiGenerated", false);

        return result;
    }

    /**
     * 调用AI大模型推荐教师（预留接口）
     */
    private List<Teacher> callAiForRecommendation(List<Teacher> teachers, String grade,
                                                   String subject, String problemDescription) {
        // TODO: 接入真实大模型API
        log.info("调用AI推荐教师，年级: {}, 学科: {}, 问题描述: {}", grade, subject, problemDescription);
        return teachers.stream().limit(5).collect(Collectors.toList());
    }

    /**
     * 生成模拟总结数据
     */
    private Map<String, Object> generateMockSummary(List<Message> messages, Appointment appointment) {
        Map<String, Object> result = new HashMap<>();

        result.put("appointmentId", appointment.getId());
        result.put("messageCount", messages.size());
        result.put("isAiGenerated", false);
        result.put("summaryNote", "演示模式总结 - 配置真实AI API Key后将启用智能总结");

        result.put("studentProblems", Arrays.asList(
                "学习态度认真，需要加强知识点的系统梳理",
                "课堂表现积极，但部分难点掌握不够牢固",
                "建议增加课后练习，巩固课堂所学内容"
        ));

        result.put("teacherSuggestions", Arrays.asList(
                "建议制定系统的学习计划，按知识点模块推进",
                "增加典型例题训练，培养解题思路",
                "定期进行知识点检测，及时查漏补缺",
                "鼓励学生多提问，培养主动思考习惯"
        ));

        result.put("followUpPlan", Arrays.asList(
                "第一周：基础知识巩固，建立知识框架",
                "第二周：重点难点突破，针对性训练",
                "第三周：综合练习提升，培养解题能力",
                "第四周：复习检测，评估学习效果"
        ));

        return result;
    }
}
