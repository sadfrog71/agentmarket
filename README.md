# 华衍智能体市场

一期采用「前台展示 + 管理员发布」模式。交易、智能体上架和 FDE 实施服务均在线下确认，由管理员在后台维护展示内容和商务登记。

## 一期功能

- 前台：智能体首页、智能体广场、详情页、多智能体场景、FDE 服务、算力中心空数据页。
- 后台：智能体内容管理、详情结构块管理、发布状态管理、商务登记与跟进。
- 商务登记：支持客户咨询、智能体上架、实施服务三类记录，以及待处理、跟进中、已确认、已关闭四种状态。
- 数据：MySQL 保存业务数据，Redis 保存登录与缓存数据，上传目录使用独立 Docker 数据卷。

专题页采用固定 Vue 组件，保证复杂排版稳定；智能体列表、详情和商务登记由后台管理。后续可在现有扩展字段和关联表上增加供应商、审核、跟进记录、订单与合同模块。

## 技术结构

| 模块 | 技术 | 目录 |
| --- | --- | --- |
| 展示前台 | Vue 3、Vite、Nginx | `agent-marketplace-web` |
| 管理后台 | RuoYi Vue 3、Element Plus | `agent-marketplace-admin` |
| 后端服务 | Java 17、Spring Boot、RuoYi | `agent-marketplace-backend` |
| 基础服务 | MySQL 8.4、Redis 7.4 | `docker-compose.yml` |

## 本地启动

首次启动前复制环境变量模板，并修改数据库密码：

```bash
cp .env.example .env
docker compose up -d --build
```

默认访问地址：

- 展示前台：`http://localhost:8081`
- 管理后台：`http://localhost:8082`
- 后端 API：`http://localhost:8080`

RuoYi 初始管理员账号为 `admin`，初始密码为 `admin123`。正式上线前必须修改默认密码。新数据库首次启动时会自动导入系统表、智能体业务表、菜单、字典和示例智能体。

## 数据库脚本

- `agent-marketplace-backend/sql/ry_20260417.sql`：RuoYi 基础表与初始数据。
- `agent-marketplace-backend/sql/marketplace.sql`：智能体市场业务表、菜单、字典与示例数据。
- `agent-marketplace-backend/sql/marketplace-upgrade-001-business.sql`：已有环境增加商务登记菜单与字典。
- `agent-marketplace-backend/sql/marketplace-upgrade-002-charset.sql`：修复早期初始化产生的系统中文乱码。

升级脚本按编号顺序执行。全新环境只需使用 Docker Compose 自动初始化，无需重复执行升级脚本。

## 上线检查

1. 修改 `.env` 中的数据库密码和对外端口。
2. 修改管理员初始密码，按需要创建内容管理员账号。
3. 配置正式域名、HTTPS 证书和服务器防火墙，仅开放前台与后台入口。
4. 在后台核对智能体名称、价格说明、实施周期、发布状态和详情结构块。
5. 替换「联系我们」弹窗中的正式业务联系方式。
6. 备份 MySQL 数据卷和上传文件卷，并验证恢复流程。
7. 验证前台公开接口、后台登录、智能体新增修改和商务登记修改。

## 当前验收结果

- 前台与后台生产构建通过。
- Docker 容器可正常启动，前台、后台、后端、MySQL 和 Redis 可互通。
- 智能体公开列表与详情接口通过。
- 商务登记新增、修改、查询通过。
- 后台真实登录、菜单加载和列表显示通过。
- 多智能体场景、FDE 服务和后台页面已完成桌面端视觉检查。
