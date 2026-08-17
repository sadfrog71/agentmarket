-- 智能体市场演示环境地址升级：可重复执行
-- 演示地址为可选字段；填写合法 http/https 地址后，前台详情页显示演示入口。
use `ry-vue`;
set names utf8mb4;

set @demo_url_column_exists = (
  select count(*) from information_schema.columns
  where table_schema = database() and table_name = 'biz_agent'
    and column_name = 'demo_url'
);
set @demo_url_column_sql = if(
  @demo_url_column_exists = 0,
  'alter table biz_agent add column demo_url varchar(500) default null comment ''演示环境地址'' after cover_url',
  'select 1'
);
prepare demo_url_column_statement from @demo_url_column_sql;
execute demo_url_column_statement;
deallocate prepare demo_url_column_statement;
