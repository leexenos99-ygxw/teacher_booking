# 系统架构设计

## 1. 整体架构图

```
┌─────────────────────────────────────────────────────────────────────┐
│                              用户端                                   │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐            │
│  │ PC浏览器  │  │ 平板浏览器│  │ 手机浏览器│  │  小程序   │            │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘            │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                            Nginx 反向代理                            │
│  静态资源托管 / 负载均衡 / HTTPS / Gzip压缩 / 限流                    │
└─────────────────────────────────────────────────────────────────────┘
                                    │
            ┌───────────────────────┴───────────────────────┐
            ▼                                               ▼
┌───────────────────────┐                   ┌───────────────────────────┐
│    前端 Vue 项目       │                   │      后端 Spring Boot      │
│  - Element Plus       │                   │  - Spring Security + JWT   │
│  - Pinia 状态管理      │                   │  - MyBatis Plus            │
│  - Vue Router         │                   │  - WebSocket               │
│  - Axios              │                   │  - 统一异常处理             │
│  - ECharts            │                   │  - 接口限流                 │
└───────────────────────┘                   └───────────────────────────┘
                                                        │
                        ┌───────────────────────────────┼───────────────────────────────┐
                        ▼                               ▼                               ▼
            ┌───────────────────┐           ┌───────────────────┐           ┌───────────────────┐
            │   MySQL 数据库     │           │    Redis 缓存      │           │   文件存储服务      │
            │  - 用户表          │           │  - JWT Token       │           │  - 头像图片         │
            │  - 教师表          │           │  - 验证码           │           │  - 聊天附件         │
            │  - 预约表          │           │  - 在线用户         │           │                    │
            │  - 消息表          │           │  - 热门数据缓存     │           │                    │
            │  - 评价表          │           │                   │           │                    │
            └───────────────────┘           └───────────────────┘           └───────────────────┘
```

## 2. 前端架构

### 2.1 目录结构
```
frontend/
├── public/              # 静态资源
├── src/
│   ├── api/            # API接口封装
│   ├── assets/         # 静态资源
│   ├── components/     # 公共组件
│   ├── layouts/        # 布局组件
│   ├── router/         # 路由配置
│   ├── stores/         # Pinia状态管理
│   ├── utils/          # 工具函数
│   ├── views/          # 页面组件
│   │   ├── auth/       # 认证页面
│   │   ├── parent/     # 家长端页面
│   │   ├── teacher/    # 教师端页面
│   │   └── admin/      # 管理后台
│   ├── App.vue
│   └── main.ts
├── index.html
├── package.json
├── vite.config.ts
└── tsconfig.json
```

### 2.2 技术选型说明
- **Vue 3 + TypeScript**：使用组合式API，类型安全
- **Element Plus**：企业级UI组件库，响应式设计
- **Pinia**：Vue官方推荐状态管理
- **Axios**：HTTP请求库，支持拦截器
- **Vue Router**：路由管理，支持路由守卫
- **ECharts**：数据可视化图表库

## 3. 后端架构

### 3.1 目录结构
```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/teacherbooking/
│   │   │   ├── common/         # 公共模块
│   │   │   │   ├── result/     # 统一返回结果
│   │   │   │   ├── exception/  # 统一异常处理
│   │   │   │   └── utils/      # 工具类
│   │   │   ├── config/         # 配置类
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── RedisConfig.java
│   │   │   │   └── WebSocketConfig.java
│   │   │   ├── entity/         # 实体类
│   │   │   ├── mapper/         # Mapper接口
│   │   │   ├── service/        # 业务逻辑层
│   │   │   │   └── impl/       # 实现类
│   │   │   ├── controller/     # 控制层
│   │   │   ├── dto/            # 数据传输对象
│   │   │   ├── vo/             # 视图对象
│   │   │   ├── enums/          # 枚举类
│   │   │   └── TeacherBookingApplication.java
│   │   └── resources/
│   │       ├── mapper/         # MyBatis XML
│   │       ├── application.yml
│   │       └── logback.xml
│   └── test/                   # 测试代码
└── pom.xml
```

### 3.2 分层架构
```
Controller层（接口层）
    ↓ 接收请求，参数校验，返回结果
Service层（业务逻辑层）
    ↓ 业务逻辑处理，事务控制
Mapper层（数据访问层）
    ↓ 数据库CRUD操作
Entity层（实体层）
    ↓ 数据库表映射
MySQL / Redis
```

### 3.3 核心模块划分
- **认证模块**：注册、登录、JWT校验、权限控制
- **用户模块**：用户信息管理、教师资料、家长信息
- **预约模块**：预约创建、状态管理、时间管理
- **消息模块**：WebSocket实时通信、历史消息
- **评价模块**：服务评价、评分统计
- **通知模块**：系统通知、消息推送
- **统计模块**：数据分析、报表生成
- **系统模块**：参数配置、日志审计

## 4. 数据库设计

### 4.1 ER图
详见 [ER图文档](ER.md)

### 4.2 核心数据表
| 表名 | 说明 | 核心字段 |
|------|------|----------|
| user | 用户表 | id, username, password, phone, role, status |
| teacher | 教师表 | id, user_id, subject, school, rating |
| parent | 家长表 | id, user_id |
| student | 学生表 | id, parent_id, name, grade |
| teacher_calendar | 教师时间表 | id, teacher_id, date, start_time, end_time, status |
| appointment | 预约表 | id, teacher_id, parent_id, student_id, time, status, reason |
| message | 消息表 | id, appointment_id, sender_id, content, type |
| evaluation | 评价表 | id, appointment_id, rating, content |
| notification | 通知表 | id, user_id, title, content, type, is_read |
| sys_log | 操作日志 | id, user_id, operation, method, params, ip |
| sys_config | 系统配置 | id, config_key, config_value, remark |

## 5. 部署架构

### 5.1 Docker容器编排
```
┌─────────────────────────────────────────────────────────┐
│                   Docker Compose                         │
│                                                           │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐ │
│  │ frontend │  │ backend  │  │  mysql   │  │  redis   │ │
│  │  (Nginx) │  │(Spring)  │  │  (3306)  │  │  (6379)  │ │
│  │   :80    │  │  :8080   │  │          │  │          │ │
│  └────┬─────┘  └────┬─────┘  └──────────┘  └──────────┘ │
│       │              │                                   │
│       └──────────────┘                                   │
│            内部网络互通                                   │
└─────────────────────────────────────────────────────────┘
```

### 5.2 部署流程
1. 代码构建（前端打包、后端编译）
2. 构建Docker镜像
3. Docker Compose启动容器
4. 数据库初始化
5. Nginx反向代理配置

## 6. 安全架构

### 6.1 认证授权
- JWT Token 无状态认证
- Spring Security 权限控制
- RBAC 角色权限模型
- 接口级权限校验

### 6.2 数据安全
- 密码BCrypt加密存储
- 敏感数据脱敏
- SQL注入防护（MyBatis Plus参数化查询）
- XSS防护（输入过滤+输出转义）

### 6.3 接口安全
- 接口限流（Redis+注解实现）
- 防重复提交
- 请求签名校验
- HTTPS传输加密

### 6.4 日志审计
- 操作日志记录
- 异常日志监控
- 登录日志追踪
- 敏感操作审计
