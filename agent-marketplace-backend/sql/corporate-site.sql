-- 企业官网内容域：仅供全新数据库初始化。
-- 已有环境必须使用 marketplace-upgrade-006-site-core.sql，并先运行迁移预检。
set names utf8mb4;

create table site_schema_migration (
  script_id            varchar(100) not null comment '升级脚本稳定标识',
  script_sha256        char(64) not null comment '预检确认的脚本SHA-256',
  release_version      varchar(64) not null comment '发布版本或构建标识',
  executor             varchar(64) not null comment '执行人或自动化任务',
  execution_status     varchar(20) not null comment 'APPLIED/FAILED',
  applied_at           datetime default current_timestamp comment '登记时间',
  evidence_json        json default null comment '预检报告、表结构核验等证据',
  error_message        text default null comment '失败原因',
  primary key (script_id),
  key idx_site_schema_migration_status_time (execution_status, applied_at)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_ai_ci comment='企业官网数据库升级登记册';

create table site_import_run (
  import_run_id        char(36) not null comment '导入批次UUID',
  manifest_id          varchar(100) not null comment '来源清单标识',
  source_snapshot_hash char(64) not null comment '来源快照哈希',
  manifest_sha256      char(64) not null comment '清单文件哈希',
  run_status           varchar(20) not null comment 'PREPARED/RUNNING/COMPLETED/FAILED/ABORTED',
  started_by           varchar(64) not null comment '执行人',
  started_at           datetime default current_timestamp comment '开始时间',
  completed_at         datetime default null comment '结束时间',
  total_items          int default 0 comment '清单总项数',
  imported_items       int default 0 comment '成功数',
  skipped_items        int default 0 comment '幂等跳过数',
  excluded_items       int default 0 comment '批准排除数',
  conflict_items       int default 0 comment '冲突数',
  failed_items         int default 0 comment '失败数',
  report_json          json default null comment '导入报告与闭包核验结果',
  remark               varchar(500) default null comment '备注',
  primary key (import_run_id),
  unique key uk_site_import_run_manifest (manifest_id, manifest_sha256),
  key idx_site_import_run_status_time (run_status, started_at)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_ai_ci comment='企业官网内容导入批次';

create table site_media (
  media_id             bigint not null auto_increment comment '媒体ID',
  public_id            char(36) not null comment '公开媒体标识，不暴露物理路径',
  media_kind           varchar(32) not null comment 'IMAGE/DOCUMENT/VIDEO/OTHER',
  original_filename    varchar(255) not null comment '原始文件名',
  source_legacy_key    varchar(500) default null comment '稳定来源键',
  source_sha256        char(64) default null comment '来源字节哈希',
  content_sha256       char(64) not null comment '落盘字节哈希',
  mime_type            varchar(128) not null comment '检测到的MIME',
  file_size            bigint not null comment '文件字节数',
  media_state          varchar(20) not null comment 'STAGED/AVAILABLE/RETIRED',
  staging_path         varchar(500) default null comment '私有暂存路径，禁止公开映射',
  storage_key          varchar(500) default null comment '受控对象存储键，可按哈希复用',
  public_path          varchar(500) default null comment '可用后的内部公开路径',
  import_run_id        char(36) default null comment '来源导入批次',
  source_license_note  varchar(500) default null comment '来源或版权说明',
  adopted_at           datetime default null comment '提升为可用时间',
  del_flag             char(1) default '0' comment '删除标志（0存在 2删除）',
  create_by            varchar(64) default '' comment '创建者',
  create_time          datetime default current_timestamp comment '创建时间',
  update_by            varchar(64) default '' comment '更新者',
  update_time          datetime default null on update current_timestamp comment '更新时间',
  remark               varchar(500) default null comment '备注',
  primary key (media_id),
  unique key uk_site_media_public_id (public_id),
  unique key uk_site_media_source_key (source_legacy_key),
  key idx_site_media_state_hash (media_state, content_sha256, del_flag),
  key idx_site_media_import_run (import_run_id)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_ai_ci comment='企业官网受控媒体库';

create table site_page (
  page_id              bigint not null auto_increment comment '页面ID',
  page_code            varchar(100) not null comment '稳定页面编码',
  legacy_key           varchar(500) default null comment '静态站来源稳定键',
  route_path           varchar(255) not null comment '公开路由路径',
  template_code        varchar(100) not null comment '既定前台模板编码',
  draft_revision_id    bigint default null comment '当前可编辑草稿修订',
  published_revision_id bigint default null comment '当前公开修订',
  previous_published_revision_id bigint default null comment '上一公开修订，用于版本回退',
  del_flag             char(1) default '0' comment '删除标志（0存在 2删除）',
  create_by            varchar(64) default '' comment '创建者',
  create_time          datetime default current_timestamp comment '创建时间',
  update_by            varchar(64) default '' comment '更新者',
  update_time          datetime default null on update current_timestamp comment '更新时间',
  remark               varchar(500) default null comment '备注',
  primary key (page_id),
  unique key uk_site_page_code (page_code),
  unique key uk_site_page_legacy_key (legacy_key),
  unique key uk_site_page_route_path (route_path),
  key idx_site_page_published (published_revision_id, del_flag)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_ai_ci comment='企业官网页面根对象';

create table site_page_revision (
  revision_id          bigint not null auto_increment comment '页面修订ID',
  page_id              bigint not null comment '页面ID',
  revision_no          int not null comment '页面内连续修订号',
  revision_state       varchar(20) not null comment 'DRAFT/PUBLISHED/SUPERSEDED/ARCHIVED',
  title                varchar(255) not null comment '页面标题',
  subtitle             varchar(1000) default '' comment '页面副标题',
  body_html            longtext default null comment '受控页面主体HTML，样式与脚本由固定前台模板承载',
  seo_json             json default null comment 'SEO标题、摘要、封面等',
  page_data_json       json default null comment '模板级配置数据',
  content_hash         char(64) default null comment '发布内容聚合哈希',
  source_import_run_id char(36) default null comment '来源导入批次',
  published_at         datetime default null comment '发布时间',
  published_by         varchar(64) default null comment '发布人',
  create_by            varchar(64) default '' comment '创建者',
  create_time          datetime default current_timestamp comment '创建时间',
  update_by            varchar(64) default '' comment '更新者',
  update_time          datetime default null on update current_timestamp comment '更新时间',
  remark               varchar(500) default null comment '备注',
  primary key (revision_id),
  unique key uk_site_page_revision_no (page_id, revision_no),
  key idx_site_page_revision_state (page_id, revision_state),
  key idx_site_page_revision_import (source_import_run_id)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_ai_ci comment='企业官网页面修订';

create table site_page_block (
  block_id             bigint not null auto_increment comment '页面区块ID',
  revision_id          bigint not null comment '页面修订ID',
  block_key            varchar(100) not null comment '模板内稳定区块编码',
  block_type           varchar(100) not null comment '既定区块类型',
  sort_no              int default 0 comment '显示顺序',
  visible_flag         char(1) default '1' comment '是否展示（1是 0否）',
  data_json            json default null comment '区块配置数据',
  primary key (block_id),
  unique key uk_site_page_block (revision_id, block_key),
  key idx_site_page_block_sort (revision_id, sort_no)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_ai_ci comment='企业官网页面修订区块';

create table site_page_block_item (
  item_id              bigint not null auto_increment comment '区块条目ID',
  block_id             bigint not null comment '页面区块ID',
  item_key             varchar(100) not null comment '区块内稳定条目编码',
  sort_no              int default 0 comment '显示顺序',
  visible_flag         char(1) default '1' comment '是否展示（1是 0否）',
  data_json            json default null comment '条目配置数据',
  primary key (item_id),
  unique key uk_site_page_block_item (block_id, item_key),
  key idx_site_page_block_item_sort (block_id, sort_no)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_ai_ci comment='企业官网页面区块条目';

create table site_article_category (
  category_id          bigint not null auto_increment comment '新闻分类ID',
  category_code        varchar(100) not null comment '稳定分类编码',
  category_name        varchar(100) not null comment '分类名称',
  route_slug           varchar(100) default null comment '前台筛选路径标识',
  sort_no              int default 0 comment '显示顺序',
  enabled_flag         char(1) default '1' comment '是否启用（1是 0否）',
  create_by            varchar(64) default '' comment '创建者',
  create_time          datetime default current_timestamp comment '创建时间',
  update_by            varchar(64) default '' comment '更新者',
  update_time          datetime default null on update current_timestamp comment '更新时间',
  remark               varchar(500) default null comment '备注',
  primary key (category_id),
  unique key uk_site_article_category_code (category_code),
  unique key uk_site_article_category_slug (route_slug)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_ai_ci comment='企业官网新闻分类';

create table site_article (
  article_id           bigint not null auto_increment comment '新闻ID',
  article_code         varchar(100) not null comment '稳定新闻编码',
  legacy_key           varchar(500) default null comment '静态站来源稳定键',
  legacy_path          varchar(500) default null comment '切流前详情路径',
  category_id          bigint default null comment '新闻分类ID',
  draft_revision_id    bigint default null comment '当前可编辑草稿修订',
  published_revision_id bigint default null comment '当前公开修订',
  previous_published_revision_id bigint default null comment '上一公开修订',
  del_flag             char(1) default '0' comment '删除标志（0存在 2删除）',
  create_by            varchar(64) default '' comment '创建者',
  create_time          datetime default current_timestamp comment '创建时间',
  update_by            varchar(64) default '' comment '更新者',
  update_time          datetime default null on update current_timestamp comment '更新时间',
  remark               varchar(500) default null comment '备注',
  primary key (article_id),
  unique key uk_site_article_code (article_code),
  unique key uk_site_article_legacy_key (legacy_key),
  unique key uk_site_article_legacy_path (legacy_path),
  key idx_site_article_category_published (category_id, published_revision_id, del_flag)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_ai_ci comment='企业官网新闻根对象';

create table site_article_revision (
  revision_id          bigint not null auto_increment comment '新闻修订ID',
  article_id           bigint not null comment '新闻ID',
  revision_no          int not null comment '新闻内连续修订号',
  revision_state       varchar(20) not null comment 'DRAFT/PUBLISHED/SUPERSEDED/ARCHIVED',
  title                varchar(255) not null comment '新闻标题',
  summary              varchar(2000) default '' comment '摘要',
  body_html            longtext default null comment '已清洗富文本正文',
  body_text            longtext default null comment '检索和核验纯文本',
  cover_media_id       bigint default null comment '封面媒体ID',
  seo_json             json default null comment 'SEO配置',
  top_flag             char(1) not null default '0' comment '是否置顶（0否 1是）',
  content_hash         char(64) default null comment '发布内容聚合哈希',
  source_import_run_id char(36) default null comment '来源导入批次',
  published_at         datetime default null comment '发布时间',
  published_by         varchar(64) default null comment '发布人',
  create_by            varchar(64) default '' comment '创建者',
  create_time          datetime default current_timestamp comment '创建时间',
  update_by            varchar(64) default '' comment '更新者',
  update_time          datetime default null on update current_timestamp comment '更新时间',
  remark               varchar(500) default null comment '备注',
  primary key (revision_id),
  unique key uk_site_article_revision_no (article_id, revision_no),
  key idx_site_article_revision_state (article_id, revision_state),
  key idx_site_article_revision_publish_time (revision_state, published_at),
  key idx_site_article_revision_top_publish (revision_state, top_flag, published_at),
  key idx_site_article_revision_cover (cover_media_id)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_ai_ci comment='企业官网新闻修订';

create table site_case (
  case_id              bigint not null auto_increment comment '案例ID',
  case_code            varchar(100) not null comment '稳定案例编码',
  legacy_key           varchar(500) default null comment '静态站来源稳定键',
  legacy_path          varchar(500) default null comment '切流前详情路径',
  draft_revision_id    bigint default null comment '当前可编辑草稿修订',
  published_revision_id bigint default null comment '当前公开修订',
  previous_published_revision_id bigint default null comment '上一公开修订',
  del_flag             char(1) default '0' comment '删除标志（0存在 2删除）',
  create_by            varchar(64) default '' comment '创建者',
  create_time          datetime default current_timestamp comment '创建时间',
  update_by            varchar(64) default '' comment '更新者',
  update_time          datetime default null on update current_timestamp comment '更新时间',
  remark               varchar(500) default null comment '备注',
  primary key (case_id),
  unique key uk_site_case_code (case_code),
  unique key uk_site_case_legacy_key (legacy_key),
  unique key uk_site_case_legacy_path (legacy_path),
  key idx_site_case_published (published_revision_id, del_flag)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_ai_ci comment='企业官网案例根对象';

create table site_case_revision (
  revision_id          bigint not null auto_increment comment '案例修订ID',
  case_id              bigint not null comment '案例ID',
  revision_no          int not null comment '案例内连续修订号',
  revision_state       varchar(20) not null comment 'DRAFT/PUBLISHED/SUPERSEDED/ARCHIVED',
  title                varchar(255) not null comment '案例标题',
  summary              varchar(2000) default '' comment '摘要',
  body_html            longtext default null comment '已清洗富文本正文',
  cover_media_id       bigint default null comment '封面媒体ID',
  data_json            json default null comment '行业、地区、指标等结构化数据',
  content_hash         char(64) default null comment '发布内容聚合哈希',
  source_import_run_id char(36) default null comment '来源导入批次',
  published_at         datetime default null comment '发布时间',
  published_by         varchar(64) default null comment '发布人',
  create_by            varchar(64) default '' comment '创建者',
  create_time          datetime default current_timestamp comment '创建时间',
  update_by            varchar(64) default '' comment '更新者',
  update_time          datetime default null on update current_timestamp comment '更新时间',
  remark               varchar(500) default null comment '备注',
  primary key (revision_id),
  unique key uk_site_case_revision_no (case_id, revision_no),
  key idx_site_case_revision_state (case_id, revision_state),
  key idx_site_case_revision_cover (cover_media_id)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_ai_ci comment='企业官网案例修订';

create table site_credential (
  credential_id        bigint not null auto_increment comment '资质证书ID',
  credential_code      varchar(100) not null comment '稳定资质编码',
  legacy_key           varchar(500) default null comment '静态站来源稳定键',
  credential_type      varchar(32) not null comment 'SOFTWARE_COPYRIGHT/PATENT/OTHER',
  draft_revision_id    bigint default null comment '当前可编辑草稿修订',
  published_revision_id bigint default null comment '当前公开修订',
  previous_published_revision_id bigint default null comment '上一公开修订',
  del_flag             char(1) default '0' comment '删除标志（0存在 2删除）',
  create_by            varchar(64) default '' comment '创建者',
  create_time          datetime default current_timestamp comment '创建时间',
  update_by            varchar(64) default '' comment '更新者',
  update_time          datetime default null on update current_timestamp comment '更新时间',
  remark               varchar(500) default null comment '备注',
  primary key (credential_id),
  unique key uk_site_credential_code (credential_code),
  unique key uk_site_credential_legacy_key (legacy_key),
  key idx_site_credential_type_published (credential_type, published_revision_id, del_flag)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_ai_ci comment='企业官网资质证书根对象';

create table site_credential_revision (
  revision_id          bigint not null auto_increment comment '资质修订ID',
  credential_id        bigint not null comment '资质证书ID',
  revision_no          int not null comment '资质内连续修订号',
  revision_state       varchar(20) not null comment 'DRAFT/PUBLISHED/SUPERSEDED/ARCHIVED',
  title                varchar(255) not null comment '资质名称',
  credential_no        varchar(255) default null comment '证书或专利编号',
  issue_date           date default null comment '发证日期',
  document_media_id    bigint default null comment '证书文件媒体ID',
  preview_media_id     bigint default null comment '预览图片媒体ID',
  data_json            json default null comment '发证机构等结构化数据',
  content_hash         char(64) default null comment '发布内容聚合哈希',
  source_import_run_id char(36) default null comment '来源导入批次',
  published_at         datetime default null comment '发布时间',
  published_by         varchar(64) default null comment '发布人',
  create_by            varchar(64) default '' comment '创建者',
  create_time          datetime default current_timestamp comment '创建时间',
  update_by            varchar(64) default '' comment '更新者',
  update_time          datetime default null on update current_timestamp comment '更新时间',
  remark               varchar(500) default null comment '备注',
  primary key (revision_id),
  unique key uk_site_credential_revision_no (credential_id, revision_no),
  key idx_site_credential_revision_state (credential_id, revision_state),
  key idx_site_credential_revision_document (document_media_id),
  key idx_site_credential_revision_preview (preview_media_id)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_ai_ci comment='企业官网资质证书修订';

create table site_config (
  config_id            bigint not null auto_increment comment '站点配置ID',
  config_code          varchar(100) not null comment '稳定配置编码，例如NAVIGATION、FOOTER',
  draft_revision_id    bigint default null comment '当前可编辑草稿修订',
  published_revision_id bigint default null comment '当前公开修订',
  previous_published_revision_id bigint default null comment '上一公开修订',
  create_by            varchar(64) default '' comment '创建者',
  create_time          datetime default current_timestamp comment '创建时间',
  update_by            varchar(64) default '' comment '更新者',
  update_time          datetime default null on update current_timestamp comment '更新时间',
  remark               varchar(500) default null comment '备注',
  primary key (config_id),
  unique key uk_site_config_code (config_code),
  key idx_site_config_published (published_revision_id)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_ai_ci comment='企业官网全局配置根对象';

create table site_config_revision (
  revision_id          bigint not null auto_increment comment '配置修订ID',
  config_id            bigint not null comment '站点配置ID',
  revision_no          int not null comment '配置内连续修订号',
  revision_state       varchar(20) not null comment 'DRAFT/PUBLISHED/SUPERSEDED/ARCHIVED',
  data_json            json not null comment '导航、页脚、联系方式等结构化配置',
  content_hash         char(64) default null comment '发布内容聚合哈希',
  source_import_run_id char(36) default null comment '来源导入批次',
  published_at         datetime default null comment '发布时间',
  published_by         varchar(64) default null comment '发布人',
  create_by            varchar(64) default '' comment '创建者',
  create_time          datetime default current_timestamp comment '创建时间',
  update_by            varchar(64) default '' comment '更新者',
  update_time          datetime default null on update current_timestamp comment '更新时间',
  remark               varchar(500) default null comment '备注',
  primary key (revision_id),
  unique key uk_site_config_revision_no (config_id, revision_no),
  key idx_site_config_revision_state (config_id, revision_state)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_ai_ci comment='企业官网全局配置修订';

create table site_media_reference (
  reference_id         bigint not null auto_increment comment '媒体引用ID',
  media_id             bigint not null comment '媒体ID',
  owner_type           varchar(32) not null comment 'PAGE_REVISION/ARTICLE_REVISION/CASE_REVISION/CREDENTIAL_REVISION/CONFIG_REVISION',
  owner_id             bigint not null comment '所属修订ID',
  reference_role       varchar(64) not null comment 'COVER/BODY/ATTACHMENT/SEO/QR_CODE等',
  field_key            varchar(100) default '' comment '结构化字段或区块键',
  sort_no              int default 0 comment '显示顺序',
  primary key (reference_id),
  unique key uk_site_media_reference (media_id, owner_type, owner_id, reference_role, field_key),
  key idx_site_media_reference_owner (owner_type, owner_id)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_ai_ci comment='企业官网修订媒体引用';

create table site_import_item (
  import_item_id        bigint not null auto_increment comment '导入项ID',
  import_run_id         char(36) not null comment '导入批次UUID',
  manifest_item_id      varchar(100) not null comment '清单项标识',
  source_key            varchar(500) not null comment '来源稳定键',
  source_relative_path  varchar(1000) not null comment '相对来源根目录的路径',
  source_sha256         char(64) not null comment '来源字节哈希',
  classification        varchar(32) not null comment 'IMPORT/ARCHIVE/APPROVED_EXCLUSION',
  target_type           varchar(32) default null comment 'PAGE/ARTICLE/CASE/CREDENTIAL/MEDIA/CONFIG',
  target_id             bigint default null comment '目标根或媒体ID',
  target_revision_id    bigint default null comment '目标修订ID',
  item_state            varchar(20) not null comment 'PENDING/IMPORTED/SKIPPED/EXCLUDED/CONFLICT/FAILED',
  conflict_reason       varchar(2000) default null comment '冲突或失败原因',
  evidence_json         json default null comment '来源、目标和哈希核验结果',
  processed_at          datetime default null comment '处理完成时间',
  primary key (import_item_id),
  unique key uk_site_import_item_manifest (import_run_id, manifest_item_id),
  key idx_site_import_item_source (source_key, source_sha256),
  key idx_site_import_item_status (import_run_id, item_state),
  key idx_site_import_item_target (target_type, target_id, target_revision_id)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_ai_ci comment='企业官网导入来源与结果账本';

create table site_publish_audit (
  audit_id             bigint not null auto_increment comment '发布审计ID',
  aggregate_type       varchar(32) not null comment 'PAGE/ARTICLE/CASE/CREDENTIAL/CONFIG',
  aggregate_id         bigint not null comment '根对象ID',
  action_type          varchar(32) not null comment 'PUBLISH/UNPUBLISH/ROLLBACK',
  from_revision_id     bigint default null comment '切换前修订ID',
  to_revision_id       bigint default null comment '切换后修订ID',
  operator             varchar(64) not null comment '操作人',
  operation_at         datetime default current_timestamp comment '操作时间',
  reason               varchar(500) default null comment '发布说明或回退原因',
  primary key (audit_id),
  key idx_site_publish_audit_aggregate (aggregate_type, aggregate_id, operation_at)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_ai_ci comment='企业官网发布与回退审计';

-- 后台菜单。对应页面在后台功能单元完成前不应切换至生产环境。
insert into sys_menu values
(2100, '企业官网', 0, 2, 'site', null, '', '', 1, 0, 'M', '0', '0', '', 'guide', 'admin', sysdate(), '', null, '企业官网内容、媒体与发布管理目录'),
(2101, '菜单管理', 2100, 1, 'page', 'site/page/index', '', '', 1, 0, 'C', '0', '0', 'site:page:list', 'edit', 'admin', sysdate(), '', null, '官网菜单、页面与修订维护'),
(2102, '页面查询', 2101, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'site:page:query', '#', 'admin', sysdate(), '', null, ''),
(2103, '页面新增', 2101, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'site:page:add', '#', 'admin', sysdate(), '', null, ''),
(2104, '页面修改', 2101, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'site:page:edit', '#', 'admin', sysdate(), '', null, ''),
(2105, '页面发布', 2101, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'site:page:publish', '#', 'admin', sysdate(), '', null, ''),
(2106, '新闻管理', 2100, 2, 'article', 'site/article/index', '', '', 1, 0, 'C', '0', '0', 'site:article:list', 'documentation', 'admin', sysdate(), '', null, '官网新闻与修订维护'),
(2107, '新闻查询', 2106, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'site:article:query', '#', 'admin', sysdate(), '', null, ''),
(2108, '新闻新增', 2106, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'site:article:add', '#', 'admin', sysdate(), '', null, ''),
(2109, '新闻修改', 2106, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'site:article:edit', '#', 'admin', sysdate(), '', null, ''),
(2110, '新闻发布', 2106, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'site:article:publish', '#', 'admin', sysdate(), '', null, ''),
(2116, '资质证书', 2100, 3, 'credential', 'site/credential/index', '', '', 1, 0, 'C', '0', '0', 'site:credential:list', 'license', 'admin', sysdate(), '', null, '官网证书、软件著作权与专利维护'),
(2117, '证书查询', 2116, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'site:credential:query', '#', 'admin', sysdate(), '', null, ''),
(2118, '证书新增', 2116, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'site:credential:add', '#', 'admin', sysdate(), '', null, ''),
(2119, '证书修改', 2116, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'site:credential:edit', '#', 'admin', sysdate(), '', null, ''),
(2120, '证书发布', 2116, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'site:credential:publish', '#', 'admin', sysdate(), '', null, ''),
(2121, '媒体库', 2100, 4, 'media', 'site/media/index', '', '', 1, 0, 'C', '0', '0', 'site:media:list', 'picture', 'admin', sysdate(), '', null, '官网专用受控媒体库'),
(2122, '媒体上传', 2121, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'site:media:add', '#', 'admin', sysdate(), '', null, '');

insert into sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark) values
(130, '官网修订状态', 'site_revision_state', '0', 'admin', sysdate(), '草稿、已发布、已替代、已归档'),
(131, '官网媒体状态', 'site_media_state', '0', 'admin', sysdate(), '私有暂存、可公开、已退役'),
(132, '官网导入状态', 'site_import_state', '0', 'admin', sysdate(), '导入项处理状态');

insert into sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time, remark) values
(130, 1, '草稿', 'DRAFT', 'site_revision_state', 'info', 'Y', '0', 'admin', sysdate(), ''),
(131, 2, '已发布', 'PUBLISHED', 'site_revision_state', 'success', 'N', '0', 'admin', sysdate(), ''),
(132, 3, '已替代', 'SUPERSEDED', 'site_revision_state', 'warning', 'N', '0', 'admin', sysdate(), ''),
(133, 4, '已归档', 'ARCHIVED', 'site_revision_state', 'info', 'N', '0', 'admin', sysdate(), ''),
(134, 1, '私有暂存', 'STAGED', 'site_media_state', 'warning', 'Y', '0', 'admin', sysdate(), ''),
(135, 2, '可公开', 'AVAILABLE', 'site_media_state', 'success', 'N', '0', 'admin', sysdate(), ''),
(136, 3, '已退役', 'RETIRED', 'site_media_state', 'info', 'N', '0', 'admin', sysdate(), ''),
(137, 1, '待处理', 'PENDING', 'site_import_state', 'info', 'Y', '0', 'admin', sysdate(), ''),
(138, 2, '已导入', 'IMPORTED', 'site_import_state', 'success', 'N', '0', 'admin', sysdate(), ''),
(139, 3, '已跳过', 'SKIPPED', 'site_import_state', 'info', 'N', '0', 'admin', sysdate(), ''),
(140, 4, '已排除', 'EXCLUDED', 'site_import_state', 'info', 'N', '0', 'admin', sysdate(), ''),
(141, 5, '存在冲突', 'CONFLICT', 'site_import_state', 'danger', 'N', '0', 'admin', sysdate(), ''),
(142, 6, '导入失败', 'FAILED', 'site_import_state', 'danger', 'N', '0', 'admin', sysdate(), '');
