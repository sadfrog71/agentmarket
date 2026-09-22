# 华衍智能体市场部署说明

本文档用于在 Linux 服务器上部署华衍智能体市场。默认采用 Docker Compose 运行展示前台、管理后台、Java 后端、MySQL 和 Redis。

## 1. 交付内容

部署包包含以下内容：

| 内容 | 目录或文件 |
| --- | --- |
| 展示前台源码 | `agent-marketplace-web/` |
| 企业官网前台源码 | `corporate-site-web/` |
| 管理后台源码 | `agent-marketplace-admin/` |
| Java 后端源码 | `agent-marketplace-backend/` |
| 数据库脚本 | `agent-marketplace-backend/sql/` |
| 容器编排配置 | `docker-compose.yml` |
| 环境变量模板 | `.env.example` |

部署包不包含本机数据库数据、登录令牌、`.env`、Git 历史、依赖缓存和构建缓存。

## 2. 服务器要求

- Linux x86_64 或 ARM64
- Docker Engine 24 或更高版本
- Docker Compose v2
- 建议 4 核 CPU、8 GB 内存、20 GB 可用磁盘
- 本地默认使用 `8080`、`8081`、`8082`、`8083` 和 `8443` 端口；生产环境只需将官网网关暴露到 `80/443`，管理端与后端端口保持在本机或 Docker 网络内。

确认运行环境：

```bash
docker version
docker compose version
```

## 3. 首次部署

解压部署包并进入项目目录：

```bash
unzip agentmarket-deployment-*.zip
cd agentmarket
```

创建环境配置：

```bash
cp .env.example .env
```

编辑 `.env`，至少替换数据库密码：

```dotenv
MYSQL_ROOT_PASSWORD=替换为高强度数据库密码
MYSQL_BIND_HOST=127.0.0.1
REDIS_BIND_HOST=127.0.0.1
RUOYI_JAVA_OPTS=-Xms128m -Xmx384m -XX:MaxMetaspaceSize=192m -XX:MaxDirectMemorySize=64m
BACKEND_BIND_HOST=127.0.0.1
BACKEND_PORT=8080
WEB_BIND_HOST=127.0.0.1
WEB_PORT=8081
CORPORATE_WEB_PORT=8083
CORPORATE_WEB_HTTPS_PORT=8443
ADMIN_BIND_HOST=127.0.0.1
ADMIN_PORT=8082
```

密码建议不少于 16 位，并包含大小写字母、数字和符号。配置完成后启动全部服务：

```bash
docker compose up -d --build
docker compose ps
```

首次启动会创建数据库和数据卷，并自动导入 RuoYi 基础表、业务表、菜单、字典及初始化内容。根据服务器和网络情况，首次构建通常需要数分钟。

## 4. 访问地址

| 服务 | 默认地址 |
| --- | --- |
| 展示前台（本地诊断） | `http://127.0.0.1:8081` |
| 企业官网（本地诊断） | `http://127.0.0.1:8083` |
| 管理后台（本地诊断） | `http://127.0.0.1:8082` |
| 后端 API（本地诊断） | `http://127.0.0.1:8080` |
| Swagger（本地诊断） | `http://127.0.0.1:8080/swagger-ui/index.html` |

生产域名部署时，建议由 HTTPS 网关提供 `https://www.example.com`（官网）、`https://admin.example.com`（管理后台）和 `https://api.example.com`（接口）；不要将管理端或后端容器端口直接暴露到公网。

初始管理员账号为 `admin`，初始密码为 `admin123`。首次登录后必须修改密码。

## 5. 运行检查

检查容器状态：

```bash
docker compose ps
```

正常情况下，`mysql`、`redis`、`backend`、`web` 和 `admin` 均应处于 `Up` 状态，MySQL 和 Redis 应显示 `healthy`。

检查后端日志：

```bash
docker compose logs --tail=200 backend
```

检查公开接口：

```bash
curl http://localhost:8080/open/agents?pageNum=1&pageSize=10
curl http://localhost:8080/open/content/CONTACT
curl 'http://localhost:8080/open/site/v1/pages?path=/index.html'
curl 'http://localhost:8080/open/site/v1/articles?limit=10'
```

## 6. 已有环境升级

升级前先备份数据库和 `.env`。解压新版本后，保留原 `.env`，再按编号执行尚未执行过的升级脚本。

```bash
docker compose exec -T mysql sh -c 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" ry-vue' < agent-marketplace-backend/sql/marketplace-upgrade-001-business.sql
docker compose exec -T mysql sh -c 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" ry-vue' < agent-marketplace-backend/sql/marketplace-upgrade-002-charset.sql
docker compose exec -T mysql sh -c 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" ry-vue' < agent-marketplace-backend/sql/marketplace-upgrade-003-site-content.sql
python3 agent-marketplace-backend/tools/corporate-site-import/upgrade_preflight.py --sql-dir agent-marketplace-backend/sql --target-sql agent-marketplace-backend/sql/marketplace-upgrade-006-site-core.sql
docker compose exec -T mysql sh -c 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" ry-vue' < agent-marketplace-backend/sql/marketplace-upgrade-006-site-core.sql
```

重新构建并启动：

```bash
docker compose up -d --build
docker compose ps
```

全新数据库由 `docker-compose.yml` 自动执行初始化脚本，不需要再次执行升级脚本。

### 企业官网内容迁入

先按 [`docs/corporate-site-migration-runbook.md`](docs/corporate-site-migration-runbook.md) 对冻结源包运行清单校验与媒体落盘，再执行 `docs/corporate-site-import.sql`。该导入包会写入固定页面、新闻、资质和媒体引用；运行前请在备份恢复出的隔离库演练，并保留 manifest 与导入运行记录。不要把源静态 HTML 与官网后台同时作为可编辑源。

## 7. 数据备份与恢复

创建数据库备份：

```bash
mkdir -p backups
docker compose exec -T mysql sh -c 'mysqldump -uroot -p"$MYSQL_ROOT_PASSWORD" --single-transaction --routines --triggers ry-vue' > backups/agentmarket-$(date +%Y%m%d-%H%M%S).sql
```

恢复数据库前应停止业务写入，并确认备份文件正确：

```bash
docker compose exec -T mysql sh -c 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" ry-vue' < backups/agentmarket-backup.sql
```

运行数据保存在以下 Docker 数据卷中：

- `huayan-agent-marketplace_mysql-data`：MySQL 数据
- `huayan-agent-marketplace_upload-data`：后台上传文件
- `huayan-agent-marketplace_site-media-data`：企业官网受控媒体

不要在未完成备份的情况下删除数据卷或执行 `docker compose down -v`。

## 8. 停止与重启

```bash
docker compose stop
docker compose start
```

更新配置或镜像后重启：

```bash
docker compose up -d --build
```

停止并移除容器，但保留数据卷：

```bash
docker compose down
```

## 9. 正式环境安全检查

1. 修改管理员初始密码。
2. 使用高强度 MySQL 密码，并限制 `.env` 文件权限。
3. 在服务器防火墙中关闭不需要的数据库和 Redis 对外端口。
4. 使用反向代理配置正式域名和 HTTPS。
5. 后端 API 不建议直接暴露到公网，可通过前台或网关转发。
6. 定期备份 MySQL 数据和上传文件，并执行恢复验证。
7. 上线前替换「联系我们」中的正式联系方式。
8. 检查算力中心的发布状态；没有真实数据时保持草稿或空内容。
9. 企业官网使用独立域名或入口路由指向 `corporate-site` 服务；不要把它的 `.html` 路径落入智能体市场 SPA fallback。
10. 仅将企业官网已发布内容的 `/open/site/v1/**` 通过前台反向代理暴露；后台裸 API、数据库、Redis 和媒体物理目录不得直接暴露。

## 10. 常见问题

### 页面无法访问

先执行 `docker compose ps`，确认对应容器已启动，再检查服务器防火墙和 `.env` 中的端口配置。

### 后台提示后端接口连接异常

检查 `backend` 容器日志，并确认 MySQL、Redis 处于 `healthy` 状态：

```bash
docker compose logs --tail=200 backend mysql redis
```

### 修改初始化 SQL 后没有生效

Docker 只会在 MySQL 数据卷首次创建时运行初始化脚本。已有环境应执行对应的 `marketplace-upgrade-*.sql`，不要通过删除数据卷重新初始化生产数据库。

### 后台菜单没有显示

重新登录后台以刷新权限和动态菜单。若仍未显示，检查对应升级脚本是否已执行。

### 登录状态过期

页面刷新后会返回登录页。重新登录后，系统会跳转到原访问地址。
