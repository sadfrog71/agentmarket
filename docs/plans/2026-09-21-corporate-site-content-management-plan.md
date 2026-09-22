---
title: 企业官网内容后台化与迁移 - Plan
type: feat
date: 2026-09-21
artifact_contract: ce-unified-plan/v1
product_contract_source: ce-plan-bootstrap
execution: code
deepened: 2026-09-21
---

## Goal Capsule

在不改变已定版官网视觉、页面结构、导航信息架构和交互的前提下，把官网全部内容迁入智能体市场现有的 RuoYi 后台。管理员在“企业官网”菜单中可直接保存草稿、发布或下架；已发布内容经公开接口即时供官网读取，不再需要同时修改静态 HTML。

交付对象是现有 Codeup 项目中的独立官网域：它复用现有账号、权限、上传、审计、容器和 Java/Vue 技术栈，但不改造或替换智能体市场的公开前台。

---

## Product Contract

### Users and outcomes

| 使用者 | 要完成的事 | 成功结果 |
| --- | --- | --- |
| 内容管理员 | 维护公司介绍、解决方案、案例、新闻、资质证书、导航页脚和联系方式 | 只在后台维护一次；发布后官网相应位置显示最新已发布内容 |
| 官网访客 | 浏览既有官网路径、栏目和新闻文章 | 页面外观与原型一致，只有公开且有效的内容可见 |
| 运维人员 | 上线、回滚和排查内容迁移 | 可核对迁移清单、数据量、媒体引用和发布状态，不需要删除生产数据卷重建数据库 |

### Requirements

- R1. 保持官网当前 8 个一级页、21 个二级页、新闻列表与详情的路径、页面结构、视觉资产和既有交互；后台只驱动内容，不提供拖拽式页面重排。
- R2. 管理端提供独立“企业官网”信息架构、`site:*` 权限和菜单；其数据、接口、字典不与 `market:*` 智能体市场域混用。
- R3. 管理员可直接将页面、新闻、案例、资质或媒体关联内容发布；保存已发布内容的新草稿不影响访客，发布时完整切换为新版本；保留草稿和下架状态以便准备与撤回，但不增加审核人、待审队列或审批流。
- R4. 官网内容按结构化对象维护：站点设置、导航页脚、固定页面与区块、新闻、案例、资质证书、媒体资产；文章正文可使用受控富文本。
- R5. 将现有官网全部内容一次性迁入，包括 53 篇新闻、现有资质与证书 PDF（当前源清单为软件著作权 65 份、专利 6 份）、图片、二维码及其可追溯的来源信息；迁入依据冻结的源导出包和带校验值的 manifest，迁入后不保留静态 HTML 作为第二个可编辑内容源。
- R6. 公开接口只返回当前已发布且未删除的数据；草稿、下架内容、未公开媒体和后台操作接口不得从匿名官网读取。
- R7. 发布前验证必填字段、富文本安全、关联媒体、内部链接及固定区块的数据结构；不完整内容不得误发布。所有已发布媒体都必须存在、校验一致且可公开读取。
- R8. 保留既有公开 URL 的可访问性，并保留页面标题、描述、分享图等 SEO 字段的维护入口；不能凭空补造新闻日期、案例指标或资质信息。
- R9. 迁移、发布与下架必须可审计；升级脚本、导入 manifest 和导入结果均有校验与执行记录，迁移可重复运行而不制造重复记录或覆盖后台后续编辑。
- R10. 切流和常规回滚以官网入口路由为边界，默认可在不恢复共享数据库的情况下切回旧站；任何市场功能或数据基线异常均为切流停止条件。

### Key Decisions

- KD1. 采用当前 Agent Marketplace Codeup 项目作为后台基础，在同一仓库新增官网域与独立官网前台，而非另建第三方官网/CMS。 (session-settled: user-directed — chosen over a separate corporate-site framework: 复用既有账号、权限、上传与运维体系，避免再维护一套后台。) Governs R2, R4, R9.
- KD2. 既有官网的视觉、页面信息架构和互动效果是固定契约；实现仅把静态内容替换为公开数据读取，不引入可自由排版的页面搭建器。 (session-settled: user-directed — chosen over redesigning the frontend: 前台界面和内容已确认。) Governs R1, R4, R8.
- KD3. 内容管理员直接发布；发布动作立即影响公开接口，不设置审核流。 (session-settled: user-directed — chosen over an editor-review-publish workflow: 管理员需直接发布。) Governs R3, R6, R7.
- KD4. 新闻、资质证书及全部历史官网内容均迁入后台，而不是只迁入未来新增内容。 (session-settled: user-directed — chosen over leaving historical content in static files: 避免后台与静态文件二次维护。) Governs R5, R9.

### Scope boundaries

In scope: 官网内容域、后台页面和权限、匿名只读接口、固定模板的数据适配、全量历史内容/媒体迁移、容器接入、回滚与验证资料。

Out of scope: 智能体市场功能改版、官网视觉重新设计、自由页面搭建器、对外留言/线索表单、审批工作流，以及对内容真实性的业务再撰写。若日后需要官网表单，另行评估是否接入现有 `BizBusinessRecord`，本次不复用它。

---

## Planning Contract

### Context and repository evidence

- 当前后台是 Java 17 / Spring Boot / MyBatis / RuoYi，管理端和前台均为 Vue 3 / Vite，MySQL 与 Redis 由 `docker-compose.yml` 编排。`agent-marketplace-web` 是智能体市场单页应用，不是官网通用壳，因此不能直接替换为官网。
- `ruoyi-market` 中的 `BizSiteContent` 是一条 `content_key` 对应标题、副标题、正文和状态的轻量内容页模型，仅能覆盖历史 COMPUTE/CONTACT 页面，无法表示首页多区块、分类新闻、案例和证书集合。
- `BizAgent` 主表与详情项、后台与公开 Controller 的分层、`@Anonymous` 公开读接口、发布状态过滤、RuoYi 上传和富文本编辑器，是官网域应沿用的实现模式。
- 现有官方网站源包含 29 个固定栏目页面、53 篇新闻详情、新闻索引，以及图片、品牌素材、二维码、65 份软件著作权和 6 份专利 PDF。新闻正文中存在内嵌样式和媒体引用，不能将整页 HTML、脚本或样式原样作为后台富文本保存。
- 已有数据库卷不会在升级时重新执行初始化 SQL；生产升级必须使用连续、幂等的升级脚本，不能以删除数据卷作为迁移手段。

### Key Technical Decisions

- KTD1. 新建 Maven 业务模块 `ruoyi-site`，在其中维护官网的 domain / mapper / service；在根 POM 的模块与依赖管理中登记，并由 `ruoyi-admin` 单向依赖。依赖方向固定为 `ruoyi-admin → ruoyi-site → ruoyi-common`，仅需组织/用户只读资料时才依赖 `ruoyi-system`；`ruoyi-site` 与 `ruoyi-market` 互不依赖，也不依赖 Controller 或前端。理由：官网与市场域有不同的对象、权限和公开契约；直接扩写 `ruoyi-market` 会让菜单、字典和业务规则交叉。Governs R2, R6.
- KTD2. 新增独立前台服务 `corporate-site-web`，沿用 Vue/Vite、Nginx 和共享后端能力；保留 `agent-marketplace-web` 原样运行。生产边缘代理为两个前台提供独立站点入口，不能让两个根路径 SPA 共用 fallback；在开始 U4 前形成入口路由表，明确官网旧路径、`/open/site/v1/**`、官网媒体、HTTPS 和健康检查的归属。理由：两个站点有不同路由、页面目的和发布节奏，共用服务会形成相互发布风险。Governs R1, R2, R8, R10.
- KTD3. 采用“固定模板 + 受约束区块 + 结构化集合”的混合模型：页面只选择既有 `template_code`；区块使用稳定 `block_key`、受限 `block_type`、标量配置与子项；新闻、案例、证书作为独立实体被页面查询或引用。理由：既可覆盖首页和详情页的复合布局，也避免将整页塞入不透明 JSON 或让编辑者打破定版界面。Governs R1, R4, R7.
- KTD4. 后端仍是唯一的媒体写入方，但官网媒体采用两阶段状态：私有暂存、哈希/MIME/来源校验、提升为公开副本、被已发布版本引用。官网前台只使用由 `/open/site/v1/media/{public_id}` 提供的稳定媒体地址；该地址在读取时核验媒体仍被当前发布修订引用，绝不暴露内部 `/profile/**` 路径。暂存或失去已发布引用的媒体不可由猜测 URL 读取；现有上传卷可继续承载物理副本，但不能作为草稿文件的直接映射。超出通用上传白名单或大小限制的历史文件由官网专用流式迁移通道处理，不全局放宽通用上传接口。理由：不重复建设文件服务，同时避免草稿、下架或上传失败文件绕过公开内容规则。Governs R5, R6, R7, R9.
- KTD5. 发布状态与内容修订分离：页面（连同区块/子项）、文章、案例、资质和站点壳都有可编辑草稿修订与不可变已发布修订，根对象保存当前 `published_revision_id`。同一管理员直接发布时，在一个事务内复核依赖、写审计并原子切换该指针；下架只撤销公开指针，不删除历史。匿名查询只读当前已发布修订。理由：满足直接发布，同时避免编辑已上线内容时访客看到半成品或内容消失。Governs R3, R6, R9.
- KTD6. 迁移采用“冻结源导出包与 manifest—私有媒体暂存映射—幂等导入—三类闭包核验”的一次性流水线。每个源对象记录规范化源键、哈希、目标 ID/修订、处理结果；同一 manifest 重跑跳过，哈希冲突或后台已人工修改时阻断并生成差异报告，绝不覆盖。源 HTML 仅作为读取输入，迁移后后台数据库和媒体库成为唯一可编辑来源。理由：全量迁入时既避免人工漏项，也避免静态页和后台长期分叉。Governs R5, R8, R9.
- KTD7. 官网公开接口从第一天采用版本化只读契约 `/open/site/v1/**`，只返回显式 public DTO；不直接返回 Entity、审计字段、草稿字段、后台权限数据或内部存储路径。筛选、排序、分页上限与路由均白名单化，服务层统一按当前已发布修订、软删和引用有效性过滤；在具备按发布版本失效策略前，公开 HTML/API 使用 `no-store` 或必须重新验证的缓存策略。理由：官网与独立前台之间需要稳定、无敏感信息泄露的长期契约。Governs R6, R7, R8.

### High-level technical design

```text
冻结官网导出包（只读）
        │  manifest、源键、哈希与分类
        ▼
导入账本 ──► 私有媒体暂存 ── 校验/提升 ──► 公开媒体副本
        │                                        │
        └── 幂等写入 ──► ruoyi-site 草稿/发布修订 ┘
                                  │
后台“企业官网” ── 直接发布、原子切换指针 ──┤
                                  ▼
                  /open/site/v1/*（public DTO）
                                  │
                                  ▼
边缘路由表 ──► corporate-site-web 固定模板
```

公开数据按以下读取边界设计：站点壳（导航、页脚、联系方式）、按 `route_path`/`page_code` 查询的页面及固定区块、新闻列表与详情、案例列表与详情、资质证书列表。前台以页面代码选择既有模板与互动组件；数据不能携带自定义 CSS、脚本或任意组件名称。保存草稿和媒体上传均不会改变这一公开读模型，只有完整修订和已提升的媒体共同发布后才在下一次匿名读取中可见。

### Content model and source mapping

| 后台对象 | 主要维护字段 | 对应官网内容 |
| --- | --- | --- |
| Site setting / navigation revision | Logo、公司名称、联系方式、二维码、页脚、导航文案/目标/排序、发布修订 | 全站 Header、Footer、联系区、外链 |
| Page / page block / block item revision | `page_code`、保留路径、模板、SEO、Hero、标题文案、卡片/标签/锚点/关联内容、发布修订 | 8 个一级页、21 个二级页、首页多区块 |
| Article / category revision | 分类、标题、来源、日期（可为空）、摘要、封面、受控富文本、SEO、发布修订 | 新闻列表、3 个分类页、53 篇详情 |
| Case revision | 分类、标签、客户/区域文字、概述、图片、详情、来源说明、发布修订 | 客户案例与首页精选案例 |
| Credential revision | 类型、名称、发证单位、编号/日期（可为空）、PDF 媒体、排序、发布修订 | 资质、65 项软著、6 项专利及后续证书 |
| Media / import run / import item | 暂存/公开状态、文件名、MIME、大小、源/目标校验值、alt、来源、处理结果、引用 | 页面图、文章图、证书 PDF、Logo、二维码和分享图 |

迁移时以官网 `page-map.json`、新闻数据索引、新闻详情和资质 PDF 清单生成带 SHA-256 的不可变导入 manifest。每条数据保留 `legacy_path` 或源文件标识；日期、指标、来源缺失时保留为空或原文，不以推断值填补。预检必须列出源目录中的每一个二进制，并标记为导入、派生、归档或经批准排除；不支持格式、超限文件或来源待补齐项不允许被静默跳过。

### System-wide impact

| 影响面 | 契约与保护措施 |
| --- | --- |
| Maven 与业务边界 | `ruoyi-admin → ruoyi-site → ruoyi-common` 单向依赖；`ruoyi-site` 与 `ruoyi-market` 不共享业务实体、Mapper、字典或权限常量，市场可独立构建。 |
| 公共入口 | 企业官网、市场前台和管理端通过边缘路由表隔离；任一路径均不能落入另一 SPA 的 fallback，也不向公网暴露后端裸端口。 |
| 匿名读取 | `/open/site/v1/**` 仅返回 public DTO、当前发布修订与可用引用；草稿、下架、后台字段和暂存媒体无匿名旁路。 |
| 文件与缓存 | 暂存媒体不可公开；公开副本只读挂载给前台；HTML、API、媒体的缓存时限和发布可见时限 T 在切流前确定并以发布/下架 canary 验证。 |
| 生产数据库 | 首次切流仅允许新增表、索引、字典、菜单和模块依赖；升级必须留有脚本 ID、哈希、执行者和结果，且不改写市场业务数据。 |
| 市场无回归 | 切流前后记录市场管理员登录、市场公开页、现有匿名 API、上传访问和服务健康基线；任一关键信号失败即停止或回切。 |

### Risks and dependencies

| 风险或依赖 | 处理原则 | 停止/回退信号 |
| --- | --- | --- |
| 旧数据库不自动执行新 SQL | 使用受控一次性升级和升级登记；先在生产备份恢复出的隔离副本演练 | 脚本编号/哈希冲突、官网对象缺失或市场基线变化 |
| 大 PDF、WebP/SVG 等超出现有通用上传能力 | 预检所有文件 MIME/大小/哈希；只为官网迁移通道增加受限白名单和流式能力 | 有未分类、超限、哈希不符或不可达必需媒体 |
| 源站在迁入期间仍被编辑 | 切流前冻结源包；更新须生成新 manifest 和完整差异核验 | manifest 与导入记录不一致或存在未决冲突 |
| 新旧站并存或缓存陈旧 | 先预装新服务，后以入口路由切流；发布/下架以 T 内 API 与浏览器 canary 验证 | 官网/API/代表媒体探针连续三次失败或 required 路径失败 |
| 共享 MySQL 恢复误伤市场写入 | 默认回滚只回切官网路由、固定/下线官网服务，保留官网表与审计；整库恢复仅由 DBA 按 RPO/PITR 决策 | 任何关键市场 smoke 失败立即回切，不以整库恢复作为常规动作 |

### Resolved during planning

- 内容粒度不是“一页一个富文本”。首页、概览页与详情页的固定区块独立维护；新闻、案例、证书作为可复用集合维护，首页引用它们而不复制文字。
- “全部迁入”也包括当前嵌入正文的图片、二维码和附件；引用不存在、仍在暂存、未通过哈希校验或不能公开读取的媒体时，发布校验失败。
- 官网前台与智能体市场共享后端与上传卷，但部署为两个独立 Web 服务与路由入口。
- 管理员的“直接发布”是发布草稿修订的原子切换，不等于把正在编辑的内容直接覆盖到线上；误发布通过切回前一发布修订处理，不依赖审批流程。

### Deferred to implementation

- 依据实际生产域名决定 Nginx 的最终 server name、证书和旧路径重写配置；其规则必须满足 R8 的路由契约。U4 开始前须由运维确认一张入口路由表，不能在切流当天临时决定。
- 依据既有迁移脚本编号确定官网升级 SQL 的连续编号；不得猜定或覆盖现有编号。
- 依据实际素材版权/来源资料补齐每个媒体的来源与使用说明；缺失时导入记录标为待补充，不伪造授权信息。

---

## Implementation Units

### U7. 封存源输入并完成生产升级前置检查

**Requirements:** R5, R7, R9, R10. **Depends on:** none. **Must complete before:** U1, U4, U5, U6.

**Files:** add `agent-marketplace-backend/tools/corporate-site-import/`; add `agent-marketplace-backend/tools/corporate-site-import/fixtures/`; add `docs/corporate-site-content-inventory.md`; add `docs/corporate-site-migration-runbook.md`; update `DEPLOYMENT.md`.

**Approach:** 指定一份获授权的官网导出包，生成源根哈希和不可变 manifest，逐项记录页面、新闻、案例、证书、媒体、MIME、字节数、SHA-256、引用数、来源分类和 `legacy_path`。manifest 中每个二进制必须被标为导入、受控派生、归档或批准排除。针对目标生产库，先记录已应用升级、拟升级脚本编号/哈希、官网表/索引预期、市场表结构与行数基线；在生产备份恢复出的隔离副本演练同一升级、导入和验证顺序。上线窗口前冻结源包，后续静态源变更只能进入新的 delta manifest。

**Test scenarios:** 全部源文件均有分类；WebP/SVG、超过通用上传限制的 PDF 和其他非标准文件被明确识别；manifest 记录 29 个固定栏目、53 篇新闻、71 份当前证书/专利基线及全部媒体；隔离副本上的升级账本、市场基线和源哈希与预检一致；编号冲突、哈希冲突、未分类文件或市场基线变化触发停止。

**Verification outcome:** 实施从一个可复现、可审计的源快照开始，生产升级不会被误当作容器首次初始化。

### U1. 建立官网域、数据库与权限骨架

**Requirements:** R2, R3, R4, R6, R9. **Depends on:** U7.

**Files:** add `agent-marketplace-backend/ruoyi-site/`; update `agent-marketplace-backend/pom.xml`, `agent-marketplace-backend/ruoyi-admin/pom.xml`, `agent-marketplace-backend/sql/marketplace.sql`; add the next continuous `agent-marketplace-backend/sql/marketplace-upgrade-*-site.sql`; update `README.md` and `DEPLOYMENT.md`.

**Approach:** 以 RuoYi / `ruoyi-market` 的实体、Mapper XML、软删、审计字段和服务事务模式创建 `ruoyi-site`，并落实 KTD1 的单向 Maven 依赖。建立站点设置、导航、页面、区块、区块子项、新闻分类/文章、案例、资质证书、媒体、媒体引用、发布修订、导入运行/条目和升级登记关系；根对象与草稿/已发布修订分离，发布指针具有业务唯一约束。加入 `site_publish_status` 字典、企业官网菜单及最小 `site:*` 权限。升级仅以受控一次性任务执行，登记脚本 ID、SHA-256、执行者、时间和结果，并使用全局锁阻止并发；`marketplace.sql` 只服务全新库，首次切流升级只允许新增对象。

**Test scenarios:** 新库初始化包含官网表、字典和菜单；已有库升级不删除、重命名或收缩既有表/市场数据；升级登记账本对同一脚本仅有一次成功记录且校验值一致；重复执行升级和导入准备写入不产生重复业务唯一键；有 `site:*` 权限的账号可访问官网菜单、无权限账号不能访问；Maven 依赖树不存在 `site ↔ market` 循环，市场模块仍可独立构建。

**Verification outcome:** 数据库结构、Maven 模块依赖、后台动态菜单和升级说明均可独立核对，智能体市场权限与菜单不受影响。

### U2. 实现受控内容管理、发布校验与匿名读取接口

**Requirements:** R2, R3, R4, R6, R7, R8, R9. **Depends on:** U1.

**Files:** add `agent-marketplace-backend/ruoyi-admin/src/main/java/com/ruoyi/web/controller/site/`; add/update `agent-marketplace-backend/ruoyi-admin/src/main/java/com/ruoyi/web/controller/open/`; add `agent-marketplace-backend/ruoyi-site/src/main/java/com/ruoyi/site/`; add `agent-marketplace-backend/ruoyi-site/src/main/resources/mapper/site/`; add tests below `agent-marketplace-backend/ruoyi-site/src/test/` and `agent-marketplace-backend/ruoyi-admin/src/test/`.

**Approach:** 管理接口按官网对象分开 CRUD、排序、关联和发布；编辑已发布对象时创建/更新其草稿修订，页面区块仅接受模板允许的类型和字段。发布前在一个事务中锁定根对象、校验修订的必填/枚举/内部链接/关联项/媒体闭包与富文本 allowlist、写审计并原子切换 `published_revision_id`；下架仅撤销公开指针。公开接口采用 `/open/site/v1/**`，聚合站点壳、路由页面、新闻、案例、证书，但仅返回白名单 public DTO，并在服务层统一限定当前已发布修订、未删除和有效引用。富文本只保存清洗后的文章正文，不保存完整静态页的 `<html>`、脚本或页面样式；公开 API 在发布版本失效策略落地前要求重新验证缓存。

**Test scenarios:** 对已发布内容保存草稿时，匿名接口响应哈希保持不变；发布后下一次匿名读取完整切换到新哈希且不混合新旧区块；草稿和下架页面/文章/证书在匿名接口均不可见；包含脚本、危险链接、暂存媒体或失效关联的正文不能发布；区块字段与模板不匹配时拒绝保存；文章日期缺失时保持为空；下架后公开列表、详情和导航引用同步消失而后台审计仍存在；DTO 字段快照不含审计/权限/草稿字段，非法筛选或超大分页被拒绝。

**Verification outcome:** 同一管理员可以直接发布完整修订，且每一个匿名数据入口都证明不会泄露草稿、下架内容、暂存媒体或后台字段。

### U3. 增加“企业官网”后台管理界面与媒体引用能力

**Requirements:** R2, R3, R4, R5, R6, R7, R8. **Depends on:** U1, U2, U7.

**Files:** add `agent-marketplace-admin/src/api/site/`; add `agent-marketplace-admin/src/views/site/`; update dynamic menu-dependent route integration only where the existing admin shell requires it; add tests or validation fixtures under `agent-marketplace-admin/src/` following the repository's available test conventions.

**Approach:** 复用已有列表、分页、Quill 编辑和预览能力，按内容对象提供页面/区块、新闻、案例、资质、媒体、导航和站点设置的独立维护界面。页面编辑器显示模板允许字段和区块顺序，不提供任意 HTML 页面编辑。媒体选择器展示 alt、来源、文件类型、引用处、暂存/公开状态和预览；文章富文本内插图使用同一媒体登记流程。官网媒体上传先进入私有暂存区，再由校验和发布流程提升，不能把通用 `/common/upload` 返回的直接公开 URL 当作草稿媒体地址。对超出当前通用限制的历史文件提供仅官网迁移可用的受限 MIME 白名单和流式传输，不修改所有业务的全局上传限制。发布和下架由当前编辑者直接操作并提示其实际影响的公开页面与版本。

**Test scenarios:** 管理员能新增、编辑、排序并直接发布每类内容；已发布内容编辑后产生草稿修订而不改线上数据；媒体从暂存提升后才生成官网可用 URL；知道草稿、下架或失去引用媒体的旧 URL 时得到不可访问结果；删除被引用媒体时得到阻止或明确的替换提示；页面区块不能选择不兼容的内容类型；证书 PDF 可预览与下载；超限/不支持格式按官网专用策略被阻止或显式处理，不静默缺件。

**Verification outcome:** 运营人员无需编辑 HTML、手填文件 URL 或在两套位置复制内容，即可完成全站内容维护。

### U4. 将定版官网接入独立动态前台

**Requirements:** R1, R4, R6, R8, R10. **Depends on:** U2, U3, U7.

**Files:** add `corporate-site-web/`; update `docker-compose.yml`; update deployment Nginx configuration and `DEPLOYMENT.md`; retain `agent-marketplace-web/` unchanged except for shared, already-proven upload URL utility extraction when a no-behavior-change reuse is demonstrably safe.

**Approach:** 在 U4 开始前固化官网/市场/后台的边缘入口路由表，明确生产域名归属、旧路径、API 代理、媒体路径、HTTPS、健康检查与 SPA fallback；官网路径不得进入市场前台，反之亦然。以当前官网源的页面、样式、图片比例、导航展开、移动端菜单、新闻筛选、案例筛选和图片预览为视觉/互动基线，将静态文案与列表替换为公开官网 API 的数据适配层。用 `page_code` 与 `route_path` 路由到固定模板，保留一级、二级与新闻详情旧路径；对未发布或不存在的公开内容显示标准 404/下架结果，不回退到静态旧文案。页面 title、description、canonical、分享图从受控 SEO 字段设置，旧路径兼容在 Web 路由和 Nginx 配置中共同验证；前台仅可只读访问已提升的官网媒体，不直接写入上传卷或访问后端裸端口。

**Test scenarios:** 29 个固定栏目路由、新闻三分类页和 53 个新闻历史路径均可直接打开且不落入市场 SPA fallback；导航、Tab、案例筛选、移动端菜单和图片预览的既有行为不回归；草稿路径返回不可见结果；已发布图片、二维码和 PDF 在目标域名下加载成功而暂存/下架媒体不可访问；桌面和移动浏览器对比中页面层级、文本位置和交互状态符合定版源；浏览器无需直接访问后端端口。

**Verification outcome:** 官网不再依赖可编辑静态 HTML；智能体市场前台仍由自己的服务正常访问，官网与市场互不覆盖。

### U5. 形成可审计的全量导入与内容核验

**Requirements:** R5, R6, R7, R8, R9. **Depends on:** U1, U2, U3, U7.

**Files:** add `agent-marketplace-backend/tools/corporate-site-import/`; add `agent-marketplace-backend/tools/corporate-site-import/fixtures/`; add `docs/corporate-site-content-inventory.md`; add `docs/corporate-site-migration-runbook.md`; update `DEPLOYMENT.md`.

**Approach:** 从 U7 的经确认官网导出源构建不可变 manifest，记录每页、区块、新闻、案例、资质、媒体、源路径、规范化源键、源/目标哈希、目标 ID/修订和处理结果。先以私有暂存流程上传媒体并获得校验一致的提升映射，再以稳定源键导入结构化记录和受控文章正文；每次导入写入运行账本和条目账本。相同 manifest 和哈希重跑必须跳过；源哈希变化、路径冲突、媒体校验不符或后台已人工修改则阻断，不自动覆盖或删除。导入后生成“已迁入、缺失、未引用、待补来源、批准排除、失败原因”报告，并执行来源闭包、引用闭包和公开闭包检查。导入期间禁止直接人工改静态源；正式切流后后台成为唯一维护入口。保留原官网导出包为只读回滚参考，不作为长期发布数据源。

**Test scenarios:** 重复导入同一 manifest 后各业务对象数量、修订和唯一键不变；53 篇新闻、71 份当前基线证书/专利 PDF、29 个固定栏目页面与所有 manifest 媒体均有对应记录；每类对象满足“manifest 数 = 成功数 + 批准排除数 + 显式失败数”，且未分类数为零；不存在未解析/暂存媒体、断裂内部链接、路径重复或错误分类；所有页面区块、文章富文本、SEO 图、导航、PDF 的媒体/内部路由均解析为可用已发布目标；匿名端点不泄露草稿/下架/后台字段；哈希冲突与人工修改产生可定位的阻断差异而非覆盖。

**Verification outcome:** 可提供完整迁移清单、哈希、导入运行记录和三类闭包报告，证明“全部内容已迁入”而非仅构建或演示通过。

### U8. 完成动态官网的全量浏览器与路径回归

**Requirements:** R1, R5, R6, R8. **Depends on:** U4, U5.

**Files:** update `docs/corporate-site-content-inventory.md`, `docs/corporate-site-migration-runbook.md`, and the browser-test or probe fixtures introduced under `corporate-site-web/`.

**Approach:** 使用已导入的完整测试数据执行固定路径、新闻详情、媒体、导航和交互回归；将 manifest 中的每条公开 `legacy_path` 映射为预期 200、精确重定向或经批准的非公开项。以真实浏览器分别检查桌面和移动端；结果同内容清单、路由映射、版本号关联，作为切流门槛而非仅演示资料。

**Test scenarios:** 82 条当前公开页面/新闻基线路径和全部 manifest 路径得到预期结果；未知路径不被错误导向首页；代表性图、二维码、PDF、分享图与富文本图片均按正确 MIME/哈希加载；title、description、canonical 与分享图按源基线抽查；导航、Tab、筛选、模态预览和移动菜单可复跑验证。

**Verification outcome:** 动态前台在接入全量实际内容后仍保持定版页面契约，且没有依赖静态 HTML 兜底。

### U6. 完成切流、回滚与上线验证

**Requirements:** R1, R5, R6, R8, R9, R10. **Depends on:** U4, U5, U8.

**Files:** update `docker-compose.yml`, deployment Nginx configuration, `DEPLOYMENT.md`, `docs/corporate-site-migration-runbook.md`, and `docs/corporate-site-content-inventory.md` with final verification evidence.

**Approach:** 按“预检 → 隔离演练 → 生产预装 → Go/入口切流 → 24 小时观察”设置闸门：DBA/运维先确认备份可恢复、升级登记/哈希、上传卷容量/权限、旧路由回切和市场基线；开发、QA、内容负责人在隔离副本完成同版本升级、导入和闭包核验；生产预装只完成新服务健康与迁入差异归零，不提前把正式流量导入新站；内容负责人确认 manifest，发布负责人确认官网/市场 preflight 后才切换入口。发布/下架可见时限 T、HTML/API/媒体缓存策略、跨域/API/HTTPS/分享图检查均在此阶段固定。默认回滚为停用官网新入口、恢复旧静态站路由、固定或下线官网服务，保留新增官网表、媒体和审计数据；整库恢复仅由 DBA 在明确 RPO/PITR 与市场写入影响后决定。

**Test scenarios:** 升级账本恰有一次成功记录且官网表/唯一键/索引存在、市场结构与基线行数无变化；升级后市场端、后台和官网服务均可启动；旧路径、新闻详情和证书附件在生产同源/跨域配置下可访问；管理员发布/下架 freshness canary 在 T 内由匿名 API 和真实浏览器同时体现；回滚演练能恢复旧路由且不删除迁入数据，市场健康检查持续通过；无权限访问后台接口、草稿公开 URL 和暂存媒体 URL 均被拒绝；官网首页/API/代表媒体连续三次探针失败、任一 required 路径/媒体失败或任一市场 smoke 失败时停止或回切。

**Verification outcome:** 上线与流量级回滚可由运维复现，且内容迁移、公开隔离、页面兼容、媒体可用性和市场无回归均有记录；观察期内未触发回滚条件才可标记验收完成。

---

## Verification Contract

### Automated and structural checks

- 后端针对发布修订原子切换、权限、输入校验、富文本清洗、媒体状态、路由查询、公开 DTO、缓存头和幂等迁移设置单元/集成测试；管理端对数据映射和表单限制设置可重复验证。
- 后端、管理端、智能体市场前端和新官网前端均完成各自构建；构建通过不视为内容迁移或视觉验收完成。
- 数据库检查包含表/索引/外键或业务唯一键、菜单与权限、升级登记的脚本 ID/哈希/执行结果、升级幂等性及市场表无意外变更。

### Content parity checks

- 来源闭包：迁移 manifest 的页面、新闻、案例、资质和媒体计数与后台记录一一比对；以当前源为基线，至少核对 30 个固定栏目页面、53 篇新闻、71 份资质/软著/专利 PDF，且每项均有源键、源/目标哈希、目标 ID/修订与处理结果。
- 引用闭包：每个 `legacy_path` 可解析到新路由或明确的兼容规则；所有页面区块、正文富文本、SEO 图、导航、封面、二维码和证书附件均解析为可用已发布目标，重复路径、断裂内部链接、缺失文件和不可达媒体均为零。
- 公开闭包：以匿名身份枚举各公开入口；草稿、下架、后台字段、暂存媒体和失去引用媒体的泄露数均为零。空日期、空指标和来源缺失保持真实空值。

### Browser and operational checks

- 在真实桌面与移动浏览器检查首页、各一级/二级页面、三类新闻列表、新闻详情、案例与资质页；验证导航、Tab、筛选、图片预览和移动菜单，以及所有旧路径不会落入市场 SPA fallback。
- 发布、下架、草稿三种状态分别验证匿名公开 API 与浏览器页面；以 T=0、约 2 小时和 24 小时的受控 canary 确认发布后生效、下架后不泄露、草稿从不出现。
- 首次生产切流前验证备份恢复演练、升级脚本登记、上传卷容量/只读可访问性、日志、旧路径、旧站构件和回滚开关；切流后连续 24 小时检查官网/API/媒体探针、4xx/5xx、前端资源错误、服务重启、数据库连接和市场同类健康信号。浏览器运行结果才可作为视觉/互动验收证据。

---

## Definition of Done

- Codeup 项目中存在独立 `ruoyi-site`、企业官网后台菜单、受控 `/open/site/v1/**` 接口与 `corporate-site-web`，Maven 无网站/市场循环依赖，智能体市场的现有功能和前台不受影响。
- 管理员可在后台直接维护并发布全部官网内容，不需要编辑静态 HTML；保存草稿不改变线上修订，发布/下架原子生效，页面模板不能被任意排版内容破坏。
- 全量历史官网资料已由可重跑、带哈希的 manifest 导入，包含 53 篇新闻和当前清单中的 71 份证书/专利 PDF；来源、引用和公开三类闭包报告均为零差异。
- 保留当前官网栏目与新闻 URL、主要交互和 SEO 字段；所有匿名接口与媒体地址均隔离草稿、下架内容、未发布媒体和后台字段。
- 生产升级不依赖删除数据卷，升级登记与隔离副本演练通过；部署、流量级回滚、迁移、市场基线和浏览器核验都有可执行文档与已记录的结果，且首 24 小时观察期未触发回滚条件。

### Sources and references

- `README.md` — 当前三端架构、服务边界和部署组成。
- `agent-marketplace-backend/ruoyi-market/src/main/java/com/ruoyi/market/domain/BizSiteContent.java` 与 `agent-marketplace-backend/sql/marketplace-upgrade-003-site-content.sql` — 现有轻量内容模型的能力边界。
- `agent-marketplace-backend/ruoyi-market/src/main/java/com/ruoyi/market/service/impl/BizAgentServiceImpl.java`、`agent-marketplace-backend/ruoyi-admin/src/main/java/com/ruoyi/web/controller/open/OpenAgentController.java` — 主从内容、发布状态和公开读取模式。
- `agent-marketplace-backend/ruoyi-admin/src/main/java/com/ruoyi/web/controller/common/CommonController.java`、`agent-marketplace-web/src/utils/assetUrl.js` — 现有上传文件和前台 URL 规范，以及官网媒体不能直接沿用的边界。
- `agent-marketplace-backend/sql/marketplace.sql`、`DEPLOYMENT.md` — 初始化/升级与生产数据卷约束。
- 获授权的官网导出包（其页面映射、新闻索引、新闻详情、证书 PDF 和品牌资产）— 迁移基线；执行时在 manifest 中登记输入哈希，不把任何本机目录固化为生产依赖。
