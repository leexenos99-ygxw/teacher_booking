-- ============================================================
-- 教师交流预约管理系统 - H2数据库初始化脚本 (MySQL兼容模式)
-- ============================================================

-- 1. 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(100) NOT NULL,
  phone VARCHAR(20),
  role VARCHAR(20) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
  avatar VARCHAR(255),
  nickname VARCHAR(50),
  email VARCHAR(100),
  last_login_time TIMESTAMP,
  last_login_ip VARCHAR(50),
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted TINYINT NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX uk_username ON sys_user(username);
CREATE INDEX idx_phone ON sys_user(phone);
CREATE INDEX idx_role ON sys_user(role);
CREATE INDEX idx_status ON sys_user(status);
CREATE INDEX idx_created_time ON sys_user(created_time);

-- 2. 教师表
DROP TABLE IF EXISTS teacher;
CREATE TABLE teacher (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  real_name VARCHAR(50),
  subject VARCHAR(50),
  school VARCHAR(100),
  title VARCHAR(50),
  description TEXT,
  teaching_years INT DEFAULT 0,
  rating DECIMAL(2,1) NOT NULL DEFAULT 0.0,
  review_count INT NOT NULL DEFAULT 0,
  appointment_count INT NOT NULL DEFAULT 0,
  audit_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  audit_remark VARCHAR(500),
  audit_time TIMESTAMP,
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX uk_teacher_user_id ON teacher(user_id);
CREATE INDEX idx_subject ON teacher(subject);
CREATE INDEX idx_audit_status ON teacher(audit_status);
CREATE INDEX idx_rating ON teacher(rating);
CREATE INDEX idx_school ON teacher(school);

-- 3. 家长表
DROP TABLE IF EXISTS parent;
CREATE TABLE parent (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  real_name VARCHAR(50),
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX uk_parent_user_id ON parent(user_id);

-- 4. 学生表
DROP TABLE IF EXISTS student;
CREATE TABLE student (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  parent_id BIGINT NOT NULL,
  name VARCHAR(50) NOT NULL,
  grade VARCHAR(50),
  class_name VARCHAR(50),
  gender TINYINT,
  age INT,
  school VARCHAR(100),
  remark VARCHAR(500),
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_parent_id ON student(parent_id);
CREATE INDEX idx_student_name ON student(name);
CREATE INDEX idx_grade ON student(grade);

-- 5. 教师时间表
DROP TABLE IF EXISTS teacher_calendar;
CREATE TABLE teacher_calendar (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  teacher_id BIGINT NOT NULL,
  date DATE NOT NULL,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
  capacity INT NOT NULL DEFAULT 1,
  booked_count INT NOT NULL DEFAULT 0,
  duration INT NOT NULL DEFAULT 30,
  remark VARCHAR(500),
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_teacher_date ON teacher_calendar(teacher_id, date);
CREATE INDEX idx_calendar_status ON teacher_calendar(status);
CREATE INDEX idx_calendar_date ON teacher_calendar(date);

-- 6. 预约表
DROP TABLE IF EXISTS appointment;
CREATE TABLE appointment (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  appointment_no VARCHAR(32) NOT NULL,
  teacher_id BIGINT NOT NULL,
  parent_id BIGINT NOT NULL,
  student_id BIGINT,
  calendar_id BIGINT,
  start_time TIMESTAMP NOT NULL,
  end_time TIMESTAMP NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'WAITING',
  reason VARCHAR(1000),
  duration INT,
  reject_reason VARCHAR(500),
  cancel_reason VARCHAR(500),
  cancel_by VARCHAR(20),
  actual_start_time TIMESTAMP,
  actual_end_time TIMESTAMP,
  summary TEXT,
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX uk_appointment_no ON appointment(appointment_no);
CREATE INDEX idx_teacher_status ON appointment(teacher_id, status);
CREATE INDEX idx_parent_status ON appointment(parent_id, status);
CREATE INDEX idx_start_time ON appointment(start_time);
CREATE INDEX idx_appointment_calendar_id ON appointment(calendar_id);
CREATE INDEX idx_appointment_student_id ON appointment(student_id);
CREATE INDEX idx_appointment_created_time ON appointment(created_time);

-- 7. 消息表
DROP TABLE IF EXISTS message;
CREATE TABLE message (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  appointment_id BIGINT NOT NULL,
  sender_id BIGINT NOT NULL,
  receiver_id BIGINT NOT NULL,
  content TEXT NOT NULL,
  type VARCHAR(20) NOT NULL DEFAULT 'TEXT',
  is_read TINYINT NOT NULL DEFAULT 0,
  read_time TIMESTAMP,
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_message_appointment_id ON message(appointment_id);
CREATE INDEX idx_sender_id ON message(sender_id);
CREATE INDEX idx_receiver_id ON message(receiver_id);
CREATE INDEX idx_is_read ON message(is_read);
CREATE INDEX idx_message_created_time ON message(created_time);

-- 8. 评价表
DROP TABLE IF EXISTS evaluation;
CREATE TABLE evaluation (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  appointment_id BIGINT NOT NULL,
  parent_id BIGINT NOT NULL,
  teacher_id BIGINT NOT NULL,
  rating INT NOT NULL,
  content TEXT,
  tags VARCHAR(255),
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX uk_evaluation_appointment_id ON evaluation(appointment_id);
CREATE INDEX idx_evaluation_teacher_id ON evaluation(teacher_id);
CREATE INDEX idx_evaluation_parent_id ON evaluation(parent_id);
CREATE INDEX idx_evaluation_rating ON evaluation(rating);
CREATE INDEX idx_evaluation_created_time ON evaluation(created_time);

-- 9. 通知表
DROP TABLE IF EXISTS notification;
CREATE TABLE notification (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  title VARCHAR(100) NOT NULL,
  content VARCHAR(500),
  type VARCHAR(30) NOT NULL,
  biz_id BIGINT,
  is_read TINYINT NOT NULL DEFAULT 0,
  read_time TIMESTAMP,
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_user_read ON notification(user_id, is_read);
CREATE INDEX idx_notification_type ON notification(type);
CREATE INDEX idx_notification_created_time ON notification(created_time);

-- 10. 操作日志表
DROP TABLE IF EXISTS sys_log;
CREATE TABLE sys_log (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  username VARCHAR(50),
  module VARCHAR(50),
  operation VARCHAR(100),
  method VARCHAR(200),
  params TEXT,
  result TEXT,
  ip VARCHAR(50),
  location VARCHAR(100),
  status TINYINT NOT NULL DEFAULT 1,
  error_msg VARCHAR(2000),
  time BIGINT,
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_log_user_id ON sys_log(user_id);
CREATE INDEX idx_log_created_time ON sys_log(created_time);
CREATE INDEX idx_module ON sys_log(module);
CREATE INDEX idx_log_status ON sys_log(status);

-- 11. 系统配置表
DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  config_key VARCHAR(50) NOT NULL,
  config_value VARCHAR(500),
  config_type VARCHAR(20) DEFAULT 'STRING',
  remark VARCHAR(255),
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX uk_config_key ON sys_config(config_key);
