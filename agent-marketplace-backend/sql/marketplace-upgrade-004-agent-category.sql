-- 智能体市场一级分类升级：可重复执行
-- 供水、排水、燃气为默认分类；现有智能体默认归入供水。
use `ry-vue`;
set names utf8mb4;

create table if not exists biz_agent_category (
  category_id       bigint not null auto_increment comment '一级分类ID',
  category_code     varchar(64) not null comment '一级分类编码',
  category_name     varchar(100) not null comment '一级分类名称',
  description       varchar(500) default '' comment '分类说明',
  icon_code         varchar(64) default 'water' comment '前端图标编码',
  sort_no           int default 0 comment '显示顺序',
  status            char(1) default '0' comment '状态（0正常 1停用）',
  del_flag          char(1) default '0' comment '删除标志（0正常 2删除）',
  create_by         varchar(64) default '' comment '创建者',
  create_time       datetime default current_timestamp comment '创建时间',
  update_by         varchar(64) default '' comment '更新者',
  update_time       datetime default null on update current_timestamp comment '更新时间',
  remark            varchar(500) default null comment '备注',
  primary key (category_id),
  unique key uk_agent_category_code (category_code),
  key idx_agent_category_status_sort (status, sort_no, del_flag)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_0900_ai_ci comment='智能体市场一级分类';

set @category_column_exists = (
  select count(*) from information_schema.columns
  where table_schema = database() and table_name = 'biz_agent'
    and column_name = 'primary_category_code'
);
set @category_column_sql = if(
  @category_column_exists = 0,
  'alter table biz_agent add column primary_category_code varchar(64) not null default ''water'' comment ''一级分类编码'' after agent_name',
  'select 1'
);
prepare category_column_statement from @category_column_sql;
execute category_column_statement;
deallocate prepare category_column_statement;

set @category_index_exists = (
  select count(*) from information_schema.statistics
  where table_schema = database() and table_name = 'biz_agent'
    and index_name = 'idx_biz_agent_primary_category_status'
);
set @category_index_sql = if(
  @category_index_exists = 0,
  'alter table biz_agent add index idx_biz_agent_primary_category_status (primary_category_code, publish_status, del_flag)',
  'select 1'
);
prepare category_index_statement from @category_index_sql;
execute category_index_statement;
deallocate prepare category_index_statement;

insert into biz_agent_category
(category_code, category_name, description, icon_code, sort_no, status, create_by, remark)
select 'water', '供水', '供水生产、管网运行、客户服务和水质安全等业务。', 'water', 1, '0', 'admin', '默认一级分类'
where not exists (select 1 from biz_agent_category where category_code = 'water');

insert into biz_agent_category
(category_code, category_name, description, icon_code, sort_no, status, create_by, remark)
select 'drainage', '排水', '排水管网、污水处理、泵站运行和防汛排涝等业务。', 'drainage', 2, '0', 'admin', '默认一级分类'
where not exists (select 1 from biz_agent_category where category_code = 'drainage');

insert into biz_agent_category
(category_code, category_name, description, icon_code, sort_no, status, create_by, remark)
select 'gas', '燃气', '燃气输配、巡检、客服和安全管理等业务。', 'gas', 3, '0', 'admin', '默认一级分类'
where not exists (select 1 from biz_agent_category where category_code = 'gas');

update biz_agent
set primary_category_code = 'water'
where primary_category_code is null or trim(primary_category_code) = '';

insert into sys_menu
select 2016, '一级分类', 2000, 4, 'category', 'market/category/index', '', '', 1, 0, 'C', '0', '0', 'market:category:list', 'list', 'admin', sysdate(), '', null, '智能体市场一级分类维护'
where not exists (select 1 from sys_menu where menu_id = 2016);

insert into sys_menu
select 2017, '一级分类查询', 2016, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'market:category:query', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2017);

insert into sys_menu
select 2018, '一级分类新增', 2016, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'market:category:add', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2018);

insert into sys_menu
select 2019, '一级分类修改', 2016, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'market:category:edit', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2019);

insert into sys_menu
select 2020, '一级分类删除', 2016, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'market:category:remove', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2020);

insert ignore into sys_role_menu (role_id, menu_id) values
(2, 2016), (2, 2017), (2, 2018), (2, 2019), (2, 2020);
