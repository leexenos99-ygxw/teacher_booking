package com.teacherbooking.service;

import java.util.Map;

/**
 * AI服务接口
 */
public interface AiService {

    /**
     * AI沟通总结
     * @param appointmentId 预约ID
     * @return 总结结果
     */
    Map<String, Object> generateChatSummary(Long appointmentId);

    /**
     * AI教师推荐
     * @param grade 年级
     * @param subject 学科
     * @param problemDescription 问题描述
     * @return 推荐教师列表
     */
    Map<String, Object> recommendTeachers(String grade, String subject, String problemDescription);
}
