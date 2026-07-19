INSERT INTO sys_config (config_key, config_value, config_type, remark) VALUES
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

-- 测试教师账号 (密码: teacher123)
INSERT INTO sys_user (username, password, phone, role, status, nickname) VALUES
('teacher1', '$2a$10$zWTWqCRtu0Gc0deL0HiegONjNvT/eU3scna0q7.r/f4W0NKp6Nx5K', '13800000001', 'TEACHER', 'NORMAL', '张老师'),
('teacher2', '$2a$10$zWTWqCRtu0Gc0deL0HiegONjNvT/eU3scna0q7.r/f4W0NKp6Nx5K', '13800000002', 'TEACHER', 'NORMAL', '李老师');

INSERT INTO teacher (user_id, real_name, subject, school, title, description, teaching_years, rating, review_count, appointment_count, audit_status, audit_time) VALUES
(1, '张明', '数学', '第一中学', '高级教师', '从教15年，擅长初中数学辅导，教学经验丰富。', 15, 4.8, 120, 0, 'APPROVED', CURRENT_TIMESTAMP),
(2, '李华', '英语', '第二中学', '一级教师', '英语专业八级，擅长英语口语和听力训练。', 8, 4.6, 85, 0, 'APPROVED', CURRENT_TIMESTAMP);

-- 测试家长账号 (密码: parent123)
INSERT INTO sys_user (username, password, phone, role, status, nickname) VALUES
('parent1', '$2a$10$skvWTw05YrEok9c09F1Tx.j4z91BAG86mZWEbpoRATr1sQ0/ZIxsC', '13900000001', 'PARENT', 'NORMAL', '王女士'),
('parent2', '$2a$10$skvWTw05YrEok9c09F1Tx.j4z91BAG86mZWEbpoRATr1sQ0/ZIxsC', '13900000002', 'PARENT', 'NORMAL', '刘先生');

INSERT INTO parent (user_id, real_name) VALUES
(3, '王芳'),
(4, '刘强');

-- 测试学生数据
INSERT INTO student (parent_id, name, grade, class_name, gender, age, school) VALUES
(1, '小明', '初二', '1班', 1, 14, '第一中学'),
(1, '小红', '小五', '3班', 2, 11, '第一小学'),
(2, '小刚', '初三', '2班', 1, 15, '第二中学');

-- 测试教师时间表（未来7天）
INSERT INTO teacher_calendar (teacher_id, date, start_time, end_time, status, capacity, booked_count, duration) VALUES
(1, DATEADD('day', 1, CURRENT_DATE), '09:00:00', '10:00:00', 'AVAILABLE', 1, 0, 30),
(1, DATEADD('day', 1, CURRENT_DATE), '14:00:00', '15:00:00', 'AVAILABLE', 1, 0, 30),
(1, DATEADD('day', 2, CURRENT_DATE), '09:00:00', '10:00:00', 'AVAILABLE', 1, 0, 30),
(1, DATEADD('day', 3, CURRENT_DATE), '19:00:00', '20:00:00', 'AVAILABLE', 1, 0, 30),
(1, DATEADD('day', 5, CURRENT_DATE), '09:00:00', '11:00:00', 'AVAILABLE', 2, 0, 60),
(2, DATEADD('day', 1, CURRENT_DATE), '10:00:00', '11:00:00', 'AVAILABLE', 1, 0, 30),
(2, DATEADD('day', 2, CURRENT_DATE), '15:00:00', '16:00:00', 'AVAILABLE', 1, 0, 30),
(2, DATEADD('day', 4, CURRENT_DATE), '19:00:00', '20:00:00', 'AVAILABLE', 1, 0, 30);
