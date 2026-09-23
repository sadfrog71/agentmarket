#!/usr/bin/env node
import { readFile, readdir, writeFile } from 'node:fs/promises'
import { fileURLToPath } from 'node:url'
import path from 'node:path'

const here = path.dirname(fileURLToPath(import.meta.url))
const appRoot = path.resolve(here, '..')
const staticRoot = '/Users/mark.ma/Documents/09产品宣传及视频/官方网站/官网'

const pageMeta = {
  HOME: {
    file: 'index.html',
    title: '数字驱动城市基建 · 智慧赋能公用事业 — 平行数字',
    subtitle: '平行数字，面向水务与燃气的数字化、AI应用及工程实施服务。',
    reason: '首页主视觉升级，并移除英文品牌副标'
  },
  GAS: {
    file: 'gas.html',
    title: '智慧燃气，连接安全与效率。 — 平行数字',
    subtitle: '围绕燃气厂站、调压计量与城市配气网络，建设现场可感知、运行可协同、管理可追溯的数智化能力。',
    reason: '智慧燃气专题主视觉与内容升级'
  },
  AI_OS: {
    file: 'ai-os.html',
    title: '衍智云 · 水务 AI OS — 平行数字',
    subtitle: '在统一的平台上，改变企业的工作方式。',
    reason: 'AI OS 统一平台、企业总线与移动工作入口升级'
  }
}

function homeHero(assetRoot) {
  return `<section class="refresh-home-hero" id="home">
  <img src="${assetRoot}/home-city-infrastructure.png" alt="城市、水务厂站与燃气设施协同运行的夜景" fetchpriority="high">
  <div class="refresh-home-hero__inner">
    <p class="refresh-home-kicker"><strong>平行数字</strong> 城市公共事业数智化</p>
    <h1 class="refresh-home-title">让智能，<span class="single-line">走进城市运行的每一天<span class="blue">。</span></span></h1>
    <div class="refresh-home-copy">
      <p>深耕水务与燃气，连接行业经验、数字技术与 AI，让每一次建设回应真实业务。</p>
      <a class="button" href="ai-os.html">探索水务 AI OS<span aria-hidden="true">↗</span></a>
    </div>
    <div class="refresh-home-meta"><span>面向公共事业的数智化服务</span><nav aria-label="业务导航"><a href="water.html">智慧水务</a><a href="gas.html">智慧燃气</a><a href="engineering.html">工程与实施</a></nav><span>向下探索</span></div>
  </div>
</section>`
}

function gasMain(assetRoot) {
  return `<main id="main" class="refresh-gas">
  <section class="refresh-gas-intro wrap">
    <div><p class="refresh-page-eyebrow"><strong>平行数字</strong> 智慧燃气</p><h1>智慧燃气，<br>连接安全与效率<span class="blue">。</span></h1></div>
    <p class="lead">围绕燃气厂站、调压计量与城市配气网络，建设现场可感知、运行可协同、管理可追溯的数智化能力。</p>
  </section>
  <section class="wrap">
    <figure class="refresh-gas-stage">
      <img src="${assetRoot}/smart-gas-station.png" alt="智慧燃气调压计量厂站与城市配气网络" fetchpriority="high">
      <div class="refresh-gas-accent"><small>平行数字 / 智慧燃气</small><h2>智慧<br>燃气</h2></div>
      <figcaption class="refresh-gas-caption"><h2>从门站到城市配气，连接每一个关键运行节点。</h2><p>厂站 / 调压计量 / 城市配气网络</p></figcaption>
    </figure>
    <div class="refresh-gas-grid" aria-label="智慧燃气能力">
      <article><small>01 / 现场</small><h3>厂站与 SCADA</h3><p>围绕设备、工艺与运行画面，支撑标准化建设和现场协同。</p></article>
      <article><small>02 / 连接</small><h3>调压计量与管网协同</h3><p>连接运行信息、计量数据和业务过程，形成可追溯的协同基础。</p></article>
      <article><small>03 / 运行</small><h3>工业网络与运行安全</h3><p>把网络、权限和运维边界纳入项目实施与持续运营。</p></article>
    </div>
  </section>
  <section class="refresh-contact"><div class="wrap"><p class="refresh-page-eyebrow"><strong>从业务现场出发</strong></p><h2>让每一处现场，都成为可靠运行的一部分。</h2><div class="contact-line"><p>从一个燃气业务问题开始，确定适合的建设与协同路径。</p><a class="button" href="mailto:wangchong@avatar-tech.com">咨询燃气业务<span aria-hidden="true">↗</span></a></div></div></section>
</main>`
}

function aiMain(assetRoot) {
  return `<main id="main" class="refresh-ai">
  <section class="refresh-ai-hero wrap">
    <p class="refresh-page-eyebrow"><strong>衍智云</strong> 水务 AI OS</p>
    <h1>在统一的平台上，<br>改变企业的工作方式<span class="blue">。</span></h1>
    <p class="lead">将企业知识、业务数据、既有系统与智能体连接到同一运行层，让每个场景共享上下文、权限与可复用的工作能力。</p>
  </section>
  <section class="refresh-bus wrap" id="capabilities">
    <div class="refresh-bus-copy"><small>01 / 平台结构概要</small><h2>企业<br>总线<span class="blue">。</span></h2><p>把企业系统、知识与现场数据，组织为可治理、可编排、可复用的智能运行层。</p></div>
    <div class="refresh-bus-graphic refresh-bus-graphic--connected">
      <div class="refresh-bus-rail"><span>内置安全与治理</span><span>组织权限</span><span>数据边界</span><span>调用审计</span><span>人工确认</span></div>
      <article class="refresh-bus-node refresh-bus-node--source"><small>来自业务与现场</small><h3>业务与现场</h3><p>生产　管网　客服　GIS　IoT<br>规程　文档　事件记录</p></article>
      <article class="refresh-bus-core"><small>企业总线 / ENTERPRISE BUS</small><h3><span>把连接，变成</span><span>可复用的工作能力。</span></h3><p>统一连接、编排和治理，<br>让业务上下文持续发挥作用。</p><div class="refresh-bus-core-tags"><span>连接</span><span>编排</span><span>治理</span></div></article>
      <article class="refresh-bus-node refresh-bus-node--target"><small>服务智能工作</small><h3>智能工作</h3><p>智能体　专业算法　工作流<br>查询　分析　协同　任务执行</p></article>
      <p class="refresh-bus-note">企业总线为平台结构概要；具体系统接入、数据权限与可执行动作，按项目方案及验证结果确定。</p>
    </div>
  </section>
  <section class="refresh-video wrap"><video controls preload="metadata" poster="${assetRoot}/ai-os-video-poster.jpg" aria-label="衍智云工作入口演示视频"><source src="${assetRoot}/ai-os-workspace.mp4" type="video/mp4">您的浏览器不支持视频播放。</video><div class="refresh-video-copy"><small>产品演示 / 01:42</small><h2>从一个入口，进入日常工作<span class="blue">。</span></h2><p>通过视频呈现统一工作入口、业务问答与 AI 使用情况；正式页面支持全屏播放。</p></div></section>
  <section class="refresh-platform"><div class="wrap"><div class="section-heading"><div><p class="refresh-page-eyebrow"><strong>统一平台</strong> 企业 AI 运行层</p><h2>从一个平台，<br>组织企业智能<span class="blue">。</span></h2></div><p>吸收统一运行层的思路，但聚焦公用事业的业务连接、场景复用和现场交付。</p></div><div class="refresh-platform-grid"><article><small>01 / 统一</small><h3>统一工作入口</h3><p>让人员、智能体与业务应用在同一平台上协同工作，减少场景之间的重复建设。</p></article><article><small>02 / 复用</small><h3>共享上下文与能力</h3><p>知识、数据连接、权限与工作流成为可复用底座，服务更多厂站、部门与业务场景。</p></article><article><small>03 / 治理</small><h3>统一运行与治理</h3><p>围绕身份、授权、调用记录、版本与人工确认，组织企业 AI 的持续运营。</p></article></div></div></section>
  <section class="refresh-mobile"><div class="wrap"><div class="refresh-mobile__head"><div><p class="refresh-page-eyebrow"><strong>移动工作入口</strong></p><h2>把工作入口，<br>带到每一个现场<span class="blue">。</span></h2></div><p>移动端统一助手承接当前身份与授权范围，让现场人员查看重点事项、使用语音交互，并继续完成日常任务。</p></div><figure class="refresh-mobile-visual"><figcaption>移动端界面来自智慧水务典型案例申报书</figcaption><img src="${assetRoot}/ai-os-mobile-assistant.png" alt="衍智云移动端统一助手与工作任务界面" loading="lazy"><img src="${assetRoot}/ai-os-mobile-voice.png" alt="衍智云移动端语音交互界面" loading="lazy"></figure></div></section>
  <section class="refresh-enterprise"><div class="wrap"><div class="section-heading"><div><p class="refresh-page-eyebrow"><strong>企业级运行底座</strong></p><h2>为真实运行而设计<span class="blue">。</span></h2></div><p>平台部署、安全治理和现场共建，围绕组织自身的环境、网络与运行要求进行设计。</p></div><div class="refresh-enterprise-grid"><article class="refresh-enterprise-card"><small>部署</small><h3>灵活部署</h3><p>根据组织的环境、网络与管理要求，设计适合的部署和运维方式。</p></article><article class="refresh-enterprise-card"><small>安全</small><h3>内置安全</h3><p>将组织权限、数据使用边界与调用留痕纳入平台和场景的设计之中。</p></article><article class="refresh-enterprise-card"><small>FDE</small><h3>驻场共建</h3><p>由 FDE 与行业团队共同推进连接、场景验证与生产交付。</p></article></div><div class="refresh-fde"><div><small>FDE / 现场交付工程师</small><h3>让 AI 在现场真正运行。</h3></div><p>从业务场景梳理、系统与数据连接，到权限配置、流程联调和上线验证，驻场共建团队把“可以演示”的能力推进到可被日常工作使用的交付成果。</p></div></div></section>
</main>`
}

function replaceWholeMain(source, main) {
  const start = source.indexOf('<main id="main">')
  const end = source.indexOf('</main>', start)
  if (start < 0 || end < 0) throw new Error('找不到页面主体')
  const footer = source.indexOf('<footer', end)
  return source.slice(0, start) + main + source.slice(footer >= 0 ? footer : end + '</main>'.length)
}

function replaceHomeHero(source, hero) {
  const start = source.indexOf('<section class="home-hero wrap" id="home">')
  const tail = source.indexOf('<section class="section wrap" id="ai">', start)
  if (start < 0 || tail < 0) throw new Error('找不到首页首屏边界')
  return source.slice(0, start).replace('<header class="site-header">', '<header class="site-header refresh-home-header">') + hero + source.slice(tail)
}

function cleanBrandEnglish(source) {
  return source.replaceAll('PARALLEL DIGITAL', '').replace(/<span>\s*<\/span>/g, '')
}

async function updateStaticSource() {
  const visualRoot = 'assets/site-visuals'
  const entries = await readdir(staticRoot)
  for (const entry of entries.filter(name => name.endsWith('.html'))) {
    const file = path.join(staticRoot, entry)
    const source = await readFile(file, 'utf8')
    await writeFile(file, cleanBrandEnglish(source), 'utf8')
  }
  const homePath = path.join(staticRoot, pageMeta.HOME.file)
  const gasPath = path.join(staticRoot, pageMeta.GAS.file)
  const aiPath = path.join(staticRoot, pageMeta.AI_OS.file)
  await writeFile(homePath, replaceHomeHero(await readFile(homePath, 'utf8'), homeHero(visualRoot)), 'utf8')
  await writeFile(gasPath, replaceWholeMain(await readFile(gasPath, 'utf8'), gasMain(visualRoot)), 'utf8')
  await writeFile(aiPath, replaceWholeMain(await readFile(aiPath, 'utf8'), aiMain(visualRoot)), 'utf8')
}

const hex = value => Buffer.from(value, 'utf8').toString('hex')
const utf8 = value => `convert(0x${hex(value)} using utf8mb4)`

function publishBlock(code, mainHtml, strategy) {
  const meta = pageMeta[code]
  const main = utf8(mainHtml('/visuals'))
  const title = utf8(meta.title)
  const subtitle = utf8(meta.subtitle)
  const reason = utf8(meta.reason)
  const comment = utf8(`品牌视觉升级：${meta.reason}`)
  const bodyExpression = strategy === 'home'
    ? `concat(replace(substring(@old_body, 1, @hero_start - 1), '<header class="site-header">', '<header class="site-header refresh-home-header">'), ${main}, substring(@old_body, @hero_tail))`
    : `concat(substring(@old_body, 1, @header_end), ${main}, substring(@old_body, @footer_start))`
  const anchors = strategy === 'home'
    ? `set @hero_start = locate('<section class="home-hero wrap" id="home">', @old_body);
set @hero_tail = locate('<section class="section wrap" id="ai">', @old_body);`
    : `set @header_end = locate('</header>', @old_body) + length('</header>') - 1;
set @footer_start = locate('<footer', @old_body);`
  return `
-- ${code}: ${meta.reason}
set @page_id = (select page_id from site_page where page_code = '${code}' and del_flag = '0' limit 1);
set @old_revision_id = (select published_revision_id from site_page where page_id = @page_id);
set @old_body = (select body_html from site_page_revision where revision_id = @old_revision_id);
set @old_seo = (select seo_json from site_page_revision where revision_id = @old_revision_id);
set @old_data = (select page_data_json from site_page_revision where revision_id = @old_revision_id);
${anchors}
set @new_body = ${bodyExpression};
set @next_no = (select coalesce(max(revision_no), 0) + 1 from site_page_revision where page_id = @page_id);
insert into site_page_revision (page_id, revision_no, revision_state, title, subtitle, body_html, seo_json, page_data_json, content_hash, create_by, create_time, remark)
values (@page_id, @next_no, 'DRAFT', ${title}, ${subtitle}, @new_body, @old_seo, @old_data, sha2(concat(${title}, char(10), ${subtitle}, char(10), @new_body, char(10), coalesce(@old_seo, ''), char(10), coalesce(@old_data, '')), 256), 'site-brand-refresh', sysdate(), ${comment});
set @new_revision_id = last_insert_id();
update site_page_revision set revision_state = 'SUPERSEDED' where revision_id = @old_revision_id and revision_state = 'PUBLISHED';
update site_page_revision set revision_state = 'PUBLISHED', published_at = sysdate(), published_by = 'site-brand-refresh' where revision_id = @new_revision_id;
update site_page set previous_published_revision_id = @old_revision_id, published_revision_id = @new_revision_id, draft_revision_id = null, update_by = 'site-brand-refresh', update_time = sysdate() where page_id = @page_id;
insert into site_publish_audit (aggregate_type, aggregate_id, action_type, from_revision_id, to_revision_id, operator, reason)
values ('PAGE', @page_id, 'PUBLISH', @old_revision_id, @new_revision_id, 'site-brand-refresh', ${reason});
`
}

function createSql(codes = Object.keys(pageMeta)) {
  const templates = {
    HOME: [homeHero, 'home'],
    GAS: [gasMain, 'whole'],
    AI_OS: [aiMain, 'whole']
  }
  const blocks = codes.map(code => publishBlock(code, ...templates[code])).join('')
  return `-- Generated by corporate-site-web/tools/brand-refresh.mjs\n-- Creates an auditable revision for each refreshed page.\nstart transaction;\n${blocks}\ncommit;\n`
}

const command = process.argv[2]
if (command === '--update-static') {
  await updateStaticSource()
  process.stdout.write('静态内容源已同步更新。\n')
} else if (command === '--sql') {
  const codes = process.argv.slice(3)
  if (codes.some(code => !pageMeta[code])) {
    throw new Error(`未知页面编码：${codes.join(', ')}`)
  }
  process.stdout.write(createSql(codes.length ? codes : undefined))
} else {
  process.stderr.write('Usage: node tools/brand-refresh.mjs --update-static | --sql [HOME GAS AI_OS]\n')
  process.exitCode = 1
}
