use `ry-vue`;

insert ignore into sys_menu values
(2006, '商务登记', 2000, 2, 'business', 'market/business/index', '', '', 1, 0, 'C', '0', '0', 'market:business:list', 'peoples', 'admin', sysdate(), '', null, '线下咨询、上架与实施登记'),
(2007, '商务登记查询', 2006, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'market:business:query', '#', 'admin', sysdate(), '', null, ''),
(2008, '商务登记新增', 2006, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'market:business:add', '#', 'admin', sysdate(), '', null, ''),
(2009, '商务登记修改', 2006, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'market:business:edit', '#', 'admin', sysdate(), '', null, ''),
(2010, '商务登记删除', 2006, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'market:business:remove', '#', 'admin', sysdate(), '', null, '');

insert ignore into sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark) values
(104, '商务登记类型', 'market_business_type', '0', 'admin', sysdate(), '线下商务登记类型'),
(105, '商务处理状态', 'market_business_status', '0', 'admin', sysdate(), '线下商务处理状态');

insert ignore into sys_dict_data
(dict_code, dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time, remark) values
(117, 1, '客户咨询', 'INQUIRY', 'market_business_type', 'primary', 'Y', '0', 'admin', sysdate(), ''),
(118, 2, '智能体上架', 'ONBOARD', 'market_business_type', 'warning', 'N', '0', 'admin', sysdate(), ''),
(119, 3, '实施服务', 'IMPLEMENTATION', 'market_business_type', 'success', 'N', '0', 'admin', sysdate(), ''),
(120, 1, '待处理', 'PENDING', 'market_business_status', 'warning', 'Y', '0', 'admin', sysdate(), ''),
(121, 2, '跟进中', 'FOLLOWING', 'market_business_status', 'primary', 'N', '0', 'admin', sysdate(), ''),
(122, 3, '已确认', 'CONFIRMED', 'market_business_status', 'success', 'N', '0', 'admin', sysdate(), ''),
(123, 4, '已关闭', 'CLOSED', 'market_business_status', 'info', 'N', '0', 'admin', sysdate(), '');
