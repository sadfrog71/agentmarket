-- 水务 AI 案例图片 URL 写回脚本
-- 由 upload-case-assets.sh 根据实际上传结果生成。
-- 执行前请先确认所有 URL 可以从前台浏览器访问。
USE `ry-vue`;
SET NAMES utf8mb4;
START TRANSACTION;

-- WATER_AI_CUSTOMER_SERVICE AI智能客服
UPDATE biz_agent
SET cover_url = 'http://127.0.0.1:18081/profile/upload/2026/08/17/customer-service-screen_20260817100907A001.png',
    description = CASE
        WHEN INSTR(COALESCE(description, ''), '<!-- CASE_ASSET:WATER_AI_CUSTOMER_SERVICE -->') = 0
        THEN CONCAT(COALESCE(description, ''), '<!-- CASE_ASSET:WATER_AI_CUSTOMER_SERVICE --><hr><p><strong>项目截图</strong></p><p><img src="http://127.0.0.1:18081/profile/upload/2026/08/17/customer-service-screen_20260817100907A001.png" alt="AI智能客服项目截图1" style="max-width:100%;height:auto;"></p><p><img src="http://127.0.0.1:18081/profile/upload/2026/08/17/customer-service-screen-2_20260817100907A002.png" alt="AI智能客服项目截图2" style="max-width:100%;height:auto;"></p><!-- /CASE_ASSET:WATER_AI_CUSTOMER_SERVICE -->')
        ELSE description
    END,
    ext_json = JSON_SET(COALESCE(ext_json, JSON_OBJECT()), '$.coverUrl', 'http://127.0.0.1:18081/profile/upload/2026/08/17/customer-service-screen_20260817100907A001.png', '$.imageUrls', JSON_ARRAY('http://127.0.0.1:18081/profile/upload/2026/08/17/customer-service-screen_20260817100907A001.png', 'http://127.0.0.1:18081/profile/upload/2026/08/17/customer-service-screen-2_20260817100907A002.png')),
    update_by = 'asset_import',
    update_time = CURRENT_TIMESTAMP
WHERE agent_code = 'WATER_AI_CUSTOMER_SERVICE' AND del_flag = '0';

-- WATER_CUSTOMER_WATER_ALERT 客户异常水量预警
UPDATE biz_agent
SET cover_url = 'http://127.0.0.1:18081/profile/upload/2026/08/17/alert-flow_20260817100907A003.png',
    description = CASE
        WHEN INSTR(COALESCE(description, ''), '<!-- CASE_ASSET:WATER_CUSTOMER_WATER_ALERT -->') = 0
        THEN CONCAT(COALESCE(description, ''), '<!-- CASE_ASSET:WATER_CUSTOMER_WATER_ALERT --><hr><p><strong>项目截图</strong></p><p><img src="http://127.0.0.1:18081/profile/upload/2026/08/17/alert-flow_20260817100907A003.png" alt="客户异常水量预警项目截图1" style="max-width:100%;height:auto;"></p><p><img src="http://127.0.0.1:18081/profile/upload/2026/08/17/alert-trend_20260817100907A004.png" alt="客户异常水量预警项目截图2" style="max-width:100%;height:auto;"></p><!-- /CASE_ASSET:WATER_CUSTOMER_WATER_ALERT -->')
        ELSE description
    END,
    ext_json = JSON_SET(COALESCE(ext_json, JSON_OBJECT()), '$.coverUrl', 'http://127.0.0.1:18081/profile/upload/2026/08/17/alert-flow_20260817100907A003.png', '$.imageUrls', JSON_ARRAY('http://127.0.0.1:18081/profile/upload/2026/08/17/alert-flow_20260817100907A003.png', 'http://127.0.0.1:18081/profile/upload/2026/08/17/alert-trend_20260817100907A004.png')),
    update_by = 'asset_import',
    update_time = CURRENT_TIMESTAMP
WHERE agent_code = 'WATER_CUSTOMER_WATER_ALERT' AND del_flag = '0';

-- WATER_CUSTOMER_SERVICE_REPORT AI客服报表自动化
UPDATE biz_agent
SET cover_url = 'http://127.0.0.1:18081/profile/upload/2026/08/17/report-dashboard_20260817100907A005.png',
    description = CASE
        WHEN INSTR(COALESCE(description, ''), '<!-- CASE_ASSET:WATER_CUSTOMER_SERVICE_REPORT -->') = 0
        THEN CONCAT(COALESCE(description, ''), '<!-- CASE_ASSET:WATER_CUSTOMER_SERVICE_REPORT --><hr><p><strong>项目截图</strong></p><p><img src="http://127.0.0.1:18081/profile/upload/2026/08/17/report-dashboard_20260817100907A005.png" alt="AI客服报表自动化项目截图1" style="max-width:100%;height:auto;"></p><p><img src="http://127.0.0.1:18081/profile/upload/2026/08/17/report-screen_20260817100907A006.png" alt="AI客服报表自动化项目截图2" style="max-width:100%;height:auto;"></p><!-- /CASE_ASSET:WATER_CUSTOMER_SERVICE_REPORT -->')
        ELSE description
    END,
    ext_json = JSON_SET(COALESCE(ext_json, JSON_OBJECT()), '$.coverUrl', 'http://127.0.0.1:18081/profile/upload/2026/08/17/report-dashboard_20260817100907A005.png', '$.imageUrls', JSON_ARRAY('http://127.0.0.1:18081/profile/upload/2026/08/17/report-dashboard_20260817100907A005.png', 'http://127.0.0.1:18081/profile/upload/2026/08/17/report-screen_20260817100907A006.png')),
    update_by = 'asset_import',
    update_time = CURRENT_TIMESTAMP
WHERE agent_code = 'WATER_CUSTOMER_SERVICE_REPORT' AND del_flag = '0';

-- WATER_DMA_LEAKAGE_CONTROL AI驱动的漏损控制体系
UPDATE biz_agent
SET cover_url = 'http://127.0.0.1:18081/profile/upload/2026/08/17/leakage-map_20260817100907A007.png',
    description = CASE
        WHEN INSTR(COALESCE(description, ''), '<!-- CASE_ASSET:WATER_DMA_LEAKAGE_CONTROL -->') = 0
        THEN CONCAT(COALESCE(description, ''), '<!-- CASE_ASSET:WATER_DMA_LEAKAGE_CONTROL --><hr><p><strong>项目截图</strong></p><p><img src="http://127.0.0.1:18081/profile/upload/2026/08/17/leakage-map_20260817100907A007.png" alt="AI驱动的漏损控制体系项目截图1" style="max-width:100%;height:auto;"></p><p><img src="http://127.0.0.1:18081/profile/upload/2026/08/17/leakage-analysis_20260817100907A008.png" alt="AI驱动的漏损控制体系项目截图2" style="max-width:100%;height:auto;"></p><!-- /CASE_ASSET:WATER_DMA_LEAKAGE_CONTROL -->')
        ELSE description
    END,
    ext_json = JSON_SET(COALESCE(ext_json, JSON_OBJECT()), '$.coverUrl', 'http://127.0.0.1:18081/profile/upload/2026/08/17/leakage-map_20260817100907A007.png', '$.imageUrls', JSON_ARRAY('http://127.0.0.1:18081/profile/upload/2026/08/17/leakage-map_20260817100907A007.png', 'http://127.0.0.1:18081/profile/upload/2026/08/17/leakage-analysis_20260817100907A008.png')),
    update_by = 'asset_import',
    update_time = CURRENT_TIMESTAMP
WHERE agent_code = 'WATER_DMA_LEAKAGE_CONTROL' AND del_flag = '0';

-- WATER_DRONE_PIPELINE_PATROL 吴江区供水管网无人机AI巡检应用
UPDATE biz_agent
SET cover_url = 'http://127.0.0.1:18081/profile/upload/2026/08/17/patrol-map_20260817100907A009.png',
    description = CASE
        WHEN INSTR(COALESCE(description, ''), '<!-- CASE_ASSET:WATER_DRONE_PIPELINE_PATROL -->') = 0
        THEN CONCAT(COALESCE(description, ''), '<!-- CASE_ASSET:WATER_DRONE_PIPELINE_PATROL --><hr><p><strong>项目截图</strong></p><p><img src="http://127.0.0.1:18081/profile/upload/2026/08/17/patrol-map_20260817100907A009.png" alt="吴江区供水管网无人机AI巡检应用项目截图1" style="max-width:100%;height:auto;"></p><p><img src="http://127.0.0.1:18081/profile/upload/2026/08/17/patrol-dashboard_20260817100907A010.png" alt="吴江区供水管网无人机AI巡检应用项目截图2" style="max-width:100%;height:auto;"></p><!-- /CASE_ASSET:WATER_DRONE_PIPELINE_PATROL -->')
        ELSE description
    END,
    ext_json = JSON_SET(COALESCE(ext_json, JSON_OBJECT()), '$.coverUrl', 'http://127.0.0.1:18081/profile/upload/2026/08/17/patrol-map_20260817100907A009.png', '$.imageUrls', JSON_ARRAY('http://127.0.0.1:18081/profile/upload/2026/08/17/patrol-map_20260817100907A009.png', 'http://127.0.0.1:18081/profile/upload/2026/08/17/patrol-dashboard_20260817100907A010.png')),
    update_by = 'asset_import',
    update_time = CURRENT_TIMESTAMP
WHERE agent_code = 'WATER_DRONE_PIPELINE_PATROL' AND del_flag = '0';

-- WATER_ALGAE_DETECTION AI藻类检测系统
UPDATE biz_agent
SET cover_url = 'http://127.0.0.1:18081/profile/upload/2026/08/17/ai-algae-result_20260817100907A011.png',
    description = CASE
        WHEN INSTR(COALESCE(description, ''), '<!-- CASE_ASSET:WATER_ALGAE_DETECTION -->') = 0
        THEN CONCAT(COALESCE(description, ''), '<!-- CASE_ASSET:WATER_ALGAE_DETECTION --><hr><p><strong>项目截图</strong></p><p><img src="http://127.0.0.1:18081/profile/upload/2026/08/17/ai-algae-result_20260817100907A011.png" alt="AI藻类检测系统项目截图1" style="max-width:100%;height:auto;"></p><p><img src="http://127.0.0.1:18081/profile/upload/2026/08/17/ai-algae-dashboard_20260817100907A012.png" alt="AI藻类检测系统项目截图2" style="max-width:100%;height:auto;"></p><!-- /CASE_ASSET:WATER_ALGAE_DETECTION -->')
        ELSE description
    END,
    ext_json = JSON_SET(COALESCE(ext_json, JSON_OBJECT()), '$.coverUrl', 'http://127.0.0.1:18081/profile/upload/2026/08/17/ai-algae-result_20260817100907A011.png', '$.imageUrls', JSON_ARRAY('http://127.0.0.1:18081/profile/upload/2026/08/17/ai-algae-result_20260817100907A011.png', 'http://127.0.0.1:18081/profile/upload/2026/08/17/ai-algae-dashboard_20260817100907A012.png')),
    update_by = 'asset_import',
    update_time = CURRENT_TIMESTAMP
WHERE agent_code = 'WATER_ALGAE_DETECTION' AND del_flag = '0';

-- WATER_LEAK_FORM_AUTOMATION 月度检漏表单自动化
UPDATE biz_agent
SET cover_url = 'http://127.0.0.1:18081/profile/upload/2026/08/17/form-automation-console_20260817100907A013.png',
    description = CASE
        WHEN INSTR(COALESCE(description, ''), '<!-- CASE_ASSET:WATER_LEAK_FORM_AUTOMATION -->') = 0
        THEN CONCAT(COALESCE(description, ''), '<!-- CASE_ASSET:WATER_LEAK_FORM_AUTOMATION --><hr><p><strong>项目截图</strong></p><p><img src="http://127.0.0.1:18081/profile/upload/2026/08/17/form-automation-console_20260817100907A013.png" alt="月度检漏表单自动化项目截图1" style="max-width:100%;height:auto;"></p><!-- /CASE_ASSET:WATER_LEAK_FORM_AUTOMATION -->')
        ELSE description
    END,
    ext_json = JSON_SET(COALESCE(ext_json, JSON_OBJECT()), '$.coverUrl', 'http://127.0.0.1:18081/profile/upload/2026/08/17/form-automation-console_20260817100907A013.png', '$.imageUrls', JSON_ARRAY('http://127.0.0.1:18081/profile/upload/2026/08/17/form-automation-console_20260817100907A013.png')),
    update_by = 'asset_import',
    update_time = CURRENT_TIMESTAMP
WHERE agent_code = 'WATER_LEAK_FORM_AUTOMATION' AND del_flag = '0';

-- WATER_PLANT_ENERGY_CONTROL AI节能技术在水厂的应用
UPDATE biz_agent
SET cover_url = 'http://127.0.0.1:18081/profile/upload/2026/08/17/control-architecture_20260817100907A014.png',
    description = CASE
        WHEN INSTR(COALESCE(description, ''), '<!-- CASE_ASSET:WATER_PLANT_ENERGY_CONTROL -->') = 0
        THEN CONCAT(COALESCE(description, ''), '<!-- CASE_ASSET:WATER_PLANT_ENERGY_CONTROL --><hr><p><strong>项目截图</strong></p><p><img src="http://127.0.0.1:18081/profile/upload/2026/08/17/control-architecture_20260817100907A014.png" alt="AI节能技术在水厂的应用项目截图1" style="max-width:100%;height:auto;"></p><p><img src="http://127.0.0.1:18081/profile/upload/2026/08/17/energy-chart_20260817100907A015.png" alt="AI节能技术在水厂的应用项目截图2" style="max-width:100%;height:auto;"></p><!-- /CASE_ASSET:WATER_PLANT_ENERGY_CONTROL -->')
        ELSE description
    END,
    ext_json = JSON_SET(COALESCE(ext_json, JSON_OBJECT()), '$.coverUrl', 'http://127.0.0.1:18081/profile/upload/2026/08/17/control-architecture_20260817100907A014.png', '$.imageUrls', JSON_ARRAY('http://127.0.0.1:18081/profile/upload/2026/08/17/control-architecture_20260817100907A014.png', 'http://127.0.0.1:18081/profile/upload/2026/08/17/energy-chart_20260817100907A015.png')),
    update_by = 'asset_import',
    update_time = CURRENT_TIMESTAMP
WHERE agent_code = 'WATER_PLANT_ENERGY_CONTROL' AND del_flag = '0';

-- WATER_TEST_REPORT_AUDIT AI全流程智能检测报告审核系统
UPDATE biz_agent
SET cover_url = 'http://127.0.0.1:18081/profile/upload/2026/08/17/report-review-screen_20260817100907A016.png',
    description = CASE
        WHEN INSTR(COALESCE(description, ''), '<!-- CASE_ASSET:WATER_TEST_REPORT_AUDIT -->') = 0
        THEN CONCAT(COALESCE(description, ''), '<!-- CASE_ASSET:WATER_TEST_REPORT_AUDIT --><hr><p><strong>项目截图</strong></p><p><img src="http://127.0.0.1:18081/profile/upload/2026/08/17/report-review-screen_20260817100907A016.png" alt="AI全流程智能检测报告审核系统项目截图1" style="max-width:100%;height:auto;"></p><!-- /CASE_ASSET:WATER_TEST_REPORT_AUDIT -->')
        ELSE description
    END,
    ext_json = JSON_SET(COALESCE(ext_json, JSON_OBJECT()), '$.coverUrl', 'http://127.0.0.1:18081/profile/upload/2026/08/17/report-review-screen_20260817100907A016.png', '$.imageUrls', JSON_ARRAY('http://127.0.0.1:18081/profile/upload/2026/08/17/report-review-screen_20260817100907A016.png')),
    update_by = 'asset_import',
    update_time = CURRENT_TIMESTAMP
WHERE agent_code = 'WATER_TEST_REPORT_AUDIT' AND del_flag = '0';

-- WATER_METER_VISION_RECOGNITION AI抄表智能识别模型
UPDATE biz_agent
SET cover_url = 'http://127.0.0.1:18081/profile/upload/2026/08/17/meter-recognition-a_20260817100907A017.png',
    description = CASE
        WHEN INSTR(COALESCE(description, ''), '<!-- CASE_ASSET:WATER_METER_VISION_RECOGNITION -->') = 0
        THEN CONCAT(COALESCE(description, ''), '<!-- CASE_ASSET:WATER_METER_VISION_RECOGNITION --><hr><p><strong>项目截图</strong></p><p><img src="http://127.0.0.1:18081/profile/upload/2026/08/17/meter-recognition-a_20260817100907A017.png" alt="AI抄表智能识别模型项目截图1" style="max-width:100%;height:auto;"></p><p><img src="http://127.0.0.1:18081/profile/upload/2026/08/17/meter-recognition-b_20260817100907A018.png" alt="AI抄表智能识别模型项目截图2" style="max-width:100%;height:auto;"></p><!-- /CASE_ASSET:WATER_METER_VISION_RECOGNITION -->')
        ELSE description
    END,
    ext_json = JSON_SET(COALESCE(ext_json, JSON_OBJECT()), '$.coverUrl', 'http://127.0.0.1:18081/profile/upload/2026/08/17/meter-recognition-a_20260817100907A017.png', '$.imageUrls', JSON_ARRAY('http://127.0.0.1:18081/profile/upload/2026/08/17/meter-recognition-a_20260817100907A017.png', 'http://127.0.0.1:18081/profile/upload/2026/08/17/meter-recognition-b_20260817100907A018.png')),
    update_by = 'asset_import',
    update_time = CURRENT_TIMESTAMP
WHERE agent_code = 'WATER_METER_VISION_RECOGNITION' AND del_flag = '0';

-- WATER_SECONDARY_SUPPLY_FAULT AI智慧化停水故障诊断
UPDATE biz_agent
SET cover_url = 'http://127.0.0.1:18081/profile/upload/2026/08/17/fault-table_20260817100907A019.png',
    description = CASE
        WHEN INSTR(COALESCE(description, ''), '<!-- CASE_ASSET:WATER_SECONDARY_SUPPLY_FAULT -->') = 0
        THEN CONCAT(COALESCE(description, ''), '<!-- CASE_ASSET:WATER_SECONDARY_SUPPLY_FAULT --><hr><p><strong>项目截图</strong></p><p><img src="http://127.0.0.1:18081/profile/upload/2026/08/17/fault-table_20260817100907A019.png" alt="AI智慧化停水故障诊断项目截图1" style="max-width:100%;height:auto;"></p><p><img src="http://127.0.0.1:18081/profile/upload/2026/08/17/diagnostic-flow_20260817100907A020.png" alt="AI智慧化停水故障诊断项目截图2" style="max-width:100%;height:auto;"></p><!-- /CASE_ASSET:WATER_SECONDARY_SUPPLY_FAULT -->')
        ELSE description
    END,
    ext_json = JSON_SET(COALESCE(ext_json, JSON_OBJECT()), '$.coverUrl', 'http://127.0.0.1:18081/profile/upload/2026/08/17/fault-table_20260817100907A019.png', '$.imageUrls', JSON_ARRAY('http://127.0.0.1:18081/profile/upload/2026/08/17/fault-table_20260817100907A019.png', 'http://127.0.0.1:18081/profile/upload/2026/08/17/diagnostic-flow_20260817100907A020.png')),
    update_by = 'asset_import',
    update_time = CURRENT_TIMESTAMP
WHERE agent_code = 'WATER_SECONDARY_SUPPLY_FAULT' AND del_flag = '0';

-- 写回校验：应返回 11 条记录，cover_url 不应为空。
SELECT agent_code, agent_name, publish_status, cover_url, JSON_EXTRACT(ext_json, '$.imageUrls') AS image_urls
FROM biz_agent WHERE agent_code IN ('WATER_AI_CUSTOMER_SERVICE', 'WATER_CUSTOMER_WATER_ALERT', 'WATER_CUSTOMER_SERVICE_REPORT', 'WATER_DMA_LEAKAGE_CONTROL', 'WATER_DRONE_PIPELINE_PATROL', 'WATER_ALGAE_DETECTION', 'WATER_LEAK_FORM_AUTOMATION', 'WATER_PLANT_ENERGY_CONTROL', 'WATER_TEST_REPORT_AUDIT', 'WATER_METER_VISION_RECOGNITION', 'WATER_SECONDARY_SUPPLY_FAULT') ORDER BY sort_no;

-- 确认图片 URL 和校验结果无误后，在同一连接执行：
-- COMMIT;
-- 如发现问题，执行：
-- ROLLBACK;
