# 部署手册

## 一、环境要求

- Docker 20.10+
- Docker Compose v2.0+
- 至少 2GB 可用内存
- 至少 5GB 可用磁盘空间

## 二、快速部署

### 2.1 克隆项目

```bash
git clone <repository-url>
cd teacher-booking-system
```

### 2.2 一键启动

```bash
cd docker
docker compose up -d
```

### 2.3 查看状态

```bash
docker compose ps
```

### 2.4 访问系统

- 前端地址: http://localhost
- 后端API: http://localhost/api
- API文档: http://localhost/api/doc.html
- MySQL: localhost:3306 (root / root123456)
- Redis: localhost:6379 (redis123456)

### 2.5 停止服务

```bash
docker compose down
```

### 2.6 重新构建

```bash
docker compose build
docker compose up -d
```

## 三、默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 教师 | teacher1 | teacher123 |
| 家长 | parent1 | parent123 |

## 四、配置说明

### 4.1 修改端口

编辑 `docker/docker-compose.yml`：

```yaml
services:
  frontend:
    ports:
      - "8080:80"  # 前端端口
  backend:
    ports:
      - "8081:8080"  # 后端端口
  mysql:
    ports:
      - "3307:3306"  # MySQL端口
  redis:
    ports:
      - "6380:6379"  # Redis端口
```

### 4.2 修改数据库密码

编辑 `docker/docker-compose.yml`：

```yaml
mysql:
  environment:
    MYSQL_ROOT_PASSWORD: your_password
    MYSQL_PASSWORD: your_password
```

同时修改后端环境变量：

```yaml
backend:
  environment:
    SPRING_DATASOURCE_PASSWORD: your_password
```

### 4.3 修改Redis密码

编辑 `docker/docker-compose.yml`：

```yaml
redis:
  command: redis-server --appendonly yes --requirepass "your_password"

backend:
  environment:
    SPRING_DATA_REDIS_PASSWORD: your_password
```

## 五、数据持久化

### 5.1 数据卷

- MySQL数据: `docker_mysql-data`
- Redis数据: `docker_redis-data`

### 5.2 数据备份

```bash
# 备份MySQL
docker exec teacher-booking-mysql mysqldump -uroot -proot123456 teacher_booking > backup.sql

# 恢复MySQL
docker exec -i teacher-booking-mysql mysql -uroot -proot123456 teacher_booking < backup.sql

# 备份Redis
docker exec teacher-booking-redis redis-cli -a redis123456 --rdb /data/dump.rdb
docker cp teacher-booking-redis:/data/dump.rdb ./redis-backup.rdb
```

## 六、常见问题

### 6.1 端口被占用

修改 `docker-compose.yml` 中的端口映射，确保不与本地其他服务冲突。

### 6.2 后端启动失败

```bash
# 查看后端日志
docker logs teacher-booking-backend
```

常见原因：
- MySQL未就绪（等待健康检查通过）
- Redis连接失败
- 数据库密码不匹配

### 6.3 前端502错误

```bash
# 检查后端是否启动
docker logs teacher-booking-backend

# 检查Nginx配置
docker exec teacher-booking-frontend cat /etc/nginx/conf.d/default.conf
```

### 6.4 数据库初始化失败

```bash
# 检查MySQL日志
docker logs teacher-booking-mysql

# 删除旧数据卷后重新启动
docker compose down -v
docker compose up -d
```

## 七、生产环境建议

1. **修改默认密码**: 务必修改MySQL、Redis的默认密码
2. **配置HTTPS**: 使用Nginx配置SSL证书
3. **数据备份**: 定期备份数据库数据
4. **资源限制**: 根据服务器配置调整容器资源限制
5. **日志管理**: 配置日志轮转，避免日志文件过大
6. **监控告警**: 接入监控系统，设置告警规则

## 八、生产环境配置示例

```yaml
backend:
  deploy:
    resources:
      limits:
        memory: 1G
        cpus: '0.5'
      reservations:
        memory: 512M

  logging:
    driver: "json-file"
    options:
      max-size: "100m"
      max-file: "3"
```
