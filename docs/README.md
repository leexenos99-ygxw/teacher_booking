# 教师交流预约管理系统

## 项目简介

教师交流预约管理系统是一个规范化家校沟通平台，核心理念是摒弃即时闲聊式沟通，建立规范化家校沟通机制。

**核心流程：预约 → 确认 → 限时沟通 → 全程记录 → 事后反馈**

## 技术栈

### 前端
- Vue 3 + TypeScript + Vite
- Element Plus UI组件库
- Pinia 状态管理
- Axios HTTP客户端
- Vue Router 路由管理
- ECharts 图表库

### 后端
- Spring Boot 3 + Java 17
- Spring Security + JWT 认证授权
- MyBatis Plus ORM框架
- MySQL 8 关系型数据库
- Redis 缓存
- WebSocket 实时通信

### 部署
- Nginx 反向代理
- Docker 容器化部署
- Docker Compose 编排

## 系统角色

### 家长（Parent）
- 账号注册、登录
- 查看全部教师信息
- 查看教师可预约时间段
- 发起沟通预约申请
- 实时查看个人预约状态
- 预约有效时间内在线沟通
- 查看全部历史沟通、预约记录
- 对教师沟通服务进行评价

### 教师（Teacher）
- 账号注册、登录
- 完善个人职业资料、展示信息
- 自主设置个人可沟通、可预约时间段
- 查看所有收到的预约申请
- 接受/拒绝家长预约申请
- 灵活修改预约沟通时间
- 开启限时家校沟通
- 查看对应学生历史沟通记录
- 查看沟通总结归档内容

### 管理员（Admin）
- 全平台用户统一管理
- 教师账号资质审核
- 预约数据统计、数据分析
- 系统基础参数配置
- 系统操作日志查看与审计

## 目录结构

```
teacher-booking-system
├── frontend/          # 前端项目
│   ├── src/
│   │   ├── api/       # API接口封装
│   │   ├── components/ # 公共组件
│   │   ├── layouts/   # 布局组件
│   │   ├── router/    # 路由配置
│   │   ├── stores/    # Pinia状态管理
│   │   ├── styles/    # 全局样式
│   │   ├── types/     # TypeScript类型定义
│   │   ├── utils/     # 工具函数
│   │   └── views/     # 页面组件
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── package.json
│   ├── vite.config.ts
│   └── tsconfig.json
├── backend/           # 后端项目
│   ├── src/main/java/com/teacherbooking/
│   │   ├── common/    # 公共模块
│   │   ├── config/    # 配置类
│   │   ├── controller/ # 控制层
│   │   ├── dto/       # 数据传输对象
│   │   ├── entity/    # 实体类
│   │   ├── enums/     # 枚举类
│   │   ├── mapper/    # Mapper接口
│   │   ├── security/  # 安全模块
│   │   ├── service/   # 业务逻辑层
│   │   ├── vo/        # 视图对象
│   │   └── websocket/ # WebSocket模块
│   ├── Dockerfile
│   └── pom.xml
├── database/          # 数据库脚本
│   └── schema.sql
├── docker/            # 容器部署配置
│   └── docker-compose.yml
└── docs/              # 项目文档
    ├── README.md      # 项目说明文档
    ├── requirements.md # 需求文档
    ├── architecture.md # 架构设计文档
    ├── ER.md          # ER图设计
    ├── API.md         # 接口文档
    └── deployment.md  # 部署手册
```

## 快速开始

### 环境要求
- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Redis 6.0+
- Docker & Docker Compose（可选）

### 本地开发

1. 克隆项目
```bash
git clone <repository-url>
cd teacher-booking-system
```

2. 初始化数据库
```bash
mysql -u root -p < database/schema.sql
```

3. 启动后端
```bash
cd backend
./mvnw spring-boot:run
```

4. 启动前端
```bash
cd frontend
npm install
npm run dev
```

### Docker 部署

```bash
docker compose up -d
```

## 默认测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 教师 | teacher1 | teacher123 |
| 家长 | parent1 | parent123 |

## 开发阶段

- [x] Phase 0 产品设计阶段 - 需求文档、架构图、ER图
- [x] Phase 1 数据库设计阶段 - 完整MySQL Schema脚本
- [x] Phase 2 后端开发阶段 - Entity、Mapper、Service、Controller、Swagger
- [x] Phase 3 前端开发阶段 - 登录注册、家长端、教师端、管理后台
- [x] Phase 4 系统功能增强阶段 - 消息通知、AI智能功能
- [ ] Phase 5 系统测试阶段 - 单元测试、接口测试、安全测试
- [x] Phase 6 项目部署阶段 - Dockerfile、docker-compose.yml

## 核心亮点

### 技术亮点
- **前后端分离**：Vue3 + Spring Boot 3 现代化技术栈
- **RBAC权限体系**：家长/教师/管理员三角色权限隔离
- **实时通信**：WebSocket支持在线沟通
- **AI智能**：沟通总结、教师推荐（预留大模型接口）
- **容器化部署**：Docker Compose一键启动

### 安全特性
- JWT无状态认证
- BCrypt密码加密
- 全局异常处理
- SQL注入防护（MyBatis Plus参数化查询）
- 接口级权限控制

### 用户体验
- 响应式设计，适配PC/平板/手机
- Element Plus 企业级UI组件
- 青色主题，简洁专业
- 操作流畅，交互友好
