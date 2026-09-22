<template>
  <div class="app-container">
    <section class="article-banner"><div><p>OFFICIAL SITE OPERATIONS</p><h2>新闻内容与发布</h2><span>新闻列表与详情页由同一已发布版本提供数据；保存草稿不影响官网访客。</span></div><el-button type="primary" icon="Plus" @click="handleAdd" v-hasPermi="['site:article:add']">新增新闻</el-button></section>
    <el-form ref="queryRef" :model="queryParams" :inline="true">
      <el-form-item label="新闻编码"><el-input v-model="queryParams.articleCode" clearable @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item label="分类"><el-select v-model="queryParams.categoryCode" clearable><el-option v-for="item in categories" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="articleList" class="article-table">
      <el-table-column label="新闻" min-width="260"><template #default="scope"><strong>{{ scope.row.articleCode }}</strong><small>{{ scope.row.legacyPath }}</small></template></el-table-column>
      <el-table-column label="分类" min-width="120"><template #default="scope"><el-tag effect="plain">{{ scope.row.categoryName || scope.row.categoryCode }}</el-tag></template></el-table-column>
      <el-table-column label="草稿" width="105"><template #default="scope"><el-tag :type="scope.row.draftRevisionId ? 'warning' : 'info'" effect="plain">{{ scope.row.draftRevisionId ? '待发布' : '无草稿' }}</el-tag></template></el-table-column>
      <el-table-column label="前台版本" width="110"><template #default="scope"><el-tag :type="scope.row.publishedRevisionId ? 'success' : 'info'" effect="plain">{{ scope.row.publishedRevisionId ? '已发布' : '未发布' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="190" fixed="right"><template #default="scope"><el-button link type="primary" icon="Edit" @click="handleEdit(scope.row)" v-hasPermi="['site:article:edit']">编辑</el-button><el-button v-if="scope.row.draftRevisionId" link type="success" icon="Upload" @click="handlePublish(scope.row)" v-hasPermi="['site:article:publish']">发布</el-button><el-button v-else-if="scope.row.publishedRevisionId" link type="danger" icon="Remove" @click="handleUnpublish(scope.row)" v-hasPermi="['site:article:publish']">下架</el-button></template></el-table-column>
    </el-table>
    <div v-if="!loading && !articleList.length" class="empty"><strong>尚未导入新闻</strong><span>运行官网迁移包后，原有文章将会在这里统一维护。</span></div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    <el-dialog :title="dialogTitle" v-model="open" width="1080px" append-to-body destroy-on-close>
      <el-alert v-if="form.publishedRevisionId" type="info" :closable="false" show-icon class="mb16" title="当前新闻已有公开版本。本次保存只会更新独立草稿，确认发布后才切换前台。" />
      <el-form ref="articleRef" :model="form" :rules="rules" label-width="96px"><el-row :gutter="18">
        <el-col :span="8"><el-form-item label="新闻编码" prop="articleCode"><el-input v-model="form.articleCode" :disabled="Boolean(form.articleId)" @input="normalizeCode" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="分类" prop="categoryCode"><el-select v-model="form.categoryCode" style="width:100%"><el-option v-for="item in categories" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="发布日期"><el-date-picker v-model="form.publishedAt" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col>
        <el-col :span="24"><el-form-item label="公开路径" prop="legacyPath"><el-input v-model="form.legacyPath" placeholder="/news-articles/a54.html" /></el-form-item></el-col>
        <el-col :span="24"><el-form-item label="新闻标题" prop="title"><el-input v-model="form.title" /></el-form-item></el-col>
        <el-col :span="24"><el-form-item label="新闻摘要"><el-input v-model="form.summary" type="textarea" :rows="2" /></el-form-item></el-col>
        <el-col :span="24"><el-form-item label="新闻正文" prop="bodyHtml"><el-input v-model="form.bodyHtml" type="textarea" :rows="15" placeholder="受控正文 HTML；不接受 script、style 或事件属性。" /></el-form-item></el-col>
        <el-col :span="24"><el-form-item label="SEO 配置"><el-input v-model="form.seoJson" type="textarea" :rows="3" placeholder='例如 {"description":"…"}' /></el-form-item></el-col>
        <el-col :span="24"><el-form-item label="内部备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item></el-col>
      </el-row></el-form>
      <template #footer><el-button type="primary" @click="submitForm">保存草稿</el-button><el-button @click="cancel">取消</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup name="SiteArticle">
import { addSiteArticle, getSiteArticle, listSiteArticles, publishSiteArticle, unpublishSiteArticle, updateSiteArticle } from '@/api/site/article'
const { proxy } = getCurrentInstance()
const categories = [{ label: '企业新闻', value: 'corp' }, { label: '技术科普', value: 'tech' }, { label: '行业资讯', value: 'ind' }]
const loading = ref(false), open = ref(false), articleList = ref([]), total = ref(0), dialogTitle = ref('')
const emptyForm = () => ({ articleId: undefined, articleCode: '', legacyPath: '/news-articles/a54.html', categoryCode: 'corp', title: '', summary: '', bodyHtml: '', seoJson: '', publishedAt: '', remark: '', draftRevisionId: undefined, publishedRevisionId: undefined })
const data = reactive({ form: emptyForm(), queryParams: { pageNum: 1, pageSize: 20, articleCode: undefined, categoryCode: undefined }, rules: { articleCode: [{ required: true, message: '新闻编码不能为空', trigger: 'blur' }, { pattern: /^[A-Z][A-Z0-9_]*$/, message: '仅支持大写字母、数字和下划线', trigger: 'blur' }], legacyPath: [{ required: true, message: '公开路径不能为空', trigger: 'blur' }], categoryCode: [{ required: true, message: '新闻分类不能为空', trigger: 'change' }], title: [{ required: true, message: '新闻标题不能为空', trigger: 'blur' }] } })
const { form, queryParams, rules } = toRefs(data)
function getList() { loading.value = true; listSiteArticles(queryParams.value).then(res => { articleList.value = res.rows || []; total.value = res.total || 0 }).finally(() => { loading.value = false }) }
function reset() { form.value = emptyForm(); proxy.resetForm('articleRef') }
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
function normalizeCode(value) { form.value.articleCode = String(value || '').toUpperCase().replace(/[^A-Z0-9_]/g, '') }
function handleAdd() { reset(); dialogTitle.value = '新增官网新闻草稿'; open.value = true }
function handleEdit(row) { reset(); getSiteArticle(row.articleId).then(res => { form.value = { ...emptyForm(), ...res.data }; dialogTitle.value = `编辑官网新闻 · ${res.data.articleCode}`; open.value = true }) }
function cancel() { open.value = false; reset() }
function submitForm() { proxy.$refs.articleRef.validate(valid => { if (!valid) return; const request = form.value.articleId ? updateSiteArticle(form.value) : addSiteArticle(form.value); request.then(res => { form.value = { ...form.value, ...res.data }; proxy.$modal.msgSuccess('草稿已保存，尚未影响官网访客'); open.value = false; getList() }) }) }
function handlePublish(row) { proxy.$modal.confirm(`确认直接发布「${row.articleCode}」的当前草稿吗？访客将立即读取新版本。`).then(() => publishSiteArticle(row.articleId)).then(() => { proxy.$modal.msgSuccess('已发布'); getList() }) }
function handleUnpublish(row) { proxy.$modal.confirm(`确认下架「${row.articleCode}」吗？访客将无法继续读取该新闻。`).then(() => unpublishSiteArticle(row.articleId)).then(() => { proxy.$modal.msgSuccess('已下架'); getList() }) }
getList()
</script>

<style scoped lang="scss">.article-banner{display:flex;justify-content:space-between;gap:24px;align-items:center;padding:22px 24px;margin-bottom:18px;color:#fff;border-radius:16px;background:linear-gradient(120deg,#10294f,#116f9d 60%,#24a4bb)}.article-banner p{margin:0 0 4px;font-size:11px;letter-spacing:1.4px;opacity:.75}.article-banner h2{margin:0 0 5px;font-size:24px}.article-banner span{font-size:13px;opacity:.9}.article-table strong,.article-table small{display:block}.article-table small{margin-top:3px;color:#909399;font-size:11px}.empty{padding:54px 0;text-align:center;color:#909399}.empty strong,.empty span{display:block}.empty span{margin-top:8px;font-size:13px}@media(max-width:700px){.article-banner{display:block}.article-banner .el-button{margin-top:14px}}</style>
