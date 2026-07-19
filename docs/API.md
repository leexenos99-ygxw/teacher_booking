# API接口文档

## 一、接口规范

### 1.1 统一返回格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | int | 状态码，200表示成功 |
| message | string | 返回消息 |
| data | object | 返回数据 |

### 1.2 认证方式

所有需要登录的接口需在请求头中携带Token：

```
Authorization: Bearer {token}
```

### 1.3 状态码

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |

---

## 二、认证接口

### 2.1 用户登录

- **接口**: `POST /api/auth/login`
- **权限**: 公开
- **描述**: 用户登录获取Token

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 是 | 用户名 |
| password | string | 是 | 密码 |

**返回数据**:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenPrefix": "Bearer",
  "expiresIn": 86400000,
  "userInfo": {
    "userId": 1,
    "username": "admin",
    "nickname": "管理员",
    "avatar": "",
    "phone": "13800000000",
    "role": "ADMIN",
    "roleName": "管理员"
  }
}
```

### 2.2 用户注册

- **接口**: `POST /api/auth/register`
- **权限**: 公开
- **描述**: 用户注册

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 是 | 用户名(4-20位) |
| password | string | 是 | 密码(6-20位) |
| confirmPassword | string | 是 | 确认密码 |
| phone | string | 否 | 手机号 |
| role | string | 是 | 角色：PARENT/TEACHER |
| nickname | string | 否 | 昵称 |
| realName | string | 否 | 真实姓名 |

### 2.3 获取当前用户信息

- **接口**: `GET /api/auth/userInfo`
- **权限**: 登录用户

### 2.4 退出登录

- **接口**: `POST /api/auth/logout`
- **权限**: 登录用户

---

## 三、教师接口（公共）

### 3.1 教师列表

- **接口**: `GET /api/public/teachers/list`
- **权限**: 公开
- **描述**: 分页查询已审核通过的教师列表

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页数量，默认10 |
| subject | string | 否 | 学科筛选 |
| keyword | string | 否 | 关键词搜索 |

### 3.2 教师详情

- **接口**: `GET /api/public/teachers/{teacherId}`
- **权限**: 公开

### 3.3 教师时间表

- **接口**: `GET /api/public/teachers/{teacherId}/calendar`
- **权限**: 公开

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| startDate | string | 否 | 开始日期(yyyy-MM-dd) |
| endDate | string | 否 | 结束日期(yyyy-MM-dd) |

---

## 四、教师端接口

### 4.1 获取个人资料

- **接口**: `GET /api/teacher/profile`
- **权限**: 教师

### 4.2 更新个人资料

- **接口**: `PUT /api/teacher/profile`
- **权限**: 教师

### 4.3 获取时间表

- **接口**: `GET /api/teacher/calendar`
- **权限**: 教师

### 4.4 设置时间表

- **接口**: `POST /api/teacher/calendar`
- **权限**: 教师

### 4.5 预约列表

- **接口**: `GET /api/teacher/appointments/list`
- **权限**: 教师

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码 |
| size | int | 否 | 每页数量 |
| status | string | 否 | 状态筛选 |

### 4.6 接受预约

- **接口**: `POST /api/teacher/appointments/{id}/accept`
- **权限**: 教师

### 4.7 拒绝预约

- **接口**: `POST /api/teacher/appointments/{id}/reject`
- **权限**: 教师

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| reason | string | 否 | 拒绝原因 |

### 4.8 完成预约

- **接口**: `POST /api/teacher/appointments/{id}/finish`
- **权限**: 教师

### 4.9 获取消息列表

- **接口**: `GET /api/teacher/appointments/{id}/messages`
- **权限**: 教师

### 4.10 发送消息

- **接口**: `POST /api/teacher/appointments/{id}/messages`
- **权限**: 教师

---

## 五、家长端接口

### 5.1 创建预约

- **接口**: `POST /api/parent/appointments`
- **权限**: 家长

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| teacherId | long | 是 | 教师ID |
| studentId | long | 否 | 学生ID |
| calendarId | long | 否 | 时间表ID |
| startTime | datetime | 是 | 开始时间 |
| endTime | datetime | 是 | 结束时间 |
| reason | string | 否 | 预约事由 |

### 5.2 预约列表

- **接口**: `GET /api/parent/appointments/list`
- **权限**: 家长

### 5.3 预约详情

- **接口**: `GET /api/parent/appointments/{id}`
- **权限**: 家长

### 5.4 取消预约

- **接口**: `POST /api/parent/appointments/{id}/cancel`
- **权限**: 家长

### 5.5 获取消息列表

- **接口**: `GET /api/parent/appointments/{id}/messages`
- **权限**: 家长

### 5.6 发送消息

- **接口**: `POST /api/parent/appointments/{id}/messages`
- **权限**: 家长

### 5.7 创建评价

- **接口**: `POST /api/parent/evaluations`
- **权限**: 家长

---

## 六、通知接口

### 6.1 通知列表

- **接口**: `GET /api/notifications/list`
- **权限**: 登录用户

### 6.2 未读数量

- **接口**: `GET /api/notifications/unread/count`
- **权限**: 登录用户

### 6.3 标记已读

- **接口**: `PUT /api/notifications/{id}/read`
- **权限**: 登录用户

### 6.4 全部已读

- **接口**: `PUT /api/notifications/read-all`
- **权限**: 登录用户

---

## 七、管理员接口

### 7.1 用户列表

- **接口**: `GET /api/admin/users`
- **权限**: 管理员

### 7.2 修改用户状态

- **接口**: `PUT /api/admin/users/{userId}/status`
- **权限**: 管理员

### 7.3 待审核教师列表

- **接口**: `GET /api/admin/teachers/audit`
- **权限**: 管理员

### 7.4 审核教师

- **接口**: `POST /api/admin/teachers/{teacherId}/audit`
- **权限**: 管理员

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| auditStatus | string | 是 | APPROVED/REJECTED |
| auditRemark | string | 否 | 审核意见 |

### 7.5 统计数据

- **接口**: `GET /api/admin/statistics`
- **权限**: 管理员

---

## 八、AI智能功能接口

### 8.1 AI沟通总结

- **接口**: `POST /api/ai/chat-summary/{appointmentId}`
- **权限**: 登录用户

### 8.2 AI教师推荐

- **接口**: `GET /api/ai/recommend-teachers`
- **权限**: 登录用户

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| grade | string | 否 | 年级 |
| subject | string | 否 | 学科 |
| problemDescription | string | 否 | 问题描述 |

---

## 九、WebSocket接口

### 9.1 实时聊天

- **连接地址**: `ws://{host}/api/ws/chat?token={token}`
- **权限**: 登录用户

**消息格式**:

```json
{
  "appointmentId": 1,
  "content": "你好",
  "type": "TEXT"
}
```

---

## 十、预约状态枚举

| 状态值 | 说明 |
|--------|------|
| WAITING | 待确认 |
| ACCEPT | 已接受 |
| REJECT | 已拒绝 |
| FINISH | 已完成 |
| CANCEL | 已取消 |

---

> 更多详细接口信息请访问 Swagger 文档：`/api/doc.html`
