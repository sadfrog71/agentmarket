<template>
  <div class="app-container category-page">
    <div class="category-banner">
      <div>
        <p>PRIMARY CATEGORY</p>
        <h2>一级分类管理</h2>
        <span>维护智能体市场的一级业务分类，前台分类展示与智能体归类使用同一套数据。</span>
      </div>
      <div class="category-route"><span>维护分类</span><i>→</i><span>关联智能体</span><i>→</i><span>前台展示</span></div>
      <el-button type="primary" icon="Plus" @click="handleAdd" v-hasPermi="['market:category:add']">新增分类</el-button>
    </div>

    <el-form ref="queryRef" :model="queryParams" :inline="true" class="query-panel" v-show="showSearch">
      <el-form-item label="分类名称" prop="categoryName"><el-input v-model="queryParams.categoryName" placeholder="供水 / 排水 / 燃气" clearable @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item label="状态" prop="status"><el-select v-model="queryParams.status" placeholder="全部状态" clearable style="width:150px"><el-option label="启用" value="0" /><el-option label="停用" value="1" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['market:category:edit']">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['market:category:remove']">删除</el-button></el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="categoryList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="52" align="center" />
      <el-table-column label="分类" min-width="220">
        <template #default="scope"><div class="category-cell"><span class="category-mark">{{ String(scope.row.sortNo ?? 0).padStart(2, '0') }}</span><div><strong>{{ scope.row.categoryName }}</strong><small>{{ scope.row.categoryCode }}</small></div></div></template>
      </el-table-column>
      <el-table-column label="分类说明" prop="description" min-width="330" show-overflow-tooltip />
      <el-table-column label="显示顺序" prop="sortNo" width="100" align="center" />
      <el-table-column label="状态" prop="status" width="100"><template #default="scope"><el-tag :type="scope.row.status === '0' ? 'success' : 'info'">{{ scope.row.status === '0' ? '启用' : '停用' }}</el-tag></template></el-table-column>
      <el-table-column label="更新时间" prop="updateTime" width="170"><template #default="scope">{{ parseTime(scope.row.updateTime || scope.row.createTime) }}</template></el-table-column>
      <el-table-column label="操作" width="145" fixed="right" align="center"><template #default="scope"><el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['market:category:edit']">编辑</el-button><el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['market:category:remove']">删除</el-button></template></el-table-column>
    </el-table>
    <div v-if="!loading && !categoryList.length" class="category-empty"><strong>暂无一级分类</strong><span>新增分类后，前台分类展示和智能体编辑页会自动读取。</span></div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="650px" append-to-body destroy-on-close>
      <el-form ref="categoryRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="分类名称" prop="categoryName" required><el-input v-model="form.categoryName" placeholder="如：供水" /></el-form-item>
        <el-form-item label="分类编码" prop="categoryCode" required><el-input v-model="form.categoryCode" :disabled="Boolean(form.categoryId)" placeholder="英文小写，如 water" @input="normalizeCode" /><span class="field-tip">编码创建后不可修改，只支持小写英文、数字、下划线和短横线。</span></el-form-item>
        <el-form-item label="状态" prop="status" required><el-radio-group v-model="form.status"><el-radio value="0">启用</el-radio><el-radio value="1">停用</el-radio></el-radio-group></el-form-item>
        <el-form-item label="显示顺序"><el-input-number v-model="form.sortNo" :min="0" /></el-form-item>
        <el-form-item label="分类说明"><el-input v-model="form.description" type="textarea" :rows="3" placeholder="用于说明分类覆盖的业务范围" /></el-form-item>
        <el-form-item label="前端图标"><el-input v-model="form.iconCode" placeholder="如 water / drainage / gas" /></el-form-item>
        <el-form-item label="内部备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer><div class="dialog-footer"><el-button type="primary" @click="submitForm">保存分类</el-button><el-button @click="cancel">取消</el-button></div></template>
    </el-dialog>
  </div>
</template>

<script setup name="MarketCategory">
import { listCategory, getCategory, addCategory, updateCategory, delCategory } from '@/api/market/category'

const { proxy } = getCurrentInstance()
const categoryList = ref([])
const loading = ref(false)
const open = ref(false)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref('')
const emptyForm = () => ({ categoryId: undefined, categoryCode: '', categoryName: '', description: '', iconCode: 'water', sortNo: 0, status: '0', remark: '' })
const data = reactive({
  form: emptyForm(),
  queryParams: { pageNum: 1, pageSize: 10, categoryName: undefined, status: undefined },
  rules: {
    categoryName: [{ required: true, message: '分类名称不能为空', trigger: 'blur' }],
    categoryCode: [{ required: true, message: '分类编码不能为空', trigger: 'blur' }, { pattern: /^[a-z][a-z0-9_-]{1,63}$/, message: '请输入小写英文开头的分类编码', trigger: 'blur' }],
    status: [{ required: true, message: '请选择状态', trigger: 'change' }]
  }
})
const { form, queryParams, rules } = toRefs(data)

function getList() { loading.value = true; listCategory(queryParams.value).then(res => { categoryList.value = res.rows || []; total.value = res.total || 0 }).finally(() => { loading.value = false }) }
function reset() { form.value = emptyForm(); proxy.resetForm('categoryRef') }
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
function handleSelectionChange(selection) { ids.value = selection.map(item => item.categoryId); single.value = selection.length !== 1; multiple.value = !selection.length }
function normalizeCode(value) { form.value.categoryCode = String(value || '').toLowerCase().replace(/[^a-z0-9_-]/g, '') }
function handleAdd() { reset(); title.value = '新增一级分类'; open.value = true }
function handleUpdate(row) { reset(); const id = row.categoryId || ids.value[0]; getCategory(id).then(res => { form.value = { ...emptyForm(), ...res.data }; title.value = `编辑分类 · ${res.data.categoryName}`; open.value = true }) }
function cancel() { open.value = false; reset() }
function submitForm() { proxy.$refs.categoryRef.validate(valid => { if (!valid) return; const request = form.value.categoryId ? updateCategory(form.value) : addCategory(form.value); request.then(() => { proxy.$modal.msgSuccess(form.value.categoryId ? '分类已更新' : '分类已创建'); open.value = false; getList() }) }) }
function handleDelete(row) { const targetIds = row.categoryId || ids.value; proxy.$modal.confirm('删除分类后，若仍有关联智能体将无法删除，是否继续？').then(() => delCategory(targetIds)).then(() => { proxy.$modal.msgSuccess('已删除'); getList() }) }

getList()
</script>

<style scoped lang="scss">
.category-page { --market-blue: #1266e3; }
.category-banner { display:grid; grid-template-columns:1fr auto auto; gap:24px; align-items:center; padding:22px 24px; margin-bottom:18px; color:#fff; border-radius:16px; background:linear-gradient(118deg,#0754bd,#1588e8 64%,#3db6f2); box-shadow:0 14px 34px rgba(18,102,227,.18); }
.category-banner p { margin:0 0 4px; font-size:10px; font-weight:850; letter-spacing:.18em; opacity:.68; }.category-banner h2 { margin:0 0 5px; font-size:24px; }.category-banner>div>span { font-size:13px; opacity:.8; }.category-banner :deep(.el-button) { color:#0759b7; border-color:#fff; background:#fff; }
.category-route { display:flex; align-items:center; gap:8px; padding:12px 15px; border:1px solid rgba(255,255,255,.2); border-radius:11px; background:rgba(255,255,255,.08); }.category-route span { padding:5px 7px; border-radius:6px; background:rgba(255,255,255,.1); font-size:11px; font-weight:750; }.category-route i { font-style:normal; opacity:.55; }
.query-panel { padding:16px 18px 0; margin-bottom:14px; border:1px solid #dce9f8; border-radius:12px; background:#f8fbff; }.category-cell { display:flex; align-items:center; gap:11px; }.category-cell strong,.category-cell small { display:block; }.category-cell small { margin-top:3px; color:#8ca0b8; font-size:11px; }.category-mark { display:grid; place-items:center; width:38px; height:38px; border-radius:11px; color:#1266e3; font-weight:850; background:#e8f2ff; border:1px solid #d4e7ff; }.category-empty { margin-top:-1px; padding:32px; text-align:center; color:#70849c; border:1px dashed #c9dced; border-radius:0 0 12px 12px; }.category-empty strong,.category-empty span { display:block; }.category-empty strong { margin-bottom:6px; color:#274561; }.field-tip { display:block; margin-top:5px; color:#8a9bb0; font-size:12px; line-height:1.5; }
@media (max-width:1050px) { .category-banner { grid-template-columns:1fr auto; }.category-route { grid-column:1/-1; grid-row:2; justify-content:center; } }
</style>
