# 企业官网已批准内容覆盖

本文件记录在保留原官网导出源的前提下，导入时应用的已批准内容调整。管理员后续仍通过“官网内容”后台维护发布内容；此覆盖仅保证首次导入和可复现迁移的基线一致。

## 2026-09-22：智慧水务梳理与生产运营框架图

- 从“智慧水务”下拉导航、`/water.html` 产品卡、栏目介绍及栏目索引移除“衍智云”；独立的 AI OS 栏目和其内容保持不变。
- 将 `/yanyun.html` 的“生产运营业务框架”切换为原创重绘图 `assets/parallel-brand/water-business-framework-v2.png`。
- 旧图 `water-business-framework.png` 只作为源资料归档，不再作为公开导入媒体或被发布页面引用。
- 覆盖规则位于 `agent-marketplace-backend/tools/corporate-site-import/content_overrides.py`，由导入工具在媒体链接重写前执行，并受单元测试保护。
