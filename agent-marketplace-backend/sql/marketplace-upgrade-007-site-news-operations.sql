-- 企业官网运营升级 007：新闻富文本、自动编码与置顶能力。
-- 适用于已执行 marketplace-upgrade-006-site-core.sql 的环境；生产发布时仅执行一次。
set names utf8mb4;

alter table site_article_revision
  add column top_flag char(1) not null default '0' comment '是否置顶（0否 1是）' after seo_json,
  add key idx_site_article_revision_top_publish (revision_state, top_flag, published_at);

-- 后台导航使用运营语言；隐藏框架默认外链，避免出现在运营后台。
update sys_menu
set menu_name = '菜单管理', remark = '官网菜单、页面与修订维护', update_by = 'site-upgrade-007', update_time = sysdate()
where menu_id = 2101;

update sys_menu
set visible = '1', status = '1', update_by = 'site-upgrade-007', update_time = sysdate()
where menu_id = 4;
