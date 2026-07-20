<template>
  <div class="app-container content-page">
    <div class="content-banner">
      <div>
        <p>CONTENT OPERATIONS</p>
        <h2>页面内容管理</h2>
        <span>维护算力中心、联系我们及后续前台静态页面，支持富文本和 Markdown。</span>
      </div>
      <div class="content-guide"><span>编辑内容</span><i>→</i><span>预览排版</span><i>→</i><span>发布前台</span></div>
      <el-button type="primary" icon="Plus" @click="handleAdd" v-hasPermi="['market:content:add']">新增页面</el-button>
    </div>

    <el-form ref="queryRef" :model="queryParams" :inline="true" class="query-panel" v-show="showSearch">
      <el-form-item label="内容名称" prop="contentName"><el-input v-model="queryParams.contentName" placeholder="算力中心 / 联系我们" clearable @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item label="发布状态" prop="publishStatus"><el-select v-model="queryParams.publishStatus" placeholder="全部状态" clearable style="width:150px"><el-option v-for="dict in market_publish_status" :key="dict.value" :label="dict.label" :value="dict.value" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['market:content:edit']">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['market:content:remove']">删除</el-button></el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="contentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="52" align="center" />
      <el-table-column label="页面内容" min-width="230">
        <template #default="scope"><div class="content-cell"><span class="page-mark">{{ scope.row.contentKey === 'CONTACT' ? '联' : '算' }}</span><div><strong>{{ scope.row.contentName }}</strong><small>{{ scope.row.contentKey }}</small></div></div></template>
      </el-table-column>
      <el-table-column label="前台标题" prop="title" min-width="210" show-overflow-tooltip />
      <el-table-column label="编辑格式" prop="contentFormat" width="120"><template #default="scope"><el-tag :type="scope.row.contentFormat === 'MARKDOWN' ? 'primary' : 'success'" effect="plain">{{ scope.row.contentFormat === 'MARKDOWN' ? 'Markdown' : '富文本' }}</el-tag></template></el-table-column>
      <el-table-column label="内容状态" width="120"><template #default="scope"><span :class="['content-state', { empty: !scope.row.content }]">{{ scope.row.content ? '已有内容' : '暂无内容' }}</span></template></el-table-column>
      <el-table-column label="发布状态" prop="publishStatus" width="110"><template #default="scope"><dict-tag :options="market_publish_status" :value="scope.row.publishStatus" /></template></el-table-column>
      <el-table-column label="更新时间" prop="updateTime" width="170"><template #default="scope">{{ parseTime(scope.row.updateTime || scope.row.createTime) }}</template></el-table-column>
      <el-table-column label="操作" width="100" fixed="right" align="center"><template #default="scope"><el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['market:content:edit']">编辑</el-button></template></el-table-column>
    </el-table>
    <div v-if="!loading && !contentList.length" class="content-empty"><strong>暂无页面内容</strong><span>新增页面内容后，可通过公开接口发布到前台。</span></div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="1040px" append-to-body destroy-on-close>
      <el-form ref="contentRef" :model="form" :rules="rules" label-width="96px">
        <el-row :gutter="18">
          <el-col :span="8"><el-form-item label="内容名称" prop="contentName"><el-input v-model="form.contentName" placeholder="后台识别名称" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="页面标识" prop="contentKey"><el-input v-model="form.contentKey" placeholder="如 CONTACT" :disabled="Boolean(form.contentId)" @input="uppercaseKey" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="发布状态" prop="publishStatus"><el-select v-model="form.publishStatus" style="width:100%"><el-option v-for="dict in market_publish_status" :key="dict.value" :label="dict.label" :value="dict.value" /></el-select></el-form-item></el-col>
          <el-col :span="16"><el-form-item label="前台标题" prop="title"><el-input v-model="form.title" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="显示顺序"><el-input-number v-model="form.sortNo" :min="0" style="width:100%" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="副标题"><el-input v-model="form.subtitle" type="textarea" :rows="2" placeholder="前台标题下方的简短说明" /></el-form-item></el-col>
          <el-col :span="24">
            <el-form-item label="页面正文" class="body-editor-item">
              <div class="editor-mode-bar">
                <el-radio-group v-model="form.contentFormat" size="small">
                  <el-radio-button value="RICH_TEXT">富文本</el-radio-button>
                  <el-radio-button value="MARKDOWN">Markdown</el-radio-button>
                </el-radio-group>
                <span>{{ form.contentFormat === 'MARKDOWN' ? '左侧编写 Markdown，右侧实时预览' : '使用工具栏完成标题、列表、图片和排版' }}</span>
              </div>
              <Editor v-if="form.contentFormat === 'RICH_TEXT'" v-model="form.content" :min-height="260" />
              <div v-else class="markdown-workbench">
                <el-input v-model="form.content" type="textarea" :rows="18" resize="vertical" placeholder="# 标题\n\n支持 **加粗**、列表、引用、代码块和表格。" />
                <div class="markdown-preview" v-html="markdownPreview"></div>
              </div>
              <span class="field-tip">发布后前台按当前格式渲染；内容为空或状态不是「已发布」时，前台显示暂无数据或默认提示。</span>
            </el-form-item>
          </el-col>
          <el-col :span="24"><el-form-item label="内部备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer><div class="dialog-footer"><el-button type="primary" @click="submitForm">保存内容</el-button><el-button @click="cancel">取消</el-button></div></template>
    </el-dialog>
  </div>
</template>

<script setup name="MarketContent">
import { listContent, getContent, addContent, updateContent, delContent } from '@/api/market/content'
import { renderMarkdown } from '@/utils/markdown'

const { proxy } = getCurrentInstance()
const { market_publish_status } = useDict('market_publish_status')
const contentList = ref([])
const loading = ref(false)
const open = ref(false)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref('')

const emptyForm = () => ({ contentId: undefined, contentKey: '', contentName: '', title: '', subtitle: '', content: '', contentFormat: 'MARKDOWN', publishStatus: '0', sortNo: 0, remark: '' })
const data = reactive({
  form: emptyForm(),
  queryParams: { pageNum: 1, pageSize: 10, contentName: undefined, publishStatus: undefined },
  rules: {
    contentName: [{ required: true, message: '内容名称不能为空', trigger: 'blur' }],
    contentKey: [{ required: true, message: '页面标识不能为空', trigger: 'blur' }, { pattern: /^[A-Z][A-Z0-9_]*$/, message: '仅支持大写英文、数字和下划线', trigger: 'blur' }],
    title: [{ required: true, message: '前台标题不能为空', trigger: 'blur' }],
    publishStatus: [{ required: true, message: '请选择发布状态', trigger: 'change' }]
  }
})
const { form, queryParams, rules } = toRefs(data)
const markdownPreview = computed(() => renderMarkdown(form.value.content))

function getList() { loading.value = true; listContent(queryParams.value).then(res => { contentList.value = res.rows || []; total.value = res.total || 0 }).finally(() => { loading.value = false }) }
function reset() { form.value = emptyForm(); proxy.resetForm('contentRef') }
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
function handleSelectionChange(selection) { ids.value = selection.map(item => item.contentId); single.value = selection.length !== 1; multiple.value = !selection.length }
function uppercaseKey(value) { form.value.contentKey = String(value || '').toUpperCase().replace(/[^A-Z0-9_]/g, '') }
function handleAdd() { reset(); title.value = '新增页面内容'; open.value = true }
function handleUpdate(row) { reset(); const id = row.contentId || ids.value[0]; getContent(id).then(res => { form.value = { ...emptyForm(), ...res.data }; title.value = `编辑页面 · ${res.data.contentName}`; open.value = true }) }
function cancel() { open.value = false; reset() }
function submitForm() { proxy.$refs.contentRef.validate(valid => { if (!valid) return; const request = form.value.contentId ? updateContent(form.value) : addContent(form.value); request.then(() => { proxy.$modal.msgSuccess(form.value.contentId ? '页面内容已更新' : '页面内容已创建'); open.value = false; getList() }) }) }
function handleDelete(row) { const targetIds = row.contentId || ids.value; proxy.$modal.confirm('删除后前台将无法读取所选页面内容，是否继续？').then(() => delContent(targetIds)).then(() => { proxy.$modal.msgSuccess('已删除'); getList() }) }

getList()
</script>

<style scoped lang="scss">
.content-banner { display:grid; grid-template-columns:1fr auto auto; gap:24px; align-items:center; padding:22px 24px; margin-bottom:18px; color:#fff; border-radius:16px; background:linear-gradient(118deg,#0754bd,#1588e8 64%,#3db6f2); box-shadow:0 14px 34px rgba(18,102,227,.18); }
.content-banner p { margin:0 0 4px; font-size:10px; font-weight:850; letter-spacing:.18em; opacity:.68; }.content-banner h2 { margin:0 0 5px; font-size:24px; }.content-banner>div>span { font-size:13px; opacity:.8; }.content-banner :deep(.el-button) { color:#0759b7; border-color:#fff; background:#fff; }
.content-guide { display:flex; align-items:center; gap:8px; padding:12px 15px; border:1px solid rgba(255,255,255,.2); border-radius:11px; background:rgba(255,255,255,.08); }.content-guide span { padding:5px 7px; border-radius:6px; background:rgba(255,255,255,.1); font-size:11px; font-weight:750; }.content-guide i { font-style:normal; opacity:.55; }
.query-panel { padding:16px 18px 0; margin-bottom:14px; border:1px solid #dce9f8; border-radius:12px; background:#f8fbff; }
.content-cell { display:flex; align-items:center; gap:11px; }.content-cell strong,.content-cell small { display:block; }.content-cell small { margin-top:3px; color:#8ca0b8; font-size:11px; }.page-mark { display:grid; place-items:center; width:38px; height:38px; border-radius:11px; color:#1266e3; font-weight:850; background:#e8f2ff; border:1px solid #d4e7ff; }
.content-state { color:#2e9c63; font-size:12px; }.content-state.empty { color:#9aabbc; }
.content-empty { margin-top:-1px; padding:32px; text-align:center; color:#70849c; border:1px dashed #c9dced; border-radius:0 0 12px 12px; }.content-empty strong,.content-empty span { display:block; }.content-empty strong { margin-bottom:6px; color:#274561; }
.body-editor-item :deep(.el-form-item__content) { display:block; }.body-editor-item :deep(.editor) { width:100%; }.editor-mode-bar { display:flex; align-items:center; justify-content:space-between; gap:16px; margin-bottom:12px; }.editor-mode-bar>span { color:#71849a; font-size:12px; }
.markdown-workbench { display:grid; grid-template-columns:1fr 1fr; gap:14px; width:100%; }.markdown-workbench :deep(.el-textarea__inner) { min-height:378px!important; font-family:"SFMono-Regular",Consolas,"Liberation Mono",monospace; font-size:13px; line-height:1.65; }.markdown-preview { min-height:378px; max-height:560px; overflow:auto; padding:18px 20px; border:1px solid #d8e5f3; border-radius:8px; color:#30465f; background:#f8fbff; line-height:1.7; }.markdown-preview :deep(> *:first-child) { margin-top:0; }.markdown-preview :deep(h1),.markdown-preview :deep(h2),.markdown-preview :deep(h3) { color:#173d68; line-height:1.35; }.markdown-preview :deep(blockquote) { margin:12px 0; padding:8px 14px; border-left:3px solid #3187dc; background:#eaf4ff; }.markdown-preview :deep(pre) { overflow:auto; padding:12px; border-radius:7px; color:#dcecff; background:#102c4d; }.markdown-preview :deep(table) { width:100%; border-collapse:collapse; }.markdown-preview :deep(th),.markdown-preview :deep(td) { padding:8px 10px; border:1px solid #d7e4f2; text-align:left; }.markdown-preview :deep(th) { background:#e9f3fe; }.field-tip { display:block; margin-top:8px; color:#8a9bb0; font-size:12px; line-height:1.5; }
@media (max-width:1000px) { .content-banner { grid-template-columns:1fr auto; }.content-guide { grid-column:1/-1; grid-row:2; justify-content:center; }.markdown-workbench { grid-template-columns:1fr; } }
</style>
