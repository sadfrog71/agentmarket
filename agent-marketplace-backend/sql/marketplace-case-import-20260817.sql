-- 水务 AI 创新项目成果展：智能体市场案例导入脚本
-- 生成日期：2026-08-17
-- 目标库：当前项目 marketplace.sql 使用的业务库，默认名为 ry-vue
--
-- 使用说明：
-- 1. 本脚本只写入 biz_agent 和 biz_agent_detail_item，不上传图片，不修改字典和菜单。
-- 2. 11 条记录全部按草稿导入：publish_status='0'，recommend_flag='N'，rating/deploy_count/hot_score=0。
-- 3. 认证等级暂填 L1 作为后台合法占位值，remark 中标记“待核验”，不代表已完成认证。
-- 4. 执行前先查看“冲突检查”结果。结果必须为 0；若不为 0，本脚本不会写入任何案例。
-- 5. 脚本启动事务，底部默认不提交。确认校验结果后，在同一数据库连接中执行 COMMIT；否则执行 ROLLBACK。
-- 6. 本脚本是“只新增、不覆盖”的首轮导入脚本；若需要重导已存在的业务编码，请先人工确认并单独处理。

USE `ry-vue`;
SET NAMES utf8mb4;

DROP TEMPORARY TABLE IF EXISTS tmp_water_case_agent_import;
CREATE TEMPORARY TABLE tmp_water_case_agent_import (
    agent_code       varchar(64)  NOT NULL,
    agent_name       varchar(100) NOT NULL,
    category_code    varchar(64)  NOT NULL,
    icon_code        varchar(64)  NOT NULL,
    provider_name    varchar(150) NOT NULL,
    summary          varchar(1000) NOT NULL,
    description      longtext      NOT NULL,
    price_text       varchar(100)  NOT NULL,
    cert_level       varchar(20)   NOT NULL,
    delivery_cycle   varchar(100)  NOT NULL,
    service_mode     varchar(100)  NOT NULL,
    slug             varchar(120)  NOT NULL,
    sort_no          int           NOT NULL,
    source_slide     int           NOT NULL,
    remark           varchar(500)  NOT NULL,
    ext_json         json          DEFAULT NULL,
    PRIMARY KEY (agent_code),
    UNIQUE KEY uk_tmp_water_case_slug (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO tmp_water_case_agent_import
(agent_code, agent_name, category_code, icon_code, provider_name, summary, description,
 price_text, cert_level, delivery_cycle, service_mode, slug, sort_no, source_slide, remark, ext_json)
VALUES
('WATER_AI_CUSTOMER_SERVICE', 'AI智能客服', 'customer', 'service', '吴江华衍水务',
 '基于水务知识库与 AI 大模型承接简单呼入咨询，通过 IVR 导航和人工转接处理复杂诉求，形成智能预处理与人工服务协同的服务闭环。',
 '<h3>AI 智能客服</h3><p>面向水务呼入服务场景，构建“智能应答—业务办理—IVR 导航转接—合规管控—数据复盘”的服务流程。系统承接常见咨询，复杂诉求保留人工坐席转接，帮助形成可追踪的人机协同闭环。</p><p><strong>说明：</strong>服务承接率、解决率和排队时长等指标来自成果整理稿，正式上线口径待核验。</p>',
 '面议（待核定）', 'L1', '待评估', '待确认', 'ai-customer-service', 1, 10,
 '水务AI创新项目成果展/腾讯表格整理稿；草稿导入，认证、部署、商业字段及指标待核验。',
 JSON_OBJECT('sourceSlide', 10, 'contentStatus', 'draft', 'assetDir', '10-ai-customer-service')),
('WATER_CUSTOMER_WATER_ALERT', '客户异常水量预警', 'customer', 'alert', '吴江华衍水务',
 '基于 43 万户智能远传水表数据建立用水基线，识别夜间小流量、水量突增突减等异常并及时提醒，辅助用户查漏和减少水费争议。',
 '<h3>客户异常水量预警</h3><p>依托智能远传水表数据建立用户用水基线，动态识别夜间持续小流量、水量突增突减等异常特征，并向用户或客服人员推送提醒。该能力用于辅助查漏、减少水费争议和家庭水资源浪费。</p><p><strong>说明：</strong>异常识别准确率和监测周期需结合线上样本、异常类别与统计周期统一口径。</p>',
 '面议（待核定）', 'L1', '待评估', '待确认', 'customer-water-alert', 2, 9,
 '水务AI创新项目成果展/腾讯表格整理稿；草稿导入，认证、部署、商业字段及指标待核验。',
 JSON_OBJECT('sourceSlide', 9, 'contentStatus', 'draft', 'assetDir', '09-water-alert')),
('WATER_CUSTOMER_SERVICE_REPORT', 'AI客服报表自动化', 'management', 'report', '吴江华衍水务',
 '通过自然语言问数和报表自动生成，将客服运营报表从人工小时级统计压缩到秒级，为管理层提供及时、可追溯的数据依据。',
 '<h3>AI 客服报表自动化</h3><p>针对客服人工统计报表重复、耗时和响应滞后的问题，搭建自然语言智能问数和报表自动生成能力。系统识别管理人员的口语化问题，联动数据仓库汇总客服运营数据，并按模板生成趋势、结构和对比报表。</p><p><strong>说明：</strong>数据源、指标口径、权限范围和实际生成时效待实施时确认。</p>',
 '面议（待核定）', 'L1', '待评估', '待确认', 'customer-service-report', 3, 11,
 '水务AI创新项目成果展/腾讯表格整理稿；草稿导入，认证、部署、商业字段及指标待核验。',
 JSON_OBJECT('sourceSlide', 11, 'contentStatus', 'draft', 'assetDir', '11-customer-report')),
('WATER_DMA_LEAKAGE_CONTROL', 'AI驱动的漏损控制体系', 'network', 'target', '吴江华衍水务',
 '融合 GIS、SCADA 和营收等多源数据，构建计量、物理和管理漏损识别能力，实现异常预警、漏点定位、处置闭环和节水效益测算。',
 '<h3>AI 驱动的漏损控制体系</h3><p>融合 GIS、SCADA、营收等多源数据，构建“数据驱动—智能诊断—精准处置—反馈优化”的漏损管控闭环。系统支持分区流量基线、异常预警、噪声与流量联动定位、夜间最小流量分析和工单闭环。</p><p><strong>说明：</strong>本条使用新的业务编码，不覆盖系统初始化脚本中的 DMA_LEAKAGE 示例记录。</p>',
 '面议（待核定）', 'L1', '待评估', '待确认', 'dma-leakage-control', 4, 4,
 '水务AI创新项目成果展/腾讯表格整理稿；草稿导入，认证、部署、商业字段及指标待核验。',
 JSON_OBJECT('sourceSlide', 4, 'contentStatus', 'draft', 'assetDir', '04-dma-leakage')),
('WATER_DRONE_PIPELINE_PATROL', '吴江区供水管网无人机AI巡检应用', 'network', 'drone', '吴江华衍水务',
 '通过无人机机场、固定航线和计算机视觉识别施工干扰等管网风险，形成识别、预警、派单、处置、销单闭环，提升主动巡检能力。',
 '<h3>供水管网无人机 AI 巡检</h3><p>项目配置无人机机场、无人机和巡检航线，支持自动起降、巡检、充电和影像回传。依托计算机视觉和低空管网风险识别能力，识别第三方施工、人员攀爬等隐患，并通过智能工单形成处置闭环。</p><p><strong>说明：</strong>航线覆盖、飞行里程和隐患数量按成果材料整理，当前导入为草稿口径。</p>',
 '面议（待核定）', 'L1', '待评估', '待确认', 'drone-pipeline-patrol', 5, 3,
 '水务AI创新项目成果展/腾讯表格整理稿；草稿导入，认证、部署、商业字段及指标待核验。',
 JSON_OBJECT('sourceSlide', 3, 'contentStatus', 'draft', 'assetDir', '03-drone-patrol')),
('WATER_ALGAE_DETECTION', 'AI藻类检测系统', 'quality', 'water', '吴江华衍水务',
 '基于 CNN 图像识别模型自动识别藻类镜检图像，完成藻种分类、定量计数和报告生成，辅助提升藻情监测时效性和一致性。',
 '<h3>AI 藻类检测系统</h3><p>系统基于 CNN 深度学习算法，对藻类镜检图像进行识别，自动提取藻类形态、纹理和结构特征，实现藻种分类与定量计数，并支持检测报告生成和导出。</p><p><strong>说明：</strong>整理材料中存在“5 种高匹配度藻类”和“识别 52 种”两种口径，本条不将 52 种表述为已稳定支持范围。</p>',
 '面议（待核定）', 'L1', '待评估', '待确认', 'algae-detection', 6, 1,
 '水务AI创新项目成果展/腾讯表格整理稿；草稿导入，认证、部署、商业字段及指标待核验。',
 JSON_OBJECT('sourceSlide', 1, 'contentStatus', 'draft', 'assetDir', '01-ai-algae')),
('WATER_LEAK_FORM_AUTOMATION', '月度检漏表单自动化', 'network', 'form', '吴江华衍水务',
 '自动解析现场采集的非结构化检漏数据，一键填充月度报漏补漏表单并进行逻辑校验，将重复填报工作从人工小时级压缩到约 30 分钟。',
 '<h3>月度检漏表单自动化</h3><p>系统自动解析现场采集的非结构化数据，提取关键业务字段并按标准模板填充月度报漏补漏表单，支持批量导入和逻辑校验，减少重复录入和人为错误。</p><p><strong>说明：</strong>表单处理时长、人力投入和准确率需补充统计基线及校验范围。</p>',
 '面议（待核定）', 'L1', '待评估', '待确认', 'leak-form-automation', 7, 7,
 '水务AI创新项目成果展/腾讯表格整理稿；草稿导入，认证、部署、商业字段及指标待核验。',
 JSON_OBJECT('sourceSlide', 7, 'contentStatus', 'draft', 'assetDir', '07-leak-form')),
('WATER_PLANT_ENERGY_CONTROL', 'AI节能技术在水厂的应用', 'production', 'energy', '吴江华衍水务',
 '结合供水需求、管网压力和电价信号优化泵组运行与错峰供水，辅助降低取水和配水泵房电耗，并保留人工确认和控制边界。',
 '<h3>AI 节能技术在水厂的应用</h3><p>项目面向水厂泵组运行，综合感知水量需求、管网压力和电价信号，辅助生成取配水平衡与错峰供水方案，通过低谷期增蓄、高峰期释蓄平滑供水负荷。</p><p><strong>说明：</strong>成果材料同时出现“AI 节能技术”和“AI 水量预测”标题，本条先按节能场景导入，预测精度与节能指标需拆分核验。AI 生成方案不替代 PLC 和授权人员的控制责任。</p>',
 '面议（待核定）', 'L1', '待评估', '待确认', 'water-plant-energy-control', 8, 6,
 '水务AI创新项目成果展/腾讯表格整理稿；草稿导入，认证、部署、商业字段及指标待核验。',
 JSON_OBJECT('sourceSlide', 6, 'contentStatus', 'draft', 'assetDir', '06-water-plant-energy')),
('WATER_TEST_REPORT_AUDIT', 'AI全流程智能检测报告审核系统', 'quality', 'audit', '吴江华衍水务',
 '融合 RPA、AI 和大模型完成水质检测报告自动审核，匹配复杂质控规则并保留全流程痕迹，辅助提升实验室审核效率和合规性。',
 '<h3>AI 全流程智能检测报告审核</h3><p>通过 RPA、AI 和大模型技术实现检测报告自动化智能审核。RPA 负责重复操作和数据抓取，大模型辅助理解复杂文本逻辑，规则引擎承载可配置质控标准，并通过全流程留痕支持追溯和审计。</p><p><strong>边界：</strong>系统定位为审核辅助和风险提示工具，最终报告责任及 CMA 合规责任仍由实验室人员承担。</p>',
 '面议（待核定）', 'L1', '待评估', '待确认', 'water-test-report-audit', 9, 2,
 '水务AI创新项目成果展/腾讯表格整理稿；草稿导入，认证、部署、商业字段及指标待核验。',
 JSON_OBJECT('sourceSlide', 2, 'contentStatus', 'draft', 'assetDir', '02-report-audit')),
('WATER_METER_VISION_RECOGNITION', 'AI抄表智能识别模型', 'customer', 'scan', '吴江华衍水务',
 '基于 CNN 和图像预处理技术自动读取机械水表，结合异常预警、合规校验和样本回流降低错抄漏抄，辅助实现抄表复核少人化。',
 '<h3>AI 抄表智能识别模型</h3><p>面向机械水表抄表和异常复核任务，构建基于 CNN 和多维图像预处理技术的智能视觉抄表能力，提供图像增强、表盘定位、读数识别、异常预警、合规校验和样本回流。</p><p><strong>说明：</strong>单表识别性能、整体复核效率和人力效果属于不同环节，相关指标需分别核验，不能合并为单一准确率。</p>',
 '面议（待核定）', 'L1', '待评估', '待确认', 'meter-vision-recognition', 10, 8,
 '水务AI创新项目成果展/腾讯表格整理稿；草稿导入，认证、部署、商业字段及指标待核验。',
 JSON_OBJECT('sourceSlide', 8, 'contentStatus', 'draft', 'assetDir', '08-meter-recognition')),
('WATER_SECONDARY_SUPPLY_FAULT', 'AI智慧化停水故障诊断', 'production', 'warning', '吴江华衍水务',
 '融合二次供水泵房运行数据与视频 AI，识别低压停水诱因，生成诊断报告并推送运维人员，辅助缩短故障定位和处置响应时间。',
 '<h3>AI 智慧化停水故障诊断</h3><p>依托二次供水泵房运行数据和视频监控 AI 图像分析，构建故障诊断流程。当出现水压偏低等异常时，系统联动调取运行参数，融合分析停水诱因，生成诊断报告并推送运维人员。</p><p><strong>边界：</strong>该能力用于辅助运维判断，具体处置仍由授权人员执行；预警提前量、诊断时延、推送时延和处置效果需拆分核验。</p>',
 '面议（待核定）', 'L1', '待评估', '待确认', 'secondary-supply-fault', 11, 5,
 '水务AI创新项目成果展/腾讯表格整理稿；草稿导入，认证、部署、商业字段及指标待核验。',
 JSON_OBJECT('sourceSlide', 5, 'contentStatus', 'draft', 'assetDir', '05-secondary-supply'));

-- 冲突检查：以下结果必须为 0。
-- 若存在已占用的业务编码或详情标识，本脚本后续导入语句会全部跳过，不覆盖已有内容。
SELECT a.agent_id, a.agent_code, a.agent_name, a.slug
FROM biz_agent a
JOIN tmp_water_case_agent_import t
  ON a.agent_code = t.agent_code OR a.slug = t.slug;

SELECT COUNT(*) AS conflict_count
FROM biz_agent a
JOIN tmp_water_case_agent_import t
  ON a.agent_code = t.agent_code OR a.slug = t.slug;

SELECT @water_case_conflict_count := COUNT(*)
FROM biz_agent a
JOIN tmp_water_case_agent_import t
  ON a.agent_code = t.agent_code OR a.slug = t.slug;

DROP TEMPORARY TABLE IF EXISTS tmp_water_case_detail_import;
CREATE TEMPORARY TABLE tmp_water_case_detail_import (
    agent_code  varchar(64)  NOT NULL,
    item_type   varchar(32)  NOT NULL,
    title       varchar(200) NOT NULL,
    value_text  varchar(500) NOT NULL,
    content     text         NOT NULL,
    icon_code   varchar(64)  NOT NULL,
    sort_no     int          NOT NULL,
    PRIMARY KEY (agent_code, item_type, sort_no),
    KEY idx_tmp_water_case_detail_agent (agent_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO tmp_water_case_detail_import
(agent_code, item_type, title, value_text, content, icon_code, sort_no)
VALUES
-- 01 AI智能客服
('WATER_AI_CUSTOMER_SERVICE','FEATURE','水务知识库问答与意图识别','','理解常见用水咨询并给出标准化答复。','book',1),
('WATER_AI_CUSTOMER_SERVICE','FEATURE','IVR 导航与人机分流','','简单问题由机器人承接，复杂诉求转接人工坐席。','branch',2),
('WATER_AI_CUSTOMER_SERVICE','FEATURE','合规管控与运营复盘','','记录服务过程并沉淀服务热点，用于持续优化知识库和流程。','report',3),
('WATER_AI_CUSTOMER_SERVICE','METRIC','在线服务','7×24 小时','成果整理稿口径，正式在线服务范围和运行记录待核验。','clock',1),
('WATER_AI_CUSTOMER_SERVICE','METRIC','简单咨询承接率','60%+','预计指标，需补充统计周期和分母口径。','chart',2),
('WATER_AI_CUSTOMER_SERVICE','METRIC','机器人自主解决率','≥50%','需确认“自主解决”的统计口径和人工转接规则。','check',3),
('WATER_AI_CUSTOMER_SERVICE','CASE','吴江华衍水务','呼入服务场景','项目成果展/场景汇总材料口径，正式上线范围和运营数据待补。','case',1),
('WATER_AI_CUSTOMER_SERVICE','PRICE_FEATURE','知识库与话术配置','待确认','需补充知识库来源、更新责任、话术和人工转接规则。','check',1),
('WATER_AI_CUSTOMER_SERVICE','COMPATIBILITY','接入方式','IVR/呼叫平台待确认','具体厂商、接口和部署方式待补。','link',1),
-- 02 客户异常水量预警
('WATER_CUSTOMER_WATER_ALERT','FEATURE','用水基线建模','43 万户智能远传水表','基于历史和实时用水数据刻画用户正常用水模式。','database',1),
('WATER_CUSTOMER_WATER_ALERT','FEATURE','异常特征识别','夜间小流量/突增突减','自动识别典型异常用水特征。','alert',2),
('WATER_CUSTOMER_WATER_ALERT','FEATURE','异常消息推送','实时提醒','将异常信息及时触达用户或客服人员。','bell',3),
('WATER_CUSTOMER_WATER_ALERT','METRIC','异常发现周期','60 天→每日','成果整理稿口径，需补充准确统计基线。','clock',1),
('WATER_CUSTOMER_WATER_ALERT','METRIC','异常识别准确率','≥90%','需确认样本量、异常类别和评估周期。','check',2),
('WATER_CUSTOMER_WATER_ALERT','CASE','吴江华衍水务','43 万户远传表数据应用','内部成果材料口径，正式部署范围待补。','case',1),
('WATER_CUSTOMER_WATER_ALERT','PRICE_FEATURE','提醒策略','待确认','需确定提醒对象、触达渠道、频率和人工复核规则。','check',1),
('WATER_CUSTOMER_WATER_ALERT','COMPATIBILITY','数据来源','智能远传水表','接入协议、数据频率和历史数据量待确认。','database',1),
-- 03 AI客服报表自动化
('WATER_CUSTOMER_SERVICE_REPORT','FEATURE','自然语言问数','','将口语化问题解析为数据查询请求。','search',1),
('WATER_CUSTOMER_SERVICE_REPORT','FEATURE','数仓联动与数据汇总','','汇总客服运营数据并返回可追溯结果。','database',2),
('WATER_CUSTOMER_SERVICE_REPORT','FEATURE','报表模板与可视化渲染','折线/柱状/饼图','按问题和模板生成可视化报表。','chart',3),
('WATER_CUSTOMER_SERVICE_REPORT','METRIC','报表生成时效','小时级→秒级','成果整理稿目标口径，需补充具体基线和统计周期。','clock',1),
('WATER_CUSTOMER_SERVICE_REPORT','METRIC','管理响应','实时依据','管理者可通过自然语言提问获取客服运营数据。','report',2),
('WATER_CUSTOMER_SERVICE_REPORT','CASE','吴江华衍水务','客服运营数据场景','内部成果材料口径，需补充实际报表和上线范围。','case',1),
('WATER_CUSTOMER_SERVICE_REPORT','PRICE_FEATURE','指标与权限配置','待确认','需补充指标字典、数据权限、审计和追溯方案。','check',1),
('WATER_CUSTOMER_SERVICE_REPORT','COMPATIBILITY','数据接口','数仓/客服系统待确认','需补充数据仓库、权限、指标治理和审计方案。','database',1),
-- 04 AI驱动的漏损控制体系
('WATER_DMA_LEAKAGE_CONTROL','FEATURE','分区流量基线与异常预警','计量漏损','学习 DMA 正常流量模式并识别异常。','chart',1),
('WATER_DMA_LEAKAGE_CONTROL','FEATURE','噪声与流量联动定位','物理漏损','联动声学测漏与流量分析，缩小疑似漏点范围。','target',2),
('WATER_DMA_LEAKAGE_CONTROL','FEATURE','MNF 夜间最小流量分析','市政/小区拆分','通过基线预测和夜间流量拆分辅助判断漏损位置。','chart',3),
('WATER_DMA_LEAKAGE_CONTROL','FEATURE','工单闭环与效益测算','监测—预警—定位—修复','支持处置优先级和节水效益分析。','workflow',4),
('WATER_DMA_LEAKAGE_CONTROL','METRIC','已定位较大漏点','31 处','腾讯表格和 PPT 均出现的内部成果口径。','target',1),
('WATER_DMA_LEAKAGE_CONTROL','METRIC','瞬时减漏','920m³/h','需补充统计时间、点位和测算方法。','water',2),
('WATER_DMA_LEAKAGE_CONTROL','METRIC','年均节水','超 870 万吨','需统一“年均节水”与“累计节水”的口径。','water',3),
('WATER_DMA_LEAKAGE_CONTROL','CASE','吴江华衍水务','DMA 漏损管控','内部成果材料口径，不与已有 DMA_LEAKAGE 示例记录的数据混用。','case',1),
('WATER_DMA_LEAKAGE_CONTROL','PRICE_FEATURE','闭环实施范围','待确认','需确认 GIS、SCADA、营收系统、工单和人工处置边界。','check',1),
('WATER_DMA_LEAKAGE_CONTROL','COMPATIBILITY','数据接口','GIS/SCADA/营收系统','接入方式、权限和数据保留周期待确认。','database',1),
('WATER_DMA_LEAKAGE_CONTROL','COMPATIBILITY','分区规模','DMA 分区待确认','需补充当前覆盖分区数和数据完整性。','map',2),
-- 05 无人机AI巡检
('WATER_DRONE_PIPELINE_PATROL','FEATURE','无人机自动巡检','4 个机场/4 台无人机/24 条航线','支持自动起降、巡检、充电和影像回传。','drone',1),
('WATER_DRONE_PIPELINE_PATROL','FEATURE','低空视觉风险识别','施工/攀爬等隐患','识别管网周边施工干扰和安全风险。','scan',2),
('WATER_DRONE_PIPELINE_PATROL','FEATURE','智能工单闭环','识别—预警—派单—处置—销单','将识别结果转为可跟踪的处置任务。','workflow',3),
('WATER_DRONE_PIPELINE_PATROL','METRIC','覆盖管线','约 200 公里','成果整理稿口径，需确认当前有效范围。','map',1),
('WATER_DRONE_PIPELINE_PATROL','METRIC','累计飞行里程','8.29 万公里','统计区间为 2024 年 12 月—2026 年 5 月，需核对原始飞行记录。','route',2),
('WATER_DRONE_PIPELINE_PATROL','METRIC','识别隐患','155 处','成果整理稿口径，需确认隐患分类。','alert',3),
('WATER_DRONE_PIPELINE_PATROL','METRIC','重大事故','0 起','需确认“重大安全事故”的定义和统计范围。','shield',4),
('WATER_DRONE_PIPELINE_PATROL','CASE','吴江区供水管网','无人机 AI 巡检应用','内部项目成果材料口径。','case',1),
('WATER_DRONE_PIPELINE_PATROL','PRICE_FEATURE','巡检运营','待确认','需确认航线维护、影像复核、告警分派和设备运维责任。','check',1),
('WATER_DRONE_PIPELINE_PATROL','COMPATIBILITY','部署方式','服务器本地化部署','成果材料提到本地化算法部署，服务器和系统环境待补。','server',1),
('WATER_DRONE_PIPELINE_PATROL','COMPATIBILITY','数据类型','巡检影像/工单数据','具体回传协议、存储和权限待确认。','database',2),
-- 06 AI藻类检测
('WATER_ALGAE_DETECTION','FEATURE','镜检图像识别','CNN 模型','自动提取藻类形态、纹理和结构特征。','scan',1),
('WATER_ALGAE_DETECTION','FEATURE','藻种分类与定量计数','','支持上传图像后进行识别和计数。','chart',2),
('WATER_ALGAE_DETECTION','FEATURE','检测报告生成','','将检测结果标准化输出并支持导出。','report',3),
('WATER_ALGAE_DETECTION','METRIC','识别与计数效率','提升约 20%','需补充人工基线、样本量和评估周期。','chart',1),
('WATER_ALGAE_DETECTION','METRIC','培训周期','压缩约 50%','需明确对比岗位和培训周期定义。','clock',2),
('WATER_ALGAE_DETECTION','METRIC','藻种覆盖','52 种（待核口径）','材料写有识别 52 种，需确认是样本覆盖、可识别范围还是正式支持范围。','water',3),
('WATER_ALGAE_DETECTION','METRIC','优势藻准确率','95%（待核）','需补充优势藻清单、测试集和模型版本。','check',4),
('WATER_ALGAE_DETECTION','CASE','吴江华衍水务','水生态监测场景','内部成果材料口径，正式应用范围待补。','case',1),
('WATER_ALGAE_DETECTION','PRICE_FEATURE','模型服务范围','待确认','需确认高匹配度藻类范围、样本标注和模型更新责任。','check',1),
('WATER_ALGAE_DETECTION','COMPATIBILITY','输入数据','藻类镜检图像','图片格式、分辨率和样本标注规则待确认。','image',1),
-- 07 月度检漏表单自动化
('WATER_LEAK_FORM_AUTOMATION','FEATURE','非结构化数据解析','','从现场采集数据中提取表单字段。','scan',1),
('WATER_LEAK_FORM_AUTOMATION','FEATURE','表单自动填充','一键生成','按标准化模板批量填充业务字段。','form',2),
('WATER_LEAK_FORM_AUTOMATION','FEATURE','多源批量导入','','适配不同采集终端和数据格式。','upload',3),
('WATER_LEAK_FORM_AUTOMATION','FEATURE','逻辑校验防错','','自动筛查异常数据并提示人工复核。','check',4),
('WATER_LEAK_FORM_AUTOMATION','METRIC','月度表单处理时长','24 小时→约 30 分钟','成果整理稿口径，需核对表单数量和统计周期。','clock',1),
('WATER_LEAK_FORM_AUTOMATION','METRIC','人力投入','降低 95%+','需明确按工时还是人员投入计算。','chart',2),
('WATER_LEAK_FORM_AUTOMATION','METRIC','填报准确率','100%（待核）','需补充校验范围和异常样本统计方法。','check',3),
('WATER_LEAK_FORM_AUTOMATION','METRIC','单张处理时长','10 秒以内（待核）','需确认适用的表单类型和硬件环境。','clock',4),
('WATER_LEAK_FORM_AUTOMATION','CASE','吴江华衍水务','月度检漏表单场景','内部成果材料口径。','case',1),
('WATER_LEAK_FORM_AUTOMATION','PRICE_FEATURE','表单模板适配','待确认','需确认模板版本、字段映射和人工复核节点。','check',1),
('WATER_LEAK_FORM_AUTOMATION','COMPATIBILITY','输入格式','多源现场采集数据','具体格式和批量规模待补。','upload',1),
-- 08 AI节能技术在水厂的应用
('WATER_PLANT_ENERGY_CONTROL','FEATURE','泵组自学习控制','','识别泵组高效运行区间并辅助生成运行方案。','energy',1),
('WATER_PLANT_ENERGY_CONTROL','FEATURE','秒级动态寻优','','综合水量需求、管网压力和电价信号进行方案计算。','chart',2),
('WATER_PLANT_ENERGY_CONTROL','FEATURE','AI 错峰供水控制','低谷增蓄/高峰释蓄','联动取配水泵房，平滑供水负荷。','energy',3),
('WATER_PLANT_ENERGY_CONTROL','METRIC','取水泵房千吨水电耗','节约 0.5%–1%（预计）','预计指标，需补充基线、计算周期和工况范围。','energy',1),
('WATER_PLANT_ENERGY_CONTROL','METRIC','配水泵房千吨水电耗','节约 0.5%–1%（预计）','预计指标，需补充基线、计算周期和工况范围。','energy',2),
('WATER_PLANT_ENERGY_CONTROL','METRIC','综合节能效益','2%–3%（预计）','需明确基线、季节、电价假设和测算方法。','chart',3),
('WATER_PLANT_ENERGY_CONTROL','METRIC','预测精度','≤3%（标题口径待确认）','PPT 中出现的预测指标，与节能课题需先拆分核验。','check',4),
('WATER_PLANT_ENERGY_CONTROL','CASE','吴江华衍水务','水厂节能控制场景','内部项目/预期效益材料口径。','case',1),
('WATER_PLANT_ENERGY_CONTROL','PRICE_FEATURE','控制边界','辅助决策或闭环控制待确认','需明确 AI、PLC、SCADA 和授权人员的责任边界。','shield',1),
('WATER_PLANT_ENERGY_CONTROL','COMPATIBILITY','数据来源','水量需求/压力/电价/泵组数据','接口、频率和控制权限待确认。','database',1),
-- 09 AI全流程智能检测报告审核
('WATER_TEST_REPORT_AUDIT','FEATURE','RPA 流程自动化','','自动抓取和录入重复性数据。','workflow',1),
('WATER_TEST_REPORT_AUDIT','FEATURE','大模型规则校验','','辅助理解报告文本并识别合规风险。','audit',2),
('WATER_TEST_REPORT_AUDIT','FEATURE','可扩展规则引擎','','支持质控规则配置和标准更新。','rule',3),
('WATER_TEST_REPORT_AUDIT','FEATURE','全流程溯源','','记录审核过程、错误和处理结果。','trace',4),
('WATER_TEST_REPORT_AUDIT','METRIC','单份审核时间','5 分钟→3 分钟内','审核效率提升约 40%，需补充样本范围。','clock',1),
('WATER_TEST_REPORT_AUDIT','METRIC','关键项错误漏检率','<0.5%（待核）','需说明评估集、关键项定义和复核方式。','check',2),
('WATER_TEST_REPORT_AUDIT','METRIC','年处理量','10,000+ 份（待核）','材料写为业务应用/能力目标，需确认实际年度量。','report',3),
('WATER_TEST_REPORT_AUDIT','CASE','吴江华衍水务','水质检测报告审核场景','内部实验室应用材料口径。','case',1),
('WATER_TEST_REPORT_AUDIT','PRICE_FEATURE','审核责任边界','人工复核保留','系统提供审核辅助和风险提示，最终报告责任由实验室人员承担。','shield',1),
('WATER_TEST_REPORT_AUDIT','COMPATIBILITY','合规约束','CMA 资质认定要求','需补充具体规则库、审计和人工复核要求。','audit',1),
-- 10 AI抄表智能识别模型
('WATER_METER_VISION_RECOGNITION','FEATURE','图像增强与表盘定位','','适配反光、模糊和几何畸变等采集问题。','image',1),
('WATER_METER_VISION_RECOGNITION','FEATURE','CNN 数字识别','','自动定位表盘并识别读数。','scan',2),
('WATER_METER_VISION_RECOGNITION','FEATURE','异常预警与复核','','对异常数据进行提示并支持人工复核。','alert',3),
('WATER_METER_VISION_RECOGNITION','FEATURE','样本回流迭代','','将错误样本回流模型，支持持续优化。','refresh',4),
('WATER_METER_VISION_RECOGNITION','METRIC','服务对象','20 万+ 只机械水表','成果整理稿业务规模口径。','database',1),
('WATER_METER_VISION_RECOGNITION','METRIC','抄表复核效率','提升 70%+（待核）','需明确整体复核效率的基线和统计范围。','chart',2),
('WATER_METER_VISION_RECOGNITION','METRIC','单表识别时长','≤1 秒（待核）','需明确终端、图片质量和统计方法。','clock',3),
('WATER_METER_VISION_RECOGNITION','METRIC','数字识别准确率','≥98%（待核）','需补充测试集、表型范围和模型版本。','check',4),
('WATER_METER_VISION_RECOGNITION','METRIC','抄表人力投入','下降 60%+（待核）','需明确与复核效率提升指标的关系。','chart',5),
('WATER_METER_VISION_RECOGNITION','CASE','吴江华衍水务','机械水表抄表复核场景','内部成果材料口径。','case',1),
('WATER_METER_VISION_RECOGNITION','PRICE_FEATURE','计费数据责任','人工/系统复核待确认','系统用于辅助抄表和复核，最终计费数据按业务规则确认。','shield',1),
('WATER_METER_VISION_RECOGNITION','COMPATIBILITY','输入数据','抄表照片/图像','图片采集终端、格式和上传链路待确认。','image',1),
-- 11 AI智慧化停水故障诊断
('WATER_SECONDARY_SUPPLY_FAULT','FEATURE','运行数据监测','WinCC OA','采集二次供水泵房运行参数和压力异常。','database',1),
('WATER_SECONDARY_SUPPLY_FAULT','FEATURE','视频 AI 分析','','识别设备运行状态和异常视觉特征。','camera',2),
('WATER_SECONDARY_SUPPLY_FAULT','FEATURE','多模态故障诊断','','联动运行参数和视频信息判断停水诱因。','diagnose',3),
('WATER_SECONDARY_SUPPLY_FAULT','FEATURE','诊断报告与短信推送','','自动生成标准化报告并触达运维人员。','bell',4),
('WATER_SECONDARY_SUPPLY_FAULT','METRIC','预警提前量','24 小时（待核）','PPT 口径，需确认相对人工发现的统计基线。','clock',1),
('WATER_SECONDARY_SUPPLY_FAULT','METRIC','诊断处置时间','2 分钟内（待核）','需定义从告警到报告还是到处置。','clock',2),
('WATER_SECONDARY_SUPPLY_FAULT','METRIC','告警推送时效','30 秒内（待核）','需通过系统日志核验。','bell',3),
('WATER_SECONDARY_SUPPLY_FAULT','METRIC','诊断准确率','>80%（待核）','需补充故障类型、样本量和评估方法。','check',4),
('WATER_SECONDARY_SUPPLY_FAULT','METRIC','停水时长','下降 60%（待核）','PPT 口径，需补充对照基线和统计周期。','water',5),
('WATER_SECONDARY_SUPPLY_FAULT','CASE','吴江华衍水务','二次供水泵房故障诊断','内部成果材料口径。','case',1),
('WATER_SECONDARY_SUPPLY_FAULT','PRICE_FEATURE','处置责任边界','辅助诊断','系统负责识别、提示和报告，具体处置由授权运维人员执行。','shield',1),
('WATER_SECONDARY_SUPPLY_FAULT','COMPATIBILITY','数据接口','WinCC OA/视频监控','具体版本、接口方式和数据留存待确认。','database',1);

-- 仅在没有业务编码或 slug 冲突时导入，避免覆盖现有记录。
START TRANSACTION;

INSERT INTO biz_agent
(agent_code, agent_name, primary_category_code, category_code, icon_code, cover_url, provider_id,
 provider_name, summary, description, price_text, price_min, price_max,
 cert_level, rating, deploy_count, delivery_cycle, service_mode,
 recommend_flag, hot_score, sort_no, publish_status, published_at, slug,
 version_no, ext_json, del_flag, create_by, remark)
SELECT
 t.agent_code, t.agent_name, 'water', t.category_code, t.icon_code, NULL, NULL,
 t.provider_name, t.summary, t.description, t.price_text, NULL, NULL,
 t.cert_level, 0, 0, t.delivery_cycle, t.service_mode,
 'N', 0, t.sort_no, '0', NULL, t.slug,
 1, t.ext_json, '0', 'sql_import', t.remark
FROM tmp_water_case_agent_import t
WHERE @water_case_conflict_count = 0;

INSERT INTO biz_agent_detail_item
(agent_id, item_type, title, subtitle, value_text, content, icon_code, link_url,
 sort_no, status, ext_json)
SELECT
 a.agent_id, d.item_type, d.title, '', d.value_text, d.content, d.icon_code, NULL,
 d.sort_no, '0', NULL
FROM tmp_water_case_detail_import d
JOIN biz_agent a ON a.agent_code = d.agent_code AND a.del_flag = '0'
WHERE @water_case_conflict_count = 0;

-- 导入校验：应返回 11 条主记录；详情项数量应大于 0；全部应为草稿。
SELECT
 a.agent_id,
 a.agent_code,
 a.agent_name,
 a.category_code,
 a.publish_status,
 a.cert_level,
 a.slug,
 a.sort_no,
 COUNT(d.item_id) AS detail_item_count
FROM biz_agent a
LEFT JOIN biz_agent_detail_item d ON d.agent_id = a.agent_id AND d.status = '0'
JOIN tmp_water_case_agent_import t ON t.agent_code = a.agent_code
GROUP BY a.agent_id, a.agent_code, a.agent_name, a.category_code,
         a.publish_status, a.cert_level, a.slug, a.sort_no
ORDER BY a.sort_no;

SELECT
 COUNT(*) AS imported_agent_count,
 SUM(publish_status = '0') AS draft_agent_count,
 SUM(del_flag = '0') AS active_agent_count
FROM biz_agent a
JOIN tmp_water_case_agent_import t ON t.agent_code = a.agent_code;

-- 确认以上结果无误后，在同一连接执行：
-- COMMIT;
-- 若发现问题，执行：
-- ROLLBACK;
-- 提交或回滚后可清理临时表：
-- DROP TEMPORARY TABLE IF EXISTS tmp_water_case_detail_import;
-- DROP TEMPORARY TABLE IF EXISTS tmp_water_case_agent_import;
