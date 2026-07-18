<template>
  <div class="site-shell">
    <header class="navbar">
      <button class="brand" @click="go('home')">
        <img src="/assets/huayan-logo.png" alt="华衍水务环境" />
        <span><strong>智能体市场</strong><small>AI Agent Marketplace</small></span>
      </button>
      <nav aria-label="主导航">
        <button v-for="item in navItems" :key="item.key" :class="{ active: page === item.key }" @click="go(item.key)">{{ item.label }}</button>
      </nav>
      <button class="menu-toggle" :class="{ active: menuOpen }" :aria-expanded="menuOpen" aria-label="打开主导航" @click="menuOpen = !menuOpen">
        <i></i><i></i><i></i>
      </button>
      <button class="contact-btn" @click="contactOpen = true">联系我们</button>
      <div v-if="menuOpen" class="mobile-menu">
        <button v-for="item in navItems" :key="item.key" :class="{ active: page === item.key }" @click="go(item.key)">{{ item.label }}</button>
      </div>
    </header>

    <main>
      <template v-if="page === 'home'">
        <section class="hero section-wrap">
          <div class="hero-copy">
            <span class="eyebrow">HUAYAN WATER · AI AGENT MARKET</span>
            <h1>水务智能体展示<br /><em>与实施服务市场</em></h1>
            <p>集中展示经过业务验证的水务智能体、复合任务方案和 FDE 实施服务，帮助业务团队快速完成能力了解与线下选型。</p>
            <div class="hero-search">
              <input v-model="keyword" placeholder="搜索漏损分析、水质预警等能力" @keyup.enter="searchAgents" />
              <button @click="searchAgents">搜索智能体</button>
            </div>
            <div class="hero-actions"><button @click="go('agents')">浏览全部智能体</button><button class="ghost" @click="go('scenarios')">查看多智能体场景</button></div>
            <div class="hero-stats"><div><strong>{{ agents.length }}</strong><span>已发布智能体</span></div><div><strong>25+</strong><span>合作水司</span></div><div><strong>6</strong><span>业务场景域</span></div><div><strong>L1-L3</strong><span>三级认证</span></div></div>
          </div>
          <div class="network-visual" aria-label="水务智能体任务网络">
            <div class="network-grid"></div>
            <div class="network-core"><span>统一编排</span><strong>任务调度中枢</strong></div>
            <span v-for="node in networkNodes" :key="node.label" class="network-node" :style="node.style">{{ node.label }}</span>
          </div>
        </section>

        <section class="section-wrap block-section">
          <div class="section-head"><div><span>SCENARIO DOMAINS</span><h2>六大水务业务场景域</h2></div><button @click="go('agents')">查看全部</button></div>
          <div class="category-grid">
            <button v-for="item in categories" :key="item.code" @click="openCategory(item.code)"><b>{{ item.index }}</b><h3>{{ item.name }}</h3><p>{{ item.description }}</p><strong>{{ item.metric }}</strong></button>
          </div>
        </section>

        <section class="section-wrap block-section">
          <div class="section-head"><div><span>FEATURED AGENTS</span><h2>精选推荐</h2></div><button @click="go('agents')">进入智能体广场</button></div>
          <div v-if="loading" class="loading-state">正在加载智能体内容…</div>
          <div v-else-if="featuredAgents.length" class="agent-grid"><AgentCard v-for="agent in featuredAgents" :key="agent.agentId" :agent="agent" @open="openAgent" /></div>
          <div v-else class="empty-state content-empty"><strong>暂无推荐智能体</strong><span>管理员发布并设为推荐后，内容将在这里展示。</span></div>
        </section>
      </template>

      <section v-else-if="page === 'agents'" class="section-wrap page-section">
        <div class="page-header"><span>AGENT DIRECTORY</span><h1>智能体广场</h1><p>按业务场景搜索和查看当前已发布的水务智能体。</p></div>
        <div class="filter-bar"><input v-model="keyword" placeholder="搜索智能体名称或能力" /><select v-model="category"><option value="">全部场景域</option><option v-for="item in categories" :key="item.code" :value="item.code">{{ item.name }}</option></select><button @click="loadAgents">筛选</button></div>
        <div class="agent-grid"><AgentCard v-for="agent in filteredAgents" :key="agent.agentId" :agent="agent" @open="openAgent" /></div>
        <div v-if="!filteredAgents.length && !loading" class="empty-state"><strong>没有匹配的智能体</strong><span>调整关键词或场景域后重新筛选。</span></div>
      </section>

      <section v-else-if="page === 'detail' && selectedAgent" class="section-wrap page-section">
        <button class="back-link" @click="go('agents')">← 返回智能体广场</button>
        <div class="detail-hero"><div class="detail-symbol">AI</div><div><span>{{ categoryName(selectedAgent.categoryCode) }}</span><h1>{{ selectedAgent.agentName }}</h1><p>{{ selectedAgent.description || selectedAgent.summary || '暂无智能体简介' }}</p><div class="detail-badges"><b>{{ selectedAgent.certLevel }} 认证级</b><span>{{ selectedAgent.providerName }}</span><span>评分 {{ selectedAgent.rating || '-' }}</span></div></div><aside><small>实施参考</small><strong>{{ selectedAgent.priceText || '面议' }}</strong><span>{{ [selectedAgent.deliveryCycle, selectedAgent.serviceMode].filter(Boolean).join(' · ') || '实施信息待确认' }}</span><button @click="contactOpen = true">联系实施评估</button></aside></div>
        <div class="detail-layout"><div><template v-if="selectedAgent.detailItems?.length"><DetailGroup title="核心功能" type="FEATURE" :items="selectedAgent.detailItems" /><DetailGroup title="实测效果" type="METRIC" :items="selectedAgent.detailItems" cards /><DetailGroup title="部署案例" type="CASE" :items="selectedAgent.detailItems" /></template><div v-else class="empty-state detail-empty"><strong>暂无详情数据</strong><span>管理员补充核心功能、效果指标和部署案例后将在这里展示。</span></div></div><aside class="compat-panel"><h3>兼容性信息</h3><template v-if="itemsOf('COMPATIBILITY').length"><div v-for="item in itemsOf('COMPATIBILITY')" :key="item.title"><span>{{ item.title }}</span><strong>{{ item.valueText }}</strong></div></template><p v-else class="aside-empty">暂无兼容性数据</p><h3>实施服务</h3><template v-if="itemsOf('PRICE_FEATURE').length"><p v-for="item in itemsOf('PRICE_FEATURE')" :key="item.title"><b>✓</b> {{ item.title }}：{{ item.content }}</p></template><p v-else class="aside-empty">暂无实施服务数据</p></aside></div>
      </section>

      <section v-else-if="page === 'scenarios'" class="section-wrap page-section scenario-page">
        <div class="scenario-intro">
          <div><span class="eyebrow">MULTI-AGENT COLLABORATION</span><h1>多智能体协同方案</h1><p>围绕水源、生产、管网、二次供水、客户服务和应急管理，由统一编排中枢拆解任务、调用专业智能体并汇总证据。适用于单一智能体无法独立完成的跨业务问题。</p></div>
          <aside><span>协同机制</span><strong>1 个编排中枢</strong><b>任务拆解 · 能力路由 · 证据汇总</b></aside>
        </div>

        <div class="collab-blueprint" aria-label="多智能体编排结构">
          <article class="primary"><span>1 · ORCHESTRATOR</span><h3>统一编排中枢</h3><p>负责意图识别、任务拆解、能力路由、进度跟踪和异常重规划。</p></article>
          <i>→</i>
          <article><span>N · DOMAIN AGENTS</span><h3>业务与通用智能体</h3><p>覆盖水源、生产、管网、二供、客服、应急，以及知识检索和数据分析。</p></article>
          <i>→</i>
          <article><span>X · COMPOSITE TASKS</span><h3>可扩展复合任务</h3><p>组合为应急响应、联合诊断、巡检报告和经营分析等任务包。</p></article>
        </div>

        <div class="section-head scenario-heading"><div><span>SCENARIO DEMO</span><h2>典型场景演示</h2></div><p>选择场景，查看触发条件、智能体分工和综合结果。</p></div>
        <div class="scenario-tabs" role="tablist" aria-label="协同场景示例">
          <button v-for="(item, index) in scenarioCases" :key="item.key" :class="{ active: activeScenario === index }" @click="activeScenario = index"><span>{{ item.domain }}</span><strong>{{ item.title }}</strong></button>
        </div>
        <div class="orchestration-board">
          <div class="task-rail"><span>触发事件</span><strong>{{ activeScenarioData.trigger }}</strong><small>{{ activeScenarioData.triggerDetail }}</small></div>
          <div class="agent-pipeline" :class="{ dense: activeScenarioData.agents.length > 4 }">
            <article v-for="(agent, index) in activeScenarioData.agents" :key="agent.name">
              <b>{{ String(index + 1).padStart(2, '0') }}</b><div><span>{{ agent.role }}</span><h3>{{ agent.name }}</h3><p>{{ agent.action }}</p></div><i v-if="index < activeScenarioData.agents.length - 1">→</i>
            </article>
          </div>
          <div class="scenario-output"><div><span>综合方案输出</span><h2>{{ activeScenarioData.output }}</h2><p>{{ activeScenarioData.summary }}</p></div><div class="outcome-metrics"><span v-for="metric in activeScenarioData.metrics" :key="metric.label"><strong>{{ metric.value }}</strong><small>{{ metric.label }}</small></span></div></div>
        </div>

        <div class="section-head scenario-heading"><div><span>COMPOSITE TASKS</span><h2>其他可组合场景</h2></div><p>统一采用「触发－分析－决策－执行」结构，便于复制和实施评估。</p></div>
        <div class="scenario-catalog">
          <article v-for="item in scenarioCatalog" :key="item.title">
            <span>{{ item.domain }}</span><h3>{{ item.title }}</h3><p>{{ item.description }}</p>
            <div class="compact-flow"><b v-for="step in item.flow" :key="step">{{ step }}</b></div>
            <dl><div><dt>参与智能体</dt><dd>{{ item.agents }}</dd></div><div><dt>{{ item.metricLabel }}</dt><dd>{{ item.metric }}</dd></div></dl>
          </article>
        </div>

        <div class="section-head scenario-heading"><div><span>ORCHESTRATION FLOW</span><h2>任务如何完成</h2></div><p>从触发到执行留痕，每个阶段都有明确责任。</p></div>
        <div class="orchestration-rail">
          <article v-for="(item, index) in orchestrationFlow" :key="item.title"><span>{{ String(index + 1).padStart(2, '0') }} · {{ item.phase }}</span><h3>{{ item.title }}</h3><p>{{ item.description }}</p></article>
        </div>

        <div class="boundary-panel">
          <div><span>IMPLEMENTATION BOUNDARY</span><h2>编排与实施边界</h2></div>
          <div class="scenario-principles">
            <article><span>01</span><h3>分层执行</h3><p>简单规则在端侧执行，复杂推理在边缘节点执行，跨域优化在平台侧完成。</p></article>
            <article><span>02</span><h3>计算与解释分离</h3><p>指标和阈值由规则计算，LLM 负责原因说明、报告生成和预案摘要。</p></article>
            <article><span>03</span><h3>关键动作人工确认</h3><p>关阀、参数调整、预案启动、派单和用户通知须由授权人员确认。</p></article>
          </div>
        </div>
      </section>

      <section v-else-if="page === 'fde'" class="section-wrap page-section fde-page">
        <div class="fde-hero">
          <div><span class="eyebrow">FORWARD DEPLOYED ENGINEERING</span><h1>FDE 实施与咨询服务</h1><p>面向水务企业的业务、数据与信息化团队，提供从场景梳理、数据诊断到智能体实施、验收和运营移交的现场服务。</p><button @click="contactOpen = true">预约实施评估</button></div>
          <div class="fde-loop" aria-label="FDE 实施闭环"><span>业务现场</span><i>→</i><span>数据与流程</span><i>→</i><strong>智能体实施</strong><i>→</i><span>验收运营</span></div>
        </div>

        <div class="fde-scope-grid">
          <article v-for="item in fdeScope" :key="item.label"><span>{{ item.label }}</span><strong>{{ item.value }}</strong></article>
        </div>

        <div class="section-head fde-heading"><div><span>DELIVERY FLOW</span><h2>FDE 实施业务流程</h2></div><p>每个阶段均形成可评审的交付物，再进入下一阶段。</p></div>
        <div class="fde-process">
          <article v-for="(step, index) in fdeSteps" :key="step.title"><b>{{ String(index + 1).padStart(2, '0') }}</b><span>{{ step.phase }}</span><h3>{{ step.title }}</h3><p>{{ step.description }}</p><small>{{ step.output }}</small></article>
        </div>

        <div class="section-head fde-heading"><div><span>CORE SERVICES</span><h2>三类核心服务</h2></div><p>可按项目阶段单项开展，也可组合为完整实施服务。</p></div>
        <div class="fde-services">
          <article v-for="service in fdeServices" :key="service.title"><div><span>{{ service.en }}</span><h2>{{ service.title }}</h2><p>{{ service.description }}</p></div><ul><li v-for="item in service.items" :key="item">{{ item }}</li></ul><footer>{{ service.deliverable }}</footer></article>
        </div>
        <div class="fde-standards"><div><span>STANDARD DELIVERY</span><h2>智能体规范化实施</h2><p>用统一规范控制接口、数据、提示词、权限、评测和运行监控，避免「能演示、难上线」。</p></div><div class="standard-tags"><span v-for="item in fdeStandards" :key="item">{{ item }}</span></div></div>

        <div class="section-head fde-heading"><div><span>CONTROL POINTS</span><h2>实施控制点</h2></div><p>控制项目范围和效果预期，避免数据不足或责任边界不清。</p></div>
        <div class="fde-controls"><article v-for="(item, index) in fdeControls" :key="item.title"><span>{{ String(index + 1).padStart(2, '0') }}</span><div><h3>{{ item.title }}</h3><p>{{ item.description }}</p></div></article></div>
      </section>

      <section v-else-if="page === 'compute'" class="section-wrap page-section compute-page">
        <div class="page-header"><span>COMPUTING CENTER</span><h1>算力中心</h1><p>用于展示模型资源、算力池、调用消耗和智能体资源占用。</p></div>
        <div class="compute-empty">
          <div class="compute-empty-symbol" aria-hidden="true"><span></span><span></span><span></span></div>
          <strong>暂无算力数据</strong>
          <p>当前尚未接入模型资源、算力池和调用消耗数据。后台完成数据配置并发布后，本页将展示资源状态与使用情况。</p>
          <small>当前页面不展示示例数据，避免将演示数值误认为真实运行数据。</small>
        </div>
      </section>

      <section v-else class="section-wrap page-section static-placeholder">
        <div class="empty-state"><strong>暂无页面内容</strong><span>管理员发布内容后将在此处展示。</span></div>
      </section>
    </main>

    <footer><img src="/assets/huayan-logo.png" alt="" /><span>华衍水务环境智能体市场</span><small>展示内容以实施范围和线下确认结果为准。</small></footer>

    <div v-if="contactOpen" class="modal-mask" @click.self="contactOpen = false"><div class="contact-modal"><button class="close" @click="contactOpen = false">×</button><span>OFFLINE SERVICE</span><h2>联系实施与上架</h2><p>一期采用线下沟通方式。确认需求、材料和实施范围后，由平台管理员完成登记与内容发布。</p><div><strong>业务咨询</strong><span>联系方式将在部署前配置</span></div><button @click="contactOpen = false">知道了</button></div></div>
  </div>
</template>

<script setup>
import { computed, defineComponent, h, onBeforeUnmount, onMounted, ref } from 'vue'
import { fetchAgentDetail, fetchAgents } from './api/agents'

const navItems = [
  { key: 'home', label: '首页' }, { key: 'agents', label: '智能体广场' },
  { key: 'scenarios', label: '多智能体场景' }, { key: 'fde', label: 'FDE 服务' }, { key: 'compute', label: '算力中心' }
]
const categories = [
  { index: '01', code: 'production', name: '供水生产与调度', description: '水量预测、泵站调度、能耗优化', metric: '能耗降低 15%-20%' },
  { index: '02', code: 'network', name: '管网漏损与运行', description: 'DMA 漏损分析、爆管预警、巡检优化', metric: '漏损率降至 9% 以下' },
  { index: '03', code: 'quality', name: '水质安全', description: '异常预警、应急溯源、加药优化', metric: '预警提前 2 小时' },
  { index: '04', code: 'customer', name: '客户服务与营销', description: '智能客服、抄表催收、用户服务', metric: '投诉率降低 35%' },
  { index: '05', code: 'engineering', name: '工程建设与资产', description: '工程进度、资产台账、预测维护', metric: '风险提前识别' },
  { index: '06', code: 'management', name: '管理与决策', description: '运营日报、指标分析、应急指挥', metric: '快速形成报告' }
]
const networkNodes = [
  { label: '生产调度', style: 'left:8%;top:17%' }, { label: '水质安全', style: 'right:8%;top:18%' },
  { label: '管网运行', style: 'left:3%;bottom:22%' }, { label: '客户服务', style: 'right:4%;bottom:21%' },
  { label: '应急处置', style: 'left:38%;bottom:5%' }
]
const scenarioCases = [
  {
    key: 'warning', domain: '主动预警', title: '水质异常提前预警', trigger: '取水口浊度持续上升', triggerDetail: '每 5 分钟采集一次数据，监测到 24 小时内浊度由 0.8 NTU 升至 1.2 NTU',
    agents: [
      { role: '采集', name: '采集智能体', action: '自动采集取水口浊度并形成连续时序' },
      { role: '识别', name: '监测智能体', action: '识别异常增幅并确认变化持续性' },
      { role: '预测', name: '预测智能体', action: '预测 36 小时后的浊度和超标风险' },
      { role: '分析', name: '分析智能体', action: '检查全流程、降雨和上游来水影响' },
      { role: '预案', name: '预案智能体', action: '生成巡检、加药和应急预案建议' },
      { role: '播报', name: '播报智能体', action: '向调度人员播报依据和待确认动作' }
    ],
    output: '形成水质异常提前预警方案', summary: '预测 36 小时后浊度可能达到 3.5 NTU，建议加强巡检、将混凝剂投加量提高 15%，并由调度人员确认是否启动应急预案。',
    metrics: [{ value: '36 小时', label: '提前预警' }, { value: '50%', label: '异常增幅' }, { value: '6 个', label: '参与智能体' }]
  },
  {
    key: 'quality', domain: '客户服务', title: '水质投诉联合诊断', trigger: '用户投诉水质浑浊', triggerDetail: '客服工单触发，自动关联小区、供水分区和近期水质数据',
    agents: [
      { role: '定位', name: '客服智能体', action: '识别投诉位置并定位高频区域' },
      { role: '分析', name: '管网水质分析智能体', action: '调取监测点数据并判断异常范围' },
      { role: '核查', name: '二次供水智能体', action: '核查水箱清洗和泵房运行状态' },
      { role: '研判', name: '水质趋势智能体', action: '分析近 7 天趋势并生成原因排序' }
    ],
    output: '形成联合诊断与处置建议', summary: '判断异常主要由二供水箱长期未清洗导致，建议立即安排清洗、加强监测并向用户推送说明。',
    metrics: [{ value: '5 秒', label: '完成诊断' }, { value: '7 天', label: '趋势回溯' }, { value: '4 类', label: '数据联合' }]
  },
  {
    key: 'burst', domain: '管网运行', title: '管网爆管应急响应', trigger: '管网爆管报警', triggerDetail: '压力波动与现场报警触发，自动定位影响区域和关键阀门',
    agents: [
      { role: '预案', name: '应急预案智能体', action: '匹配同类事件预案和响应等级' },
      { role: '评估', name: '管网风险评估智能体', action: '评估停水范围、重点用户和次生风险' },
      { role: '核价', name: '抢维修核价智能体', action: '估算材料、施工周期和维修费用' },
      { role: '通知', name: '客服智能体', action: '生成分区域停水通知和用户答复口径' }
    ],
    output: '形成抢修、停水与客户通知方案', summary: '建议关闭 3 个阀门，预计停水 12 小时，影响约 1200 户，并同步形成抢修资源与费用计划。',
    metrics: [{ value: '3 个', label: '建议关阀' }, { value: '12 小时', label: '预计停水' }, { value: '1200 户', label: '影响范围' }]
  }
]
const scenarioCatalog = [
  { domain: '客户服务', title: '水质投诉联合诊断', description: '汇聚近 7 天工单、管网监测和二供水箱记录，定位投诉原因并生成答复。', flow: ['问题接收', '数据汇聚', '多源诊断', '方案与答复'], agents: '客服 + 管网水质 + 二供', metricLabel: '诊断时效', metric: '约 5 秒' },
  { domain: '管网运行', title: '爆管应急响应', description: '由压力、流量异常或集中报修触发，确认影响范围并形成关阀、抢修和通知建议。', flow: ['事件确认', '风险评估', '抢修核价', '通知与执行'], agents: '应急 + 风险 + 核价 + 客服', metricLabel: '最终动作', metric: '值班人员确认' },
  { domain: '生产调度', title: '生产能耗优化分析', description: '建立全厂、泵房和泵组三级单耗账本，结合用水预测与峰谷电价生成调度建议。', flow: ['生产监测', '单耗计算', '偏差归因', '调度建议'], agents: '生产监测 + 能耗分析', metricLabel: '参考目标', metric: '能耗降低 15%－20%' },
  { domain: '水质安全', title: '全链路水质巡检', description: '并行检查水源、生产过程、管网监测点和二供泵房，形成风险清单。', flow: ['水源检测', '生产监测', '管网水质', '二供巡检'], agents: '水源 + 生产 + 管网 + 二供', metricLabel: '交付结果', metric: '巡检报告 + 风险清单' }
]
const orchestrationFlow = [
  { phase: 'TRIGGER', title: '感知与触发', description: '由监测告警、工单、定时计划或人工指令启动任务。' },
  { phase: 'PLAN', title: '拆解与选路', description: '编排中枢识别目标，将复杂问题拆为可执行子任务。' },
  { phase: 'EXECUTE', title: '并行执行', description: '专业智能体调用数据、知识库和业务工具并返回证据。' },
  { phase: 'VERIFY', title: '校验与确认', description: '汇总结果并检查冲突，高风险动作交由业务人员确认。' },
  { phase: 'TRACE', title: '执行与留痕', description: '下发已确认动作，记录输入、过程、证据和处置结果。' }
]
const fdeScope = [
  { label: '服务对象', value: '业务部门、数据团队、信息化部门' },
  { label: '介入阶段', value: '项目规划、试点建设、推广复制' },
  { label: '工作方式', value: '现场调研 + 联合设计 + 分阶段验证' },
  { label: '交付原则', value: '范围明确、数据可用、效果可验收' }
]
const fdeSteps = [
  { phase: 'INITIATE', title: '项目启动与目标确认', description: '明确业务目标、项目范围、参与角色、计划节点和验收责任。', output: '交付：项目任务书' },
  { phase: 'DISCOVER', title: '场景梳理与优先级评估', description: '还原现有流程，识别高频问题、关键判断点和人工操作环节。', output: '交付：场景清单与优先级矩阵' },
  { phase: 'ASSESS', title: '数据诊断与治理设计', description: '检查数据来源、字段、质量、更新频率、接口和权限条件。', output: '交付：数据评估报告' },
  { phase: 'DESIGN', title: '方案设计与实施计划', description: '设计智能体职责、任务流程、知识库、系统接口和安全边界。', output: '交付：实施方案与接口清单' },
  { phase: 'DELIVER', title: '配置开发与试点验证', description: '完成数据接入、流程配置、测试集验证和业务人员试用。', output: '交付：试点版本与测试报告' },
  { phase: 'OPERATE', title: '验收、培训与运营移交', description: '对照基线评估效果，完成操作培训、权限移交和运行监测。', output: '交付：验收报告与运营手册' }
]
const fdeServices = [
  { en: 'SCENARIO CONSULTING', title: '场景梳理与咨询', description: '从岗位任务和业务流程出发，识别适合使用智能体的工作环节。', items: ['业务访谈、岗位任务和现有流程还原', '问题频率、业务影响和数据条件评估', '任务拆解、能力边界和人工确认点设计', '试点选择、优先级排序和价值测算'], deliverable: '标准交付物：现状流程图、场景机会清单、优先级矩阵、试点建议书' },
  { en: 'DATA & CONSULTING', title: '数据治理与咨询服务', description: '核查数据是否可获得、可理解、可连接和可持续使用。', items: ['业务系统、数据表、接口和文件来源盘点', '数据字典、指标口径和主数据定义', '完整性、准确性、及时性和一致性检查', '接口改造、权限申请和治理任务安排'], deliverable: '标准交付物：数据目录、指标字典、质量报告、接口清单、治理实施计划' },
  { en: 'STANDARD DELIVERY', title: '智能体规范化实施', description: '按统一方法完成方案、配置、测试、部署和验收。', items: ['智能体职责、工作流、工具调用和异常处理设计', '知识库、提示词、权限和操作留痕配置', '业务测试集、效果指标和安全测试执行', '部署记录、版本管理、培训和运营监测'], deliverable: '标准交付物：实施方案、配置清单、测试报告、部署记录、操作与运营手册' }
]
const fdeStandards = ['能力说明书', '数据接口规范', '知识库规范', '工具调用规范', '权限边界', '评测用例集', '发布检查表', '运行监控指标']
const fdeControls = [
  { title: '先确认业务目标', description: '用业务指标定义项目价值，不以技术功能数量代替效果。' },
  { title: '数据就绪再实施', description: '数据来源、质量、权限和接口责任确认后进入配置开发。' },
  { title: '关键动作保留确认', description: '调度参数、用户通知和预案启动等动作由授权人员确认。' },
  { title: '基于证据完成验收', description: '使用测试集、运行记录和业务基线验证准确率与实际效果。' }
]

const page = ref('home')
const agents = ref([])
const selectedAgent = ref(null)
const keyword = ref('')
const category = ref('')
const loading = ref(false)
const contactOpen = ref(false)
const menuOpen = ref(false)
const activeScenario = ref(0)

const currentLabel = computed(() => navItems.find(item => item.key === page.value)?.label || '')
const activeScenarioData = computed(() => scenarioCases[activeScenario.value])
const featuredAgents = computed(() => agents.value.filter(item => item.recommendFlag === 'Y').slice(0, 4))
const filteredAgents = computed(() => agents.value.filter(item => {
  const matchCategory = !category.value || item.categoryCode === category.value
  const text = `${item.agentName}${item.summary || ''}`
  return matchCategory && (!keyword.value || text.toLowerCase().includes(keyword.value.toLowerCase()))
}))

function categoryName(code) { return categories.find(item => item.code === code)?.name || '水务智能体' }
function go(target) { page.value = target; menuOpen.value = false; if (target !== 'detail') window.location.hash = target; window.scrollTo({ top: 0, behavior: 'smooth' }) }
function syncHash() { const target = window.location.hash.slice(1); if (navItems.some(item => item.key === target)) page.value = target }
function searchAgents() { category.value = ''; go('agents') }
function openCategory(code) { category.value = code; go('agents') }
async function loadAgents() { loading.value = true; try { agents.value = await fetchAgents() } catch { agents.value = [] } finally { loading.value = false } }
async function openAgent(agent) { selectedAgent.value = await fetchAgentDetail(agent.agentId); go('detail') }
function itemsOf(type) { return selectedAgent.value?.detailItems?.filter(item => item.itemType === type) || [] }

const AgentCard = defineComponent({
  props: { agent: { type: Object, required: true } }, emits: ['open'],
  setup(props, { emit }) { return () => h('button', { class: 'agent-card', onClick: () => emit('open', props.agent) }, [
    h('div', { class: 'agent-card-top' }, [h('span', { class: 'agent-icon' }, 'AI'), h('div', [h('small', categoryName(props.agent.categoryCode)), h('h3', props.agent.agentName)])]),
    h('p', props.agent.summary), h('div', { class: 'agent-tags' }, [h('b', `${props.agent.certLevel} 认证`), props.agent.recommendFlag === 'Y' ? h('span', '精选') : null]),
    h('div', { class: 'agent-card-foot' }, [h('span', props.agent.providerName), h('strong', props.agent.priceText || '面议')])
  ]) }
})

const DetailGroup = defineComponent({
  props: { title: String, type: String, items: Array, cards: Boolean },
  setup(props) { return () => {
    const list = (props.items || []).filter(item => item.itemType === props.type)
    if (!list.length) return null
    return h('section', { class: ['detail-group', props.cards ? 'metric-group' : ''] }, [h('h2', props.title), h('div', { class: props.cards ? 'metric-cards' : 'detail-items' }, list.map(item => h('article', [item.valueText ? h('strong', item.valueText) : null, h('h3', item.title), h('p', item.content)])))])
  }}
})

onMounted(() => { syncHash(); window.addEventListener('hashchange', syncHash); loadAgents() })
onBeforeUnmount(() => window.removeEventListener('hashchange', syncHash))
</script>
