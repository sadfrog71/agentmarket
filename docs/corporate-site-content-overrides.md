# 企业官网已批准内容覆盖

本文件记录在保留原官网导出源的前提下，导入时应用的已批准内容调整。管理员后续仍通过“官网内容”后台维护发布内容；此覆盖仅保证首次导入和可复现迁移的基线一致。

## 2026-09-22：智慧水务梳理与生产运营框架图

- 从“智慧水务”下拉导航、`/water.html` 产品卡、栏目介绍及栏目索引移除“衍智云”；独立的 AI OS 栏目和其内容保持不变。
- 将 `/yanyun.html` 的“生产运营业务框架”切换为原创重绘图 `assets/parallel-brand/water-business-framework-v2.png`。
- 旧图 `water-business-framework.png` 只作为源资料归档，不再作为公开导入媒体或被发布页面引用。
- 覆盖规则位于 `agent-marketplace-backend/tools/corporate-site-import/content_overrides.py`，由导入工具在媒体链接重写前执行，并受单元测试保护。

## 2026-09-22：新增智慧排水产品线

- 在“智慧水务”下拉导航、产品卡、栏目介绍和栏目索引中，将“智慧排水”放在“衍云”之后、“衍数”之前。
- 新增固定页面 `/drainage.html`，内容依据《大禹厂站网一体化调度平台建设方案》浓缩，聚焦厂站网河协同、泵站拓扑、联动调度、运行监控和闭环处置。
- 从方案中选取并压缩三张说明图：`smart-drainage-chain.webp`、`smart-drainage-topology.webp`、`smart-drainage-monitoring.webp`。图片作为官网媒体进入 manifest、`site_media` 和页面媒体引用，不依赖前端本地写死路径。
- 页面明确保留项目边界：图示只说明产品思路，实际接入对象、数据范围、控制权限与交付阶段按项目现状确定。
