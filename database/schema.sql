-- ============================================================
-- 教师交流预约管理系统 - 数据库初始化脚本
-- 数据库版本: MySQL 8.0+
-- 字符集: utf8mb4
-- 创建日期: 2026-07-18
-- ============================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `teacher_booking`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE `teacher_booking`;

-- ============================================================
-- 1. 用户表
-- ============================================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码(BCrypt加密)',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `role` varchar(20) NOT NULL COMMENT '角色: PARENT-家长 TEACHER-教师 ADMIN-管理员',
  `status` varchar(20) NOT NULL DEFAULT 'NORMAL' COMMENT '状态: NORMAL-正常 DISABLED-禁用',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(50) DEFAULT NULL COMMENT '最后登录IP',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除: 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_phone` (`phone`),
  KEY `idx_role` (`role`),
  KEY `idx_status` (`status`),
  KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================================
-- 2. 教师表
-- ============================================================
DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '关联用户ID',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `subject` varchar(50) DEFAULT NULL COMMENT '教授学科',
  `school` varchar(100) DEFAULT NULL COMMENT '所在学校',
  `title` varchar(50) DEFAULT NULL COMMENT '职称',
  `description` text COMMENT '个人简介',
  `teaching_years` int DEFAULT '0' COMMENT '教龄',
  `rating` decimal(2,1) NOT NULL DEFAULT '0.0' COMMENT '综合评分',
  `review_count` int NOT NULL DEFAULT '0' COMMENT '评价数量',
  `appointment_count` int NOT NULL DEFAULT '0' COMMENT '预约完成次数',
  `audit_status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '审核状态: PENDING-待审核 APPROVED-已通过 REJECTED-已拒绝',
  `audit_remark` varchar(500) DEFAULT NULL COMMENT '审核意见',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_subject` (`subject`),
  KEY `idx_audit_status` (`audit_status`),
  KEY `idx_rating` (`rating`),
  KEY `idx_school` (`school`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教师表';

-- ============================================================
-- 3. 家长表
-- ============================================================
DROP TABLE IF EXISTS `parent`;
CREATE TABLE `parent` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '关联用户ID',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='家长表';

-- ============================================================
-- 4. 学生表
-- ============================================================
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` bigint NOT NULL COMMENT '关联家长ID',
  `name` varchar(50) NOT NULL COMMENT '学生姓名',
  `grade` varchar(50) DEFAULT NULL COMMENT '年级',
  `class_name` varchar(50) DEFAULT NULL COMMENT '班级',
  `gender` tinyint DEFAULT NULL COMMENT '性别: 1-男 2-女',
  `age` int DEFAULT NULL COMMENT '年龄',
  `school` varchar(100) DEFAULT NULL COMMENT '学校',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_name` (`name`),
  KEY `idx_grade` (`grade`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生表';

-- ============================================================
-- 5. 教师时间表
-- ============================================================
DROP TABLE IF EXISTS `teacher_calendar`;
CREATE TABLE `teacher_calendar` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `date` date NOT NULL COMMENT '日期',
  `start_time` time NOT NULL COMMENT '开始时间',
  `end_time` time NOT NULL COMMENT '结束时间',
  `status` varchar(20) NOT NULL DEFAULT 'AVAILABLE' COMMENT '状态: AVAILABLE-可预约 FULL-已满 CLOSED-已关闭',
  `capacity` int NOT NULL DEFAULT '1' COMMENT '可预约人数',
  `booked_count` int NOT NULL DEFAULT '0' COMMENT '已预约人数',
  `duration` int NOT NULL DEFAULT '30' COMMENT '单次时长(分钟)',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_teacher_date` (`teacher_id`, `date`),
  KEY `idx_status` (`status`),
  KEY `idx_date` (`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教师时间表';

-- ============================================================
-- 6. 预约表（核心表）
-- ============================================================
DROP TABLE IF EXISTS `appointment`;
CREATE TABLE `appointment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `appointment_no` varchar(32) NOT NULL COMMENT '预约编号',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `parent_id` bigint NOT NULL COMMENT '家长ID',
  `student_id` bigint DEFAULT NULL COMMENT '学生ID',
  `calendar_id` bigint DEFAULT NULL COMMENT '关联时间表ID',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `status` varchar(20) NOT NULL DEFAULT 'WAITING' COMMENT '状态: WAITING-待确认 ACCEPT-已接受 REJECT-已拒绝 FINISH-已完成 CANCEL-已取消',
  `reason` varchar(1000) DEFAULT NULL COMMENT '预约事由',
  `duration` int DEFAULT NULL COMMENT '沟通时长(分钟)',
  `reject_reason` varchar(500) DEFAULT NULL COMMENT '拒绝原因',
  `cancel_reason` varchar(500) DEFAULT NULL COMMENT '取消原因',
  `cancel_by` varchar(20) DEFAULT NULL COMMENT '取消方: PARENT-家长 TEACHER-教师 ADMIN-管理员',
  `actual_start_time` datetime DEFAULT NULL COMMENT '实际开始时间',
  `actual_end_time` datetime DEFAULT NULL COMMENT '实际结束时间',
  `summary` text COMMENT '沟通总结',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_appointment_no` (`appointment_no`),
  KEY `idx_teacher_status` (`teacher_id`, `status`),
  KEY `idx_parent_status` (`parent_id`, `status`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_calendar_id` (`calendar_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预约表';

-- ============================================================
-- 7. 消息表
-- ============================================================
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `appointment_id` bigint NOT NULL COMMENT '预约ID',
  `sender_id` bigint NOT NULL COMMENT '发送者用户ID',
  `receiver_id` bigint NOT NULL COMMENT '接收者用户ID',
  `content` text NOT NULL COMMENT '消息内容',
  `type` varchar(20) NOT NULL DEFAULT 'TEXT' COMMENT '消息类型: TEXT-文本 IMAGE-图片 FILE-文件',
  `is_read` tinyint NOT NULL DEFAULT '0' COMMENT '是否已读: 0-未读 1-已读',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`id`),
  KEY `idx_appointment_id` (`appointment_id`),
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_receiver_id` (`receiver_id`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- ============================================================
-- 8. 评价表
-- ============================================================
DROP TABLE IF EXISTS `evaluation`;
CREATE TABLE `evaluation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `appointment_id` bigint NOT NULL COMMENT '预约ID',
  `parent_id` bigint NOT NULL COMMENT '家长ID',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `rating` int NOT NULL COMMENT '评分: 1-5分',
  `content` text COMMENT '评价内容',
  `tags` varchar(255) DEFAULT NULL COMMENT '评价标签(逗号分隔)',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_appointment_id` (`appointment_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_rating` (`rating`),
  KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价表';

-- ============================================================
-- 9. 通知表
-- ============================================================
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '接收用户ID',
  `title` varchar(100) NOT NULL COMMENT '通知标题',
  `content` varchar(500) DEFAULT NULL COMMENT '通知内容',
  `type` varchar(30) NOT NULL COMMENT '通知类型: APPOINTMENT_NEW-新预约 APPOINTMENT_ACCEPT-预约通过 APPOINTMENT_REJECT-预约拒绝 APPOINTMENT_REMIND-预约提醒 SYSTEM-系统通知',
  `biz_id` bigint DEFAULT NULL COMMENT '业务ID',
  `is_read` tinyint NOT NULL DEFAULT '0' COMMENT '是否已读: 0-未读 1-已读',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_read` (`user_id`, `is_read`),
  KEY `idx_type` (`type`),
  KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- ============================================================
-- 10. 操作日志表
-- ============================================================
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint DEFAULT NULL COMMENT '操作用户ID',
  `username` varchar(50) DEFAULT NULL COMMENT '操作用户名',
  `module` varchar(50) DEFAULT NULL COMMENT '模块名',
  `operation` varchar(100) DEFAULT NULL COMMENT '操作描述',
  `method` varchar(200) DEFAULT NULL COMMENT '请求方法',
  `params` text COMMENT '请求参数',
  `result` text COMMENT '返回结果',
  `ip` varchar(50) DEFAULT NULL COMMENT '操作IP',
  `location` varchar(100) DEFAULT NULL COMMENT '操作地点',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态: 1-成功 0-失败',
  `error_msg` varchar(2000) DEFAULT NULL COMMENT '错误信息',
  `time` bigint DEFAULT NULL COMMENT '耗时(毫秒)',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_created_time` (`created_time`),
  KEY `idx_module` (`module`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ============================================================
-- 11. 系统配置表
-- ============================================================
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` varchar(50) NOT NULL COMMENT '配置键',
  `config_value` varchar(500) DEFAULT NULL COMMENT '配置值',
  `config_type` varchar(20) DEFAULT 'STRING' COMMENT '配置类型: STRING-字符串 NUMBER-数字 BOOLEAN-布尔 JSON-JSON',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注说明',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- ============================================================
-- 初始化数据
-- ============================================================

-- 初始化管理员账号 (密码: admin123, 需要BCrypt加密)
-- 实际部署时请修改密码
INSERT INTO `sys_user` (`username`, `password`, `phone`, `role`, `status`, `nickname`, `created_time`) VALUES
('admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '13800000000', 'ADMIN', 'NORMAL', '系统管理员', NOW());

-- 初始化系统配置
INSERT INTO `sys_config` (`config_key`, `config_value`, `config_type`, `remark`) VALUES
('system.name', '教师交流预约管理系统', 'STRING', '系统名称'),
('system.logo', '', 'STRING', '系统Logo'),
('appointment.default_duration', '30', 'NUMBER', '默认预约时长(分钟)'),
('appointment.max_days_ahead', '7', 'NUMBER', '最多可提前预约天数'),
('appointment.cancel_before_hours', '2', 'NUMBER', '预约前几小时可取消'),
('notification.remind_before_minutes', '30', 'NUMBER', '提前多少分钟发送预约提醒'),
('teacher.auto_audit', 'false', 'BOOLEAN', '教师注册是否自动审核通过'),
('file.upload.max_size', '10', 'NUMBER', '文件上传大小限制(MB)'),
('chat.message.max_length', '500', 'NUMBER', '聊天消息最大长度'),
('ai.enable', 'false', 'BOOLEAN', '是否启用AI功能'),
('ai.api_key', '', 'STRING', 'AI API Key'),
('ai.api_url', '', 'STRING', 'AI API地址');

-- ============================================================
-- 测试数据（可选，开发环境使用）
-- ============================================================

-- 测试教师账号 (密码: teacher123)
INSERT INTO `sys_user` (`username`, `password`, `phone`, `role`, `status`, `nickname`, `created_time`) VALUES
('teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800000001', 'TEACHER', 'NORMAL', '张老师', NOW()),
('teacher2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800000002', 'TEACHER', 'NORMAL', '李老师', NOW());

INSERT INTO `teacher` (`user_id`, `real_name`, `subject`, `school`, `title`, `description`, `teaching_years`, `rating`, `review_count`, `audit_status`, `audit_time`) VALUES
(2, '张明', '数学', '第一中学', '高级教师', '从教15年，擅长初中数学辅导，教学经验丰富。', 15, 4.8, 120, 'APPROVED', NOW()),
(3, '李华', '英语', '第二中学', '一级教师', '英语专业八级，擅长英语口语和听力训练。', 8, 4.6, 85, 'APPROVED', NOW());

-- 测试家长账号 (密码: parent123)
INSERT INTO `sys_user` (`username`, `password`, `phone`, `role`, `status`, `nickname`, `created_time`) VALUES
('parent1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13900000001', 'PARENT', 'NORMAL', '王女士', NOW()),
('parent2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13900000002', 'PARENT', 'NORMAL', '刘先生', NOW());

INSERT INTO `parent` (`user_id`, `real_name`) VALUES
(4, '王芳'),
(5, '刘强');

-- 测试学生数据
INSERT INTO `student` (`parent_id`, `name`, `grade`, `class_name`, `gender`, `age`, `school`) VALUES
(1, '小明', '初二', '1班', 1, 14, '第一中学'),
(1, '小红', '小五', '3班', 2, 11, '第一小学'),
(2, '小刚', '初三', '2班', 1, 15, '第二中学');

-- 测试教师时间表（未来7天）
INSERT INTO `teacher_calendar` (`teacher_id`, `date`, `start_time`, `end_time`, `status`, `capacity`, `booked_count`, `duration`) VALUES
(1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '09:00:00', '10:00:00', 'AVAILABLE', 1, 0, 30),
(1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '14:00:00', '15:00:00', 'AVAILABLE', 1, 0, 30),
(1, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '09:00:00', '10:00:00', 'AVAILABLE', 1, 0, 30),
(1, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '19:00:00', '20:00:00', 'AVAILABLE', 1, 0, 30),
(1, DATE_ADD(CURDATE(), INTERVAL 5 DAY), '09:00:00', '11:00:00', 'AVAILABLE', 2, 0, 60),
(2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '10:00:00', '11:00:00', 'AVAILABLE', 1, 0, 30),
(2, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '15:00:00', '16:00:00', 'AVAILABLE', 1, 0, 30),
(2, DATE_ADD(CURDATE(), INTERVAL 4 DAY), '19:00:00', '20:00:00', 'AVAILABLE', 1, 0, 30);
