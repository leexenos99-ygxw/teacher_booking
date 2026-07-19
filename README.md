# 🎓 Teacher Booking System

> 一个面向学校家校沟通场景的现代化教师预约管理系统（Teacher Booking System）。

通过数字化预约、实时消息沟通、教师管理和家长端服务，提高家校沟通效率，减少教师时间冲突，为学校提供统一、高效、安全的预约管理平台。

---

# ✨ 项目简介

Teacher Booking 是一个前后端分离的教师预约管理平台，主要服务于学校、教师及家长三类用户。

系统支持：

* 👨‍🏫 教师发布可预约时间
* 👨‍👩‍👧 家长在线预约教师
* 💬 在线聊天沟通
* 📅 预约管理
* ⭐ 教师评价
* 🔔 消息提醒
* 📊 后台数据统计
* 🔐 基于 JWT 的身份认证与权限控制

项目采用现代 Web 技术栈开发，具有良好的扩展性与维护性。

---

# 🚀 核心功能

## 家长端（Parent）

* 用户注册 / 登录
* 浏览教师信息
* 按学科搜索教师
* 查看教师详情
* 在线预约教师
* 管理预约记录
* 在线聊天
* 查看消息通知
* 教师评价

---

## 教师端（Teacher）

* 教师注册
* 完善个人资料
* 设置可预约时间
* 查看预约申请
* 接受 / 拒绝预约
* 管理个人日程
* 回复家长消息
* 查看评价统计

---

## 管理员（Admin）

* 用户管理
* 教师审核
* 家长管理
* 预约管理
* 系统配置
* 数据统计
* 系统公告
* 权限管理

---

# 🏗️ 技术架构

## 后端

* Java 21
* Spring Boot 3
* Spring Security
* JWT Authentication
* MyBatis Plus
* MySQL
* Redis（可选）
* Maven

---

## 前端

* Vue 3
* TypeScript
* Vite
* Element Plus
* Axios
* Pinia

---

## 数据库

* MySQL 8.x

---

## 部署

* Docker（规划支持）
* Nginx（生产环境）
* Linux / Windows

---

# 📂 项目结构

```text
teacher_booking
│
├── backend                 # Spring Boot 后端
│   ├── src
│   ├── resources
│   └── pom.xml
│
├── frontend                # Vue3 前端
│   ├── src
│   ├── public
│   └── package.json
│
├── sql                     # 数据库脚本
│   ├── schema.sql
│   ├── config.sql
│   └── demo.sql
│
├── docs                    # 项目文档
│
└── README.md
```

---

# 🔐 权限模型

系统采用基于角色（RBAC）的权限控制。

| 角色      | 权限                  |
| ------- | ------------------- |
| Admin   | 系统管理、用户管理、教师审核、系统配置 |
| Teacher | 管理个人信息、预约、时间安排、消息   |
| Parent  | 浏览教师、预约、聊天、评价       |

---

# 🗄️ 数据库设计

主要数据表包括：

* sys_user
* teacher
* parent
* student
* appointment
* schedule
* chat_message
* review
* notification
* sys_config

数据库采用逻辑外键设计，提高系统扩展能力。

---

# 🔑 登录账号（开发环境）

| 用户  | 用户名      | 密码           |
| --- | -------- | ------------ |
| 管理员 | admin    | 首次启动初始化或自行配置 |
| 教师  | teacher1 | 开发环境初始化      |
| 家长  | parent1  | 开发环境初始化      |

> ⚠️ 演示账号仅用于本地开发，请勿在生产环境使用默认密码。

---

# ⚙️ 本地运行

## 1. 克隆项目

```bash
git clone <repository-url>
```

## 2. 创建数据库

```sql
CREATE DATABASE teacher_booking DEFAULT CHARACTER SET utf8mb4;
```

执行：

* schema.sql
* config.sql
* demo.sql

---

## 3. 修改配置

复制

```text
application-example.yml
```

为

```text
application-local.yml
```

配置：

* MySQL
* JWT Secret
* Redis（可选）
* AI API（可选）

---

## 4. 启动后端

```bash
mvn spring-boot:run
```

---

## 5. 启动前端

```bash
npm install

npm run dev
```

---

# 📌 项目特点

* 前后端分离架构
* JWT 无状态认证
* RBAC 权限管理
* RESTful API 设计
* 模块化开发
* 响应式界面
* 易于扩展和维护
* 支持后续接入 AI 助手、消息推送和移动端

---

# 📈 后续规划

* AI 智能预约助手
* 微信通知
* 邮件提醒
* 教师排班优化
* 数据可视化分析
* 移动端（UniApp / Flutter）
* Docker 一键部署
* CI/CD 自动部署

---

# 📄 License

本项目仅用于学习交流与课程实践。

如需用于商业用途，请根据实际需求进行完善与安全加固。
