-- 华衍水务环境智能体市场：一期业务表与初始化数据
-- 依赖：先执行 RuoYi 主库脚本 ry_20260417.sql

set names utf8mb4;

drop table if exists biz_business_follow;
drop table if exists biz_business_record;
drop table if exists biz_site_content;
drop table if exists biz_agent_detail_item;
drop table if exists biz_agent;

create table biz_site_content (
  content_id        bigint not null auto_increment comment '页面内容ID',
  content_key       varchar(64) not null comment '稳定页面标识',
  content_name      varchar(100) not null comment '后台内容名称',
  title             varchar(200) not null comment '前台页面标题',
  subtitle          varchar(500) default '' comment '前台页面副标题',
  content           longtext comment '富文本或Markdown内容',
  content_format    varchar(20) default 'RICH_TEXT' comment '内容格式（RICH_TEXT/MARKDOWN）',
  publish_status    char(1) default '0' comment '发布状态（0草稿 1发布 2下架）',
  sort_no           int default 0 comment '显示顺序',
  ext_json          json default null comment '扩展属性',
  del_flag          char(1) default '0' comment '删除标志（0正常 2删除）',
  create_by         varchar(64) default '' comment '创建者',
  create_time       datetime default current_timestamp comment '创建时间',
  update_by         varchar(64) default '' comment '更新者',
  update_time       datetime default null on update current_timestamp comment '更新时间',
  remark            varchar(500) default null comment '备注',
  primary key (content_id),
  unique key uk_site_content_key (content_key),
  key idx_site_content_status_sort (publish_status, sort_no, del_flag)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_ai_ci comment='前台页面内容';

create table biz_agent (
  agent_id          bigint not null auto_increment comment '智能体ID',
  agent_code        varchar(64) not null comment '稳定业务编码',
  agent_name        varchar(100) not null comment '智能体名称',
  category_code     varchar(64) default '' comment '场景域编码',
  icon_code         varchar(64) default 'robot' comment '前端图标编码',
  cover_url         varchar(500) default null comment '封面地址',
  provider_id       bigint default null comment '预留供应商ID',
  provider_name     varchar(150) default '' comment '供应商名称快照',
  summary           varchar(1000) default '' comment '卡片摘要',
  description       longtext comment '完整说明',
  price_text        varchar(100) default '面议' comment '前台价格文字',
  price_min         decimal(12,2) default null comment '预留最低价格',
  price_max         decimal(12,2) default null comment '预留最高价格',
  cert_level        varchar(20) default 'L1' comment '认证等级',
  rating            decimal(2,1) default 0.0 comment '展示评分',
  deploy_count      int default 0 comment '部署数量',
  delivery_cycle    varchar(100) default '' comment '交付周期',
  service_mode      varchar(100) default '' comment '服务方式',
  recommend_flag    char(1) default 'N' comment '首页推荐（Y/N）',
  hot_score         int default 0 comment '热门排序分',
  sort_no           int default 0 comment '显示顺序',
  publish_status    char(1) default '0' comment '发布状态（0草稿 1发布 2下架）',
  published_at      datetime default null comment '发布时间',
  slug              varchar(120) default null comment '预留详情地址标识',
  version_no        int default 1 comment '内容版本号',
  ext_json          json default null comment '非检索扩展信息',
  del_flag          char(1) default '0' comment '删除标志（0正常 2删除）',
  create_by         varchar(64) default '' comment '创建者',
  create_time       datetime default current_timestamp comment '创建时间',
  update_by         varchar(64) default '' comment '更新者',
  update_time       datetime default null on update current_timestamp comment '更新时间',
  remark            varchar(500) default null comment '备注',
  primary key (agent_id),
  unique key uk_biz_agent_code (agent_code),
  unique key uk_biz_agent_slug (slug),
  key idx_biz_agent_category_status (category_code, publish_status, del_flag),
  key idx_biz_agent_recommend_sort (recommend_flag, sort_no, hot_score),
  key idx_biz_agent_publish_time (publish_status, published_at)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_ai_ci comment='智能体主表';

create table biz_agent_detail_item (
  item_id           bigint not null auto_increment comment '详情项ID',
  agent_id          bigint not null comment '智能体ID',
  item_type         varchar(32) not null comment '详情项类型',
  title             varchar(200) default '' comment '标题或字段名',
  subtitle          varchar(300) default '' comment '副标题',
  value_text        varchar(500) default '' comment '指标值或字段值',
  content           text comment '内容说明',
  icon_code         varchar(64) default '' comment '图标编码',
  link_url          varchar(500) default null comment '预留链接',
  sort_no           int default 0 comment '显示顺序',
  status            char(1) default '0' comment '状态（0正常 1停用）',
  ext_json          json default null comment '扩展属性',
  create_time       datetime default current_timestamp comment '创建时间',
  update_time       datetime default null on update current_timestamp comment '更新时间',
  primary key (item_id),
  key idx_detail_agent_type_sort (agent_id, item_type, sort_no)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_ai_ci comment='智能体详情结构项';

create table biz_business_record (
  record_id         bigint not null auto_increment comment '登记ID',
  record_no         varchar(64) not null comment '登记编号',
  record_type       varchar(32) not null comment '登记类型',
  source_type       varchar(32) default 'OFFLINE' comment '来源',
  company_name      varchar(200) default '' comment '客户或供应商名称',
  contact_name      varchar(100) default '' comment '联系人',
  contact_phone     varchar(50) default '' comment '联系电话',
  contact_email     varchar(150) default '' comment '联系邮箱',
  agent_id          bigint default null comment '关联智能体',
  provider_id       bigint default null comment '预留供应商ID',
  owner_id          bigint default null comment '负责人用户ID',
  record_status     varchar(32) default 'PENDING' comment '处理状态',
  requirement       text comment '需求说明',
  next_follow_time  datetime default null comment '下次跟进时间',
  last_follow_time  datetime default null comment '最近跟进时间',
  result_summary    text comment '处理结果',
  ext_json          json default null comment '扩展属性',
  del_flag          char(1) default '0' comment '删除标志',
  create_by         varchar(64) default '' comment '创建者',
  create_time       datetime default current_timestamp comment '创建时间',
  update_by         varchar(64) default '' comment '更新者',
  update_time       datetime default null on update current_timestamp comment '更新时间',
  remark            varchar(500) default null comment '备注',
  primary key (record_id),
  unique key uk_business_record_no (record_no),
  key idx_business_status_owner (record_type, record_status, owner_id),
  key idx_business_agent (agent_id)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_ai_ci comment='线下商务登记';

create table biz_business_follow (
  follow_id         bigint not null auto_increment comment '跟进ID',
  record_id         bigint not null comment '登记ID',
  follow_type       varchar(32) default 'PHONE' comment '跟进方式',
  follow_content    text not null comment '跟进内容',
  next_follow_time  datetime default null comment '下次跟进时间',
  attachment_url    varchar(500) default null comment '附件地址',
  create_by         varchar(64) default '' comment '创建者',
  create_time       datetime default current_timestamp comment '创建时间',
  primary key (follow_id),
  key idx_follow_record_time (record_id, create_time)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_ai_ci comment='商务跟进记录';

-- 后台菜单
insert into sys_menu values
(2000, '智能体市场', 0, 1, 'market', null, '', '', 1, 0, 'M', '0', '0', '', 'shopping', 'admin', sysdate(), '', null, '智能体市场目录'),
(2001, '智能体管理', 2000, 1, 'agent', 'market/agent/index', '', '', 1, 0, 'C', '0', '0', 'market:agent:list', 'component', 'admin', sysdate(), '', null, '智能体内容管理'),
(2002, '智能体查询', 2001, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'market:agent:query', '#', 'admin', sysdate(), '', null, ''),
(2003, '智能体新增', 2001, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'market:agent:add', '#', 'admin', sysdate(), '', null, ''),
(2004, '智能体修改', 2001, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'market:agent:edit', '#', 'admin', sysdate(), '', null, ''),
(2005, '智能体删除', 2001, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'market:agent:remove', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu values
(2006, '商务登记', 2000, 2, 'business', 'market/business/index', '', '', 1, 0, 'C', '0', '0', 'market:business:list', 'peoples', 'admin', sysdate(), '', null, '线下咨询、上架与实施登记'),
(2007, '商务登记查询', 2006, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'market:business:query', '#', 'admin', sysdate(), '', null, ''),
(2008, '商务登记新增', 2006, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'market:business:add', '#', 'admin', sysdate(), '', null, ''),
(2009, '商务登记修改', 2006, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'market:business:edit', '#', 'admin', sysdate(), '', null, ''),
(2010, '商务登记删除', 2006, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'market:business:remove', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu values
(2011, '页面内容', 2000, 3, 'content', 'market/content/index', '', '', 1, 0, 'C', '0', '0', 'market:content:list', 'edit', 'admin', sysdate(), '', null, '算力中心、联系我们等前台内容维护'),
(2012, '页面内容查询', 2011, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'market:content:query', '#', 'admin', sysdate(), '', null, ''),
(2013, '页面内容新增', 2011, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'market:content:add', '#', 'admin', sysdate(), '', null, ''),
(2014, '页面内容修改', 2011, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'market:content:edit', '#', 'admin', sysdate(), '', null, ''),
(2015, '页面内容删除', 2011, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'market:content:remove', '#', 'admin', sysdate(), '', null, '');

-- 字典类型
insert into sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark) values
(100, '智能体场景域', 'market_agent_category', '0', 'admin', sysdate(), '智能体六大场景域'),
(101, '智能体认证等级', 'market_cert_level', '0', 'admin', sysdate(), 'L1-L3认证等级'),
(102, '智能体发布状态', 'market_publish_status', '0', 'admin', sysdate(), '草稿、发布、下架'),
(103, '智能体详情项类型', 'market_detail_item_type', '0', 'admin', sysdate(), '详情页结构块类型'),
(104, '商务登记类型', 'market_business_type', '0', 'admin', sysdate(), '线下商务登记类型'),
(105, '商务处理状态', 'market_business_status', '0', 'admin', sysdate(), '线下商务处理状态');

insert into sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time, remark) values
(100, 1, '供水生产与调度', 'production', 'market_agent_category', 'primary', 'Y', '0', 'admin', sysdate(), ''),
(101, 2, '管网漏损与运行', 'network', 'market_agent_category', 'success', 'N', '0', 'admin', sysdate(), ''),
(102, 3, '水质安全', 'quality', 'market_agent_category', 'warning', 'N', '0', 'admin', sysdate(), ''),
(103, 4, '客户服务与营销', 'customer', 'market_agent_category', 'info', 'N', '0', 'admin', sysdate(), ''),
(104, 5, '工程建设与资产', 'engineering', 'market_agent_category', 'primary', 'N', '0', 'admin', sysdate(), ''),
(105, 6, '管理与决策', 'management', 'market_agent_category', 'success', 'N', '0', 'admin', sysdate(), ''),
(106, 1, 'L1 基础级', 'L1', 'market_cert_level', 'info', 'N', '0', 'admin', sysdate(), ''),
(107, 2, 'L2 认证级', 'L2', 'market_cert_level', 'primary', 'Y', '0', 'admin', sysdate(), ''),
(108, 3, 'L3 标杆级', 'L3', 'market_cert_level', 'success', 'N', '0', 'admin', sysdate(), ''),
(109, 1, '草稿', '0', 'market_publish_status', 'info', 'Y', '0', 'admin', sysdate(), ''),
(110, 2, '已发布', '1', 'market_publish_status', 'success', 'N', '0', 'admin', sysdate(), ''),
(111, 3, '已下架', '2', 'market_publish_status', 'danger', 'N', '0', 'admin', sysdate(), ''),
(112, 1, '核心功能', 'FEATURE', 'market_detail_item_type', 'primary', 'Y', '0', 'admin', sysdate(), ''),
(113, 2, '效果指标', 'METRIC', 'market_detail_item_type', 'success', 'N', '0', 'admin', sysdate(), ''),
(114, 3, '部署案例', 'CASE', 'market_detail_item_type', 'warning', 'N', '0', 'admin', sysdate(), ''),
(115, 4, '实施服务内容', 'PRICE_FEATURE', 'market_detail_item_type', 'info', 'N', '0', 'admin', sysdate(), ''),
(116, 5, '兼容性信息', 'COMPATIBILITY', 'market_detail_item_type', 'primary', 'N', '0', 'admin', sysdate(), ''),
(117, 1, '客户咨询', 'INQUIRY', 'market_business_type', 'primary', 'Y', '0', 'admin', sysdate(), ''),
(118, 2, '智能体上架', 'ONBOARD', 'market_business_type', 'warning', 'N', '0', 'admin', sysdate(), ''),
(119, 3, '实施服务', 'IMPLEMENTATION', 'market_business_type', 'success', 'N', '0', 'admin', sysdate(), ''),
(120, 1, '待处理', 'PENDING', 'market_business_status', 'warning', 'Y', '0', 'admin', sysdate(), ''),
(121, 2, '跟进中', 'FOLLOWING', 'market_business_status', 'primary', 'N', '0', 'admin', sysdate(), ''),
(122, 3, '已确认', 'CONFIRMED', 'market_business_status', 'success', 'N', '0', 'admin', sysdate(), ''),
(123, 4, '已关闭', 'CLOSED', 'market_business_status', 'info', 'N', '0', 'admin', sysdate(), '');

-- 页面内容初始化：算力中心保持草稿空内容，联系我们发布 Markdown 示例
insert into biz_site_content
(content_key, content_name, title, subtitle, content, content_format, publish_status, sort_no, create_by, remark) values
('COMPUTE', '算力中心', '算力中心', '用于展示模型资源、算力池、调用消耗和智能体资源占用。', '', 'MARKDOWN', '0', 1, 'admin', '未发布或内容为空时，前台展示暂无数据'),
('CONTACT', '联系我们', '联系实施与上架', '一期采用线下沟通方式。确认需求、材料和实施范围后，由平台管理员完成登记与内容发布。', '### 业务咨询\n\n联系方式将在部署前配置。\n\n> 支持智能体实施评估、上架沟通与 FDE 服务咨询。', 'MARKDOWN', '1', 2, 'admin', '前台联系弹窗内容');

-- 首条示例智能体，用于接口与页面联调
insert into biz_agent
(agent_id, agent_code, agent_name, category_code, icon_code, provider_name, summary,
 description, price_text, price_min, price_max, cert_level, rating, deploy_count,
 delivery_cycle, service_mode, recommend_flag, hot_score, sort_no, publish_status,
 published_at, slug, create_by)
values
(1, 'DMA_LEAKAGE', 'DMA漏损分析智能体', 'network', 'search', '华衍水务研究院',
 '基于 DMA 分区计量数据与 AI 时序分析模型，自动识别漏损异常，精准定位漏点区域，并给出修复优先级与效益测算。',
 '<h3>DMA 漏损智能分析与闭环治理</h3><p>基于 <strong>DMA 分区计量数据</strong>与 AI 时序分析模型，自动识别各分区漏损异常，精准定位漏点区域，并提供修复优先级排序与经济效益测算。</p><p>已在华衍旗下 3 家水司完成实测验证，平均漏损率从 <strong>13.2% 降至 8.7%</strong>，达到 L2 认证标准。</p>',
 '5-15万', 50000, 150000, 'L2', 4.8, 18, '2-4周', '本地化部署', 'Y', 98, 1, '1',
 sysdate(), 'dma-leakage-agent', 'admin');

insert into biz_agent_detail_item
(agent_id, item_type, title, value_text, content, icon_code, sort_no) values
(1, 'FEATURE', '实时漏损监测', '', '7×24 小时持续监测各 DMA 分区夜间最小流量，自动计算漏损指数（ILI），异常时即时告警。', 'chart', 1),
(1, 'FEATURE', '漏点区域精准定位', '', '结合管网拓扑与压力数据，将漏损范围缩小至 500 米管段级别，减少无效开挖。', 'target', 2),
(1, 'FEATURE', '修复优先级智能排序', '', '综合漏损量、管龄、修复成本和影响用户数，自动生成修复工单优先级清单。', 'sort', 3),
(1, 'FEATURE', '效益测算与报告生成', '', '自动计算漏损治理经济效益，一键生成可提交监管部门的漏损管控报告。', 'report', 4),
(1, 'METRIC', '漏损率平均降幅', '34%', '13.2% → 8.7%', '', 1),
(1, 'METRIC', '预警时间提前量', '2h', '相比人工巡检', '', 2),
(1, 'METRIC', '已部署水司数量', '18家', '含 3 家 L2 验证单位', '', 3),
(1, 'CASE', '华衍（无锡）水务有限公司', '产销差率降低 34% · 年节水 120 万吨', '2024 年 10 月完成部署，覆盖全市 48 个 DMA 分区，接入 SCADA 系统实时数据流。部署后 6 个月内，产销差率从 14.1% 下降至 9.3%，年节约水量约 120 万吨，直接经济效益约 360 万元。', '', 1),
(1, 'CASE', '华衍（张家港）水务有限公司', '漏损率降 5.2% · 修复成本降低 60%', '2025 年 3 月完成部署，重点针对老城区高漏损管网。智能体识别出 3 处长期未被发现的隐性漏点，修复后当月漏损率下降 5.2 个百分点，修复成本较传统方式降低 60%。', '', 2),
(1, 'PRICE_FEATURE', '计费方式', '', '智能体授权 + 一次性实施费', 'check', 1),
(1, 'PRICE_FEATURE', '本地化部署', '', '数据不出水司', 'check', 2),
(1, 'PRICE_FEATURE', '标准实施周期', '', '30 天', 'check', 3),
(1, 'PRICE_FEATURE', '效果目标', '', '漏损率从 13% 降至 9% 以下', 'check', 4),
(1, 'PRICE_FEATURE', '接入规模', '', '最多 50 个 DMA 分区接入', 'check', 5),
(1, 'PRICE_FEATURE', '运营服务', '', '月度效果报告与优化建议', 'check', 6),
(1, 'PRICE_FEATURE', '保障方式', '', '效果不达标可按约定退款', 'check', 7),
(1, 'COMPATIBILITY', '数据接口', 'SCADA / OPC-UA', '', '', 1),
(1, 'COMPATIBILITY', '部署方式', '本地化 / 私有化', '', '', 2),
(1, 'COMPATIBILITY', '操作系统', 'Windows / Linux', '', '', 3),
(1, 'COMPATIBILITY', '数据安全', '等保三级认证', '', '', 4),
(1, 'COMPATIBILITY', '最小数据量', '≥ 6 个月历史数据', '', '', 5),
(1, 'COMPATIBILITY', '上线周期', '2-4 周', '', '', 6);
