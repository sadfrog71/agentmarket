<template>
  <div class="app-container site-page-manager">
    <section class="site-banner">
      <div>
        <p>OFFICIAL SITE OPERATIONS</p>
        <h2>官网页面与发布</h2>
        <span>固定前台模板保持原型结构；保存草稿不影响访客，发布时才切换公开版本。</span>
      </div>
      <div class="site-banner-rules"><span>保存草稿</span><b>→</b><span>核对内容</span><b>→</b><span>直接发布</span></div>
      <el-button type="primary" icon="Plus" @click="handleAdd" v-hasPermi="['site:page:add']">新增页面</el-button>
    </section>

    <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch" class="query-panel">
      <el-form-item label="页面编码" prop="pageCode"><el-input v-model="queryParams.pageCode" placeholder="如 HOME" clearable @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item label="公开路径" prop="routePath"><el-input v-model="queryParams.routePath" placeholder="如 /index.html" clearable @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="pageList" class="site-table">
      <el-table-column label="页面" min-width="210">
        <template #default="scope"><div class="page-code"><span>{{ scope.row.pageCode.slice(0, 1) }}</span><div><strong>{{ scope.row.pageCode }}</strong><small>{{ scope.row.templateCode }}</small></div></div></template>
      </el-table-column>
      <el-table-column label="既有公开路径" prop="routePath" min-width="220" />
      <el-table-column label="草稿" width="105"><template #default="scope"><el-tag :type="scope.row.draftRevisionId ? 'warning' : 'info'" effect="plain">{{ scope.row.draftRevisionId ? '待发布' : '无草稿' }}</el-tag></template></el-table-column>
      <el-table-column label="前台版本" width="110"><template #default="scope"><el-tag :type="scope.row.publishedRevisionId ? 'success' : 'info'" effect="plain">{{ scope.row.publishedRevisionId ? '已发布' : '未发布' }}</el-tag></template></el-table-column>
      <el-table-column label="更新时间" prop="updateTime" width="170"><template #default="scope">{{ parseTime(scope.row.updateTime || scope.row.createTime) }}</template></el-table-column>
      <el-table-column label="操作" width="190" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleEdit(scope.row)" v-hasPermi="['site:page:edit']">编辑</el-button>
          <el-button v-if="scope.row.draftRevisionId" link type="success" icon="Upload" @click="handlePublish(scope.row)" v-hasPermi="['site:page:publish']">发布</el-button>
          <el-button v-else-if="scope.row.publishedRevisionId" link type="danger" icon="Remove" @click="handleUnpublish(scope.row)" v-hasPermi="['site:page:publish']">下架</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div v-if="!loading && !pageList.length" class="site-empty"><strong>尚未导入官网页面</strong><span>迁移工具导入后，所有既有页面会在此统一维护。</span></div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="dialogTitle" v-model="open" width="1080px" append-to-body destroy-on-close>
      <el-alert v-if="form.publishedRevisionId" type="info" :closable="false" show-icon class="mb16" title="当前页面已有公开版本。本次保存会创建或更新独立草稿，访客不会看到编辑中的内容。" />
      <el-form ref="pageRef" :model="form" :rules="rules" label-width="96px">
        <el-row :gutter="18">
          <el-col :span="8"><el-form-item label="页面编码" prop="pageCode"><el-input v-model="form.pageCode" placeholder="如 HOME" :disabled="Boolean(form.pageId)" @input="normalizeCode" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="公开路径" prop="routePath"><el-input v-model="form.routePath" placeholder="/index.html" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="固定模板" prop="templateCode"><el-input v-model="form.templateCode" placeholder="如 LEGACY_PAGE" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="页面标题" prop="title"><el-input v-model="form.title" placeholder="用于浏览器标题和页面内容核对" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="页面摘要"><el-input v-model="form.subtitle" type="textarea" :rows="2" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="页面主体" prop="bodyHtml"><el-input v-model="form.bodyHtml" type="textarea" :rows="15" placeholder="固定前台模板内的受控页面主体 HTML；不接受 script、style 或事件属性。" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="SEO 配置"><el-input v-model="form.seoJson" type="textarea" :rows="3" placeholder='例如 {"description":"…"}' /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="内部备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer><div class="dialog-footer"><el-button type="primary" @click="submitForm">保存草稿</el-button><el-button @click="cancel">取消</el-button></div></template>
    </el-dialog>
  </div>
</template>

<script setup name="SitePage">
import { addSitePage, getSitePage, listSitePages, publishSitePage, unpublishSitePage, updateSitePage } from '@/api/site/page'

const { proxy } = getCurrentInstance()
const loading = ref(false)
const open = ref(false)
const showSearch = ref(true)
const pageList = ref([])
const total = ref(0)
const dialogTitle = ref('')
const emptyForm = () => ({ pageId: undefined, pageCode: '', routePath: '/index.html', templateCode: 'LEGACY_PAGE', title: '', subtitle: '', bodyHtml: '', seoJson: '', remark: '', draftRevisionId: undefined, publishedRevisionId: undefined })
const data = reactive({
  form: emptyForm(),
  queryParams: { pageNum: 1, pageSize: 20, pageCode: undefined, routePath: undefined },
  rules: {
    pageCode: [{ required: true, message: '页面编码不能为空', trigger: 'blur' }, { pattern: /^[A-Z][A-Z0-9_]*$/, message: '仅支持大写字母、数字和下划线', trigger: 'blur' }],
    routePath: [{ required: true, message: '公开路径不能为空', trigger: 'blur' }],
    templateCode: [{ required: true, message: '固定模板不能为空', trigger: 'blur' }],
    title: [{ required: true, message: '页面标题不能为空', trigger: 'blur' }]
  }
})
const { form, queryParams, rules } = toRefs(data)

function getList() { loading.value = true; listSitePages(queryParams.value).then(res => { pageList.value = res.rows || []; total.value = res.total || 0 }).finally(() => { loading.value = false }) }
function reset() { form.value = emptyForm(); proxy.resetForm('pageRef') }
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
function normalizeCode(value) { form.value.pageCode = String(value || '').toUpperCase().replace(/[^A-Z0-9_]/g, '') }
function handleAdd() { reset(); dialogTitle.value = '新增官网页面草稿'; open.value = true }
function handleEdit(row) { reset(); getSitePage(row.pageId).then(res => { form.value = { ...emptyForm(), ...res.data }; dialogTitle.value = `编辑官网页面 · ${res.data.pageCode}`; open.value = true }) }
function cancel() { open.value = false; reset() }
function submitForm() { proxy.$refs.pageRef.validate(valid => { if (!valid) return; const request = form.value.pageId ? updateSitePage(form.value) : addSitePage(form.value); request.then(res => { form.value = { ...form.value, ...res.data }; proxy.$modal.msgSuccess('草稿已保存，尚未影响官网访客'); open.value = false; getList() }) }) }
function handlePublish(row) { proxy.$modal.confirm(`确认直接发布「${row.pageCode}」的当前草稿吗？访客将立即读取新版本。`).then(() => publishSitePage(row.pageId)).then(() => { proxy.$modal.msgSuccess('已发布'); getList() }) }
function handleUnpublish(row) { proxy.$modal.confirm(`确认下架「${row.pageCode}」吗？访客将无法继续读取该页面。`).then(() => unpublishSitePage(row.pageId)).then(() => { proxy.$modal.msgSuccess('已下架'); getList() }) }

getList()
</script>

<style scoped lang="scss">
.site-banner { display:grid; grid-template-columns:1fr auto auto; align-items:center; gap:24px; padding:22px 24px; margin-bottom:18px; color:#fff; border-radius:16px; background:linear-gradient(120deg,#10294f,#116f9d 60%,#24a4bb); box-shadow:0 14px 34px rgba(16,67,113,.16); }
.site-banner p { margin:0 0 4px; font-size:11px; letter-spacing:1.4px; opacity:.75; }.site-banner h2 { margin:0 0 5px; font-size:24px; }.site-banner span { font-size:13px; opacity:.9; }.site-banner-rules { display:flex; gap:9px; align-items:center; white-space:nowrap; font-size:13px; }.site-banner-rules b { opacity:.65; }.page-code { display:flex; gap:10px; align-items:center; }.page-code > span { display:grid; place-items:center; width:30px; height:30px; border-radius:9px; color:#0b6d94; background:#e7f6fa; font-weight:700; }.page-code strong,.page-code small { display:block; }.page-code small { margin-top:2px; color:#909399; font-size:11px; }.site-empty { padding:54px 0; text-align:center; color:#909399; }.site-empty strong,.site-empty span { display:block; }.site-empty span { margin-top:8px; font-size:13px; }
@media (max-width:900px) { .site-banner { grid-template-columns:1fr; gap:14px; }.site-banner-rules { white-space:normal; } }
</style>
