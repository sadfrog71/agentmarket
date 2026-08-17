<template>
  <div class="app-container market-agent-page">
    <div class="page-intro">
      <div>
        <p class="page-kicker">AGENT CONTENT</p>
        <h2>智能体内容管理</h2>
        <p>维护前台卡片、排行榜和详情页数据。页面结构由前端固定，后台只维护内容。</p>
      </div>
      <el-button type="primary" icon="Plus" @click="handleAdd" v-hasPermi="['market:agent:add']">新增智能体</el-button>
    </div>

    <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch" class="query-panel">
      <el-form-item label="智能体名称" prop="agentName">
        <el-input v-model="queryParams.agentName" placeholder="输入名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="一级分类" prop="primaryCategoryCode">
        <el-select v-model="queryParams.primaryCategoryCode" placeholder="全部一级分类" clearable style="width: 150px">
          <el-option v-for="item in categoryOptions" :key="item.categoryCode" :label="item.categoryName" :value="item.categoryCode" />
        </el-select>
      </el-form-item>
      <el-form-item label="场景域" prop="categoryCode">
        <el-select v-model="queryParams.categoryCode" placeholder="全部场景域" clearable style="width: 190px">
          <el-option v-for="dict in market_agent_category" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="发布状态" prop="publishStatus">
        <el-select v-model="queryParams.publishStatus" placeholder="全部状态" clearable style="width: 150px">
          <el-option v-for="dict in market_publish_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['market:agent:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['market:agent:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="agentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="52" align="center" />
      <el-table-column label="智能体" min-width="230">
        <template #default="scope">
          <div class="agent-cell">
            <div class="agent-mark">AI</div>
            <div><strong>{{ scope.row.agentName }}</strong><small>{{ scope.row.agentCode }}</small></div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="一级分类" prop="primaryCategoryCode" width="105"><template #default="scope"><span>{{ categoryName(scope.row.primaryCategoryCode) }}</span></template></el-table-column>
      <el-table-column label="场景域" prop="categoryCode" min-width="150">
        <template #default="scope"><dict-tag :options="market_agent_category" :value="scope.row.categoryCode" /></template>
      </el-table-column>
      <el-table-column label="服务商" prop="providerName" min-width="150" show-overflow-tooltip />
      <el-table-column label="认证" prop="certLevel" width="105">
        <template #default="scope"><dict-tag :options="market_cert_level" :value="scope.row.certLevel" /></template>
      </el-table-column>
      <el-table-column label="参考价格" prop="priceText" width="120" />
      <el-table-column label="推荐" prop="recommendFlag" width="80" align="center">
        <template #default="scope"><el-tag v-if="scope.row.recommendFlag === 'Y'" type="primary">推荐</el-tag><span v-else>-</span></template>
      </el-table-column>
      <el-table-column label="状态" prop="publishStatus" width="100">
        <template #default="scope"><dict-tag :options="market_publish_status" :value="scope.row.publishStatus" /></template>
      </el-table-column>
      <el-table-column label="更新时间" prop="updateTime" width="165">
        <template #default="scope">{{ parseTime(scope.row.updateTime || scope.row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="140" fixed="right" align="center">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['market:agent:edit']">编辑</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['market:agent:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="980px" append-to-body destroy-on-close>
      <el-form ref="agentRef" :model="form" :rules="rules" label-width="100px">
        <el-divider content-position="left">卡片与发布信息</el-divider>
        <el-row :gutter="18">
          <el-col :span="12"><el-form-item label="智能体名称" prop="agentName" required><el-input v-model="form.agentName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="业务编码" prop="agentCode" required><el-input v-model="form.agentCode" placeholder="如 DMA_LEAKAGE" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="一级分类" prop="primaryCategoryCode" required><el-select v-model="form.primaryCategoryCode" style="width:100%"><el-option v-for="item in categoryOptions" :key="item.categoryCode" :label="item.categoryName" :value="item.categoryCode" /></el-select></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="场景域" prop="categoryCode" required><el-select v-model="form.categoryCode" style="width:100%"><el-option v-for="dict in market_agent_category" :key="dict.value" :label="dict.label" :value="dict.value" /></el-select></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="认证等级" prop="certLevel" required><el-select v-model="form.certLevel" style="width:100%"><el-option v-for="dict in market_cert_level" :key="dict.value" :label="dict.label" :value="dict.value" /></el-select></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="发布状态" prop="publishStatus" required><el-select v-model="form.publishStatus" style="width:100%"><el-option v-for="dict in market_publish_status" :key="dict.value" :label="dict.label" :value="dict.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="服务商" prop="providerName" required><el-input v-model="form.providerName" /></el-form-item></el-col>
          <el-col :span="6"><el-form-item label="参考价格"><el-input v-model="form.priceText" placeholder="面议" /></el-form-item></el-col>
          <el-col :span="6"><el-form-item label="详情标识"><el-input v-model="form.slug" placeholder="英文短名称" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="评分"><el-input-number v-model="form.rating" :min="0" :max="5" :step="0.1" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="部署数量"><el-input-number v-model="form.deployCount" :min="0" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="首页推荐"><el-switch v-model="form.recommendFlag" active-value="Y" inactive-value="N" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="热门分值"><el-input-number v-model="form.hotScore" :min="0" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="显示顺序"><el-input-number v-model="form.sortNo" :min="0" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="交付周期"><el-input v-model="form.deliveryCycle" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="服务方式"><el-input v-model="form.serviceMode" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="演示环境地址" prop="demoUrl"><el-input v-model="form.demoUrl" maxlength="500" show-word-limit placeholder="https://demo.example.com"><template #prepend>选填</template></el-input><span class="field-tip">填写后，前台详情页右侧会显示“前往演示地址”入口；仅支持 http/https。</span></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="卡片摘要" prop="summary" required><el-input v-model="form.summary" type="textarea" :rows="3" maxlength="500" show-word-limit /></el-form-item></el-col>
          <el-col :span="24">
            <el-form-item label="详细说明" prop="description" required class="description-editor-item">
              <div class="editor-mode-bar">
                <el-radio-group v-model="descriptionMode" size="small">
                  <el-radio-button value="rich">富文本</el-radio-button>
                  <el-radio-button value="markdown">Markdown</el-radio-button>
                </el-radio-group>
                <span>{{ descriptionMode === 'markdown' ? '左侧编辑 Markdown，右侧实时预览' : '使用工具栏编辑排版内容' }}</span>
              </div>
              <Editor v-if="descriptionMode === 'rich'" v-model="form.description" :min-height="220" />
              <div v-else class="markdown-workbench">
                <el-input v-model="form.description" type="textarea" :rows="16" resize="vertical" placeholder="# 标题\n\n使用 **加粗**、列表、引用、代码块和表格编写详情。" />
                <div class="markdown-preview rich-preview" v-html="markdownPreview"></div>
              </div>
              <span class="field-tip">两种模式共用详情字段，切换模式不会自动转换已有内容；Markdown 中的原始 HTML 不会执行。</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">详情页结构项</el-divider>
        <div class="detail-toolbar">
          <span>带 <b class="required-mark">*</b> 的字段必填；功能／案例／实施服务需填写内容，指标／兼容性需填写指标值。</span>
          <div class="detail-actions">
            <el-button type="success" plain icon="Upload" :disabled="!form.agentId" @click="handleCaseImport">导入案例</el-button>
            <el-button type="primary" plain icon="Plus" @click="addDetailItem">新增一项</el-button>
          </div>
        </div>
        <el-table :data="form.detailItems" border max-height="360">
          <el-table-column label="类型" width="150">
            <template #header><span class="required-mark">* </span>类型</template>
            <template #default="scope"><el-select v-model="scope.row.itemType"><el-option v-for="dict in market_detail_item_type" :key="dict.value" :label="dict.label" :value="dict.value" /></el-select></template>
          </el-table-column>
          <el-table-column label="标题" min-width="170"><template #header><span class="required-mark">* </span>标题</template><template #default="scope"><el-input v-model="scope.row.title" /></template></el-table-column>
          <el-table-column label="指标值／字段值" min-width="145"><template #header>指标值／字段值</template><template #default="scope"><el-input v-model="scope.row.valueText" /></template></el-table-column>
          <el-table-column label="内容说明" min-width="260"><template #header>内容说明</template><template #default="scope"><el-input v-model="scope.row.content" type="textarea" :rows="2" /></template></el-table-column>
          <el-table-column label="排序" width="90"><template #default="scope"><el-input-number v-model="scope.row.sortNo" :min="0" controls-position="right" style="width:70px" /></template></el-table-column>
          <el-table-column label="操作" width="70" align="center"><template #default="scope"><el-button link type="danger" icon="Delete" @click="removeDetailItem(scope.$index)" /></template></el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <div class="dialog-footer"><el-button type="primary" @click="submitForm">保存内容</el-button><el-button @click="cancel">取消</el-button></div>
      </template>
    </el-dialog>

    <excel-import-dialog
      ref="caseImportRef"
      title="导入部署案例"
      width="560px"
      :action="caseImportAction"
      template-action="/market/agent/case-import-template"
      template-file-name="agent_case_template"
      update-support-label="标题相同的案例是否覆盖现有内容"
      @success="handleCaseImportSuccess"
    />
  </div>
</template>

<script setup name="MarketAgent">
import { listAgent, getAgent, addAgent, updateAgent, delAgent } from '@/api/market/agent'
import { listCategoryOptions } from '@/api/market/category'
import { isHtmlContent, renderMarkdown } from '@/utils/markdown'
import ExcelImportDialog from '@/components/ExcelImportDialog'

const { proxy } = getCurrentInstance()
const { market_agent_category, market_cert_level, market_publish_status, market_detail_item_type } = useDict(
  'market_agent_category', 'market_cert_level', 'market_publish_status', 'market_detail_item_type'
)
const categoryOptions = ref([])

const agentList = ref([])
const loading = ref(false)
const open = ref(false)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref('')
const descriptionMode = ref('rich')

const emptyForm = () => ({
  agentId: undefined, agentCode: '', agentName: '', primaryCategoryCode: 'water', categoryCode: 'production', iconCode: 'robot', demoUrl: '',
  providerName: '', summary: '', description: '', priceText: '面议', certLevel: 'L1', rating: 0,
  deployCount: 0, deliveryCycle: '', serviceMode: '', recommendFlag: 'N', hotScore: 0, sortNo: 0,
  publishStatus: '0', slug: '', detailItems: []
})

const data = reactive({
  form: emptyForm(),
  queryParams: { pageNum: 1, pageSize: 10, agentName: undefined, primaryCategoryCode: undefined, categoryCode: undefined, publishStatus: undefined },
  rules: {
    agentName: [{ required: true, message: '智能体名称不能为空', trigger: 'blur' }],
    agentCode: [{ required: true, message: '业务编码不能为空', trigger: 'blur' }],
    primaryCategoryCode: [{ required: true, message: '请选择一级分类', trigger: 'change' }],
    categoryCode: [{ required: true, message: '请选择场景域', trigger: 'change' }],
    certLevel: [{ required: true, message: '请选择认证等级', trigger: 'change' }],
    publishStatus: [{ required: true, message: '请选择发布状态', trigger: 'change' }],
    providerName: [{ required: true, message: '服务商不能为空', trigger: 'blur' }],
    summary: [{ required: true, message: '卡片摘要不能为空', trigger: 'blur' }],
    description: [{ required: true, message: '详细说明不能为空', trigger: 'blur' }],
    demoUrl: [{ validator: validateDemoUrl, trigger: 'blur' }]
  }
})
const { form, queryParams, rules } = toRefs(data)
const markdownPreview = computed(() => renderMarkdown(form.value.description))
const caseImportRef = ref(null)
const caseImportAction = computed(() => form.value.agentId ? `/market/agent/${form.value.agentId}/case-import` : '')

function categoryName(code) { return categoryOptions.value.find(item => item.categoryCode === code)?.categoryName || code || '-' }
function loadCategoryOptions() { listCategoryOptions().then(res => { categoryOptions.value = res.data || [] }) }
function validateDemoUrl(rule, value, callback) {
  const demoUrl = String(value || '').trim()
  if (!demoUrl || /^https?:\/\/\S+$/i.test(demoUrl)) return callback()
  callback(new Error('请输入有效的 http/https 演示地址'))
}

function getList() {
  loading.value = true
  listAgent(queryParams.value).then(res => { agentList.value = res.rows; total.value = res.total }).finally(() => { loading.value = false })
}
function reset() { form.value = emptyForm(); descriptionMode.value = 'rich'; proxy.resetForm('agentRef') }
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
function handleSelectionChange(selection) { ids.value = selection.map(item => item.agentId); single.value = selection.length !== 1; multiple.value = !selection.length }
function handleAdd() { reset(); title.value = '新增智能体'; open.value = true }
function handleUpdate(row) {
  reset()
  const id = row.agentId || ids.value[0]
  getAgent(id).then(res => {
    form.value = { ...emptyForm(), ...res.data, detailItems: res.data.detailItems || [] }
    descriptionMode.value = form.value.description && !isHtmlContent(form.value.description) ? 'markdown' : 'rich'
    title.value = '编辑智能体'
    open.value = true
  })
}
function addDetailItem() { form.value.detailItems.push({ itemType: 'FEATURE', title: '', valueText: '', content: '', sortNo: form.value.detailItems.length, status: '0' }) }
function removeDetailItem(index) { form.value.detailItems.splice(index, 1) }
function cancel() { open.value = false; reset() }
function validateDetailItems() {
  for (let index = 0; index < form.value.detailItems.length; index++) {
    const item = form.value.detailItems[index]
    const row = index + 1
    if (!item.itemType) return proxy.$modal.msgError(`第${row}条详情项请选择类型`)
    if (!item.title?.trim()) return proxy.$modal.msgError(`第${row}条详情项标题不能为空`)
    if (['FEATURE', 'CASE', 'PRICE_FEATURE'].includes(item.itemType) && !item.content?.trim()) {
      return proxy.$modal.msgError(`第${row}条详情项内容不能为空`)
    }
    if (['METRIC', 'COMPATIBILITY'].includes(item.itemType) && !item.valueText?.trim()) {
      return proxy.$modal.msgError(`第${row}条详情项的指标值／字段值不能为空`)
    }
  }
  return true
}
function handleCaseImport() {
  if (!form.value.agentId) return proxy.$modal.msgWarning('请先保存智能体，再导入案例')
  caseImportRef.value?.open()
}
function handleCaseImportSuccess() {
  getAgent(form.value.agentId).then(res => {
    form.value = { ...form.value, ...res.data, detailItems: res.data.detailItems || [] }
  })
}
function submitForm() {
  if (!validateDetailItems()) return
  proxy.$refs.agentRef.validate(valid => {
    if (!valid) return
    const request = form.value.agentId ? updateAgent(form.value) : addAgent(form.value)
    request.then(() => { proxy.$modal.msgSuccess(form.value.agentId ? '内容已更新' : '智能体已创建'); open.value = false; getList() })
  })
}
function handleDelete(row) {
  const targetIds = row.agentId || ids.value
  proxy.$modal.confirm('删除后前台将不再展示所选智能体，是否继续？').then(() => delAgent(targetIds)).then(() => { proxy.$modal.msgSuccess('已删除'); getList() })
}

getList()
loadCategoryOptions()
</script>

<style scoped lang="scss">
.market-agent-page { --market-blue: #1266e3; }
.page-intro { display:flex; align-items:flex-end; justify-content:space-between; gap:24px; padding:22px 24px; margin-bottom:18px; color:#fff; border-radius:16px; background:linear-gradient(120deg,#0750bc,#1788ef); box-shadow:0 14px 32px rgba(18,102,227,.18); }
.page-intro h2 { margin:2px 0 6px; font-size:24px; }
.page-intro p { margin:0; opacity:.82; }
.page-intro .page-kicker { font-size:11px; font-weight:800; letter-spacing:.18em; opacity:.66; }
.page-intro :deep(.el-button) { background:#fff; color:#0757c7; border-color:#fff; }
.query-panel { padding:16px 18px 0; margin-bottom:14px; border:1px solid #dce9f8; border-radius:12px; background:#f8fbff; }
.agent-cell { display:flex; align-items:center; gap:11px; }
.agent-cell small { display:block; margin-top:3px; color:#8ca0b8; font-size:11px; }
.agent-mark { display:grid; place-items:center; width:38px; height:38px; border-radius:11px; color:#1266e3; font-size:12px; font-weight:900; background:#e8f2ff; border:1px solid #d4e7ff; }
.detail-toolbar { display:flex; justify-content:space-between; align-items:center; gap:16px; margin-bottom:12px; color:#657991; font-size:13px; }.detail-actions { display:flex; gap:8px; flex-shrink:0; }.required-mark { color:#f56c6c; font-weight:700; }
.description-editor-item :deep(.el-form-item__content) { display:block; }
.description-editor-item :deep(.editor) { width:100%; }
.editor-mode-bar { display:flex; align-items:center; justify-content:space-between; gap:16px; margin-bottom:12px; }
.editor-mode-bar>span { color:#71849a; font-size:12px; }
.markdown-workbench { display:grid; grid-template-columns:1fr 1fr; gap:14px; width:100%; }
.markdown-workbench :deep(.el-textarea__inner) { min-height:338px!important; font-family:"SFMono-Regular",Consolas,"Liberation Mono",monospace; font-size:13px; line-height:1.65; }
.markdown-preview { min-height:338px; max-height:520px; overflow:auto; padding:18px 20px; border:1px solid #d8e5f3; border-radius:8px; color:#30465f; background:#f8fbff; line-height:1.7; }
.rich-preview :deep(> *:first-child) { margin-top:0; }
.rich-preview :deep(h1),.rich-preview :deep(h2),.rich-preview :deep(h3) { color:#173d68; line-height:1.35; }
.rich-preview :deep(blockquote) { margin:12px 0; padding:8px 14px; border-left:3px solid #3187dc; background:#eaf4ff; }
.rich-preview :deep(pre) { overflow:auto; padding:12px; border-radius:7px; color:#dcecff; background:#102c4d; }
.rich-preview :deep(code) { font-family:"SFMono-Regular",Consolas,monospace; }
.rich-preview :deep(table) { width:100%; border-collapse:collapse; }
.rich-preview :deep(th),.rich-preview :deep(td) { padding:8px 10px; border:1px solid #d7e4f2; text-align:left; }
.rich-preview :deep(th) { background:#e9f3fe; }
.field-tip { display:block; margin-top:8px; color:#8a9bb0; font-size:12px; line-height:1.5; }
@media (max-width:900px) { .markdown-workbench { grid-template-columns:1fr; } }
</style>
