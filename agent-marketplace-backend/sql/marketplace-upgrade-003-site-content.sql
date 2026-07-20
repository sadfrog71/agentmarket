-- 页面内容管理升级：可重复执行
set names utf8mb4;

create table if not exists biz_site_content (
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

insert into biz_site_content
(content_key, content_name, title, subtitle, content, content_format, publish_status, sort_no, create_by, remark)
select 'COMPUTE', '算力中心', '算力中心', '用于展示模型资源、算力池、调用消耗和智能体资源占用。', '', 'MARKDOWN', '0', 1, 'admin', '未发布或内容为空时，前台展示暂无数据'
where not exists (select 1 from biz_site_content where content_key = 'COMPUTE');

insert into biz_site_content
(content_key, content_name, title, subtitle, content, content_format, publish_status, sort_no, create_by, remark)
select 'CONTACT', '联系我们', '联系实施与上架', '一期采用线下沟通方式。确认需求、材料和实施范围后，由平台管理员完成登记与内容发布。', '### 业务咨询\n\n联系方式将在部署前配置。\n\n> 支持智能体实施评估、上架沟通与 FDE 服务咨询。', 'MARKDOWN', '1', 2, 'admin', '前台联系弹窗内容'
where not exists (select 1 from biz_site_content where content_key = 'CONTACT');

insert into sys_menu
select 2011, '页面内容', 2000, 3, 'content', 'market/content/index', '', '', 1, 0, 'C', '0', '0', 'market:content:list', 'edit', 'admin', sysdate(), '', null, '算力中心、联系我们等前台内容维护'
where not exists (select 1 from sys_menu where menu_id = 2011);

insert into sys_menu
select 2012, '页面内容查询', 2011, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'market:content:query', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2012);

insert into sys_menu
select 2013, '页面内容新增', 2011, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'market:content:add', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2013);

insert into sys_menu
select 2014, '页面内容修改', 2011, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'market:content:edit', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2014);

insert into sys_menu
select 2015, '页面内容删除', 2011, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'market:content:remove', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2015);
