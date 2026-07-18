<template>
  <div class="app-container business-page">
    <div class="business-banner">
      <div>
        <p>OFFLINE BUSINESS</p>
        <h2>商务登记与跟进</h2>
        <span>统一记录线下咨询、智能体上架和 FDE 实施需求。</span>
      </div>
      <div class="status-route" aria-label="业务处理流程">
        <span>待处理</span><i>→</i><span>跟进中</span><i>→</i><span>已确认</span><i>→</i><span>已关闭</span>
      </div>
      <el-button type="primary" icon="Plus" @click="handleAdd" v-hasPermi="['market:business:add']">新增登记</el-button>
    </div>

    <el-form ref="queryRef" :model="queryParams" :inline="true" class="query-panel" v-show="showSearch">
      <el-form-item label="单位名称" prop="companyName"><el-input v-model="queryParams.companyName" placeholder="客户或供应商" clearable @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item label="登记类型" prop="recordType"><el-select v-model="queryParams.recordType" placeholder="全部类型" clearable style="width:150px"><el-option v-for="dict in market_business_type" :key="dict.value" :label="dict.label" :value="dict.value" /></el-select></el-form-item>
      <el-form-item label="处理状态" prop="recordStatus"><el-select v-model="queryParams.recordStatus" placeholder="全部状态" clearable style="width:150px"><el-option v-for="dict in market_business_status" :key="dict.value" :label="dict.label" :value="dict.value" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['market:business:edit']">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['market:business:remove']">删除</el-button></el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="businessList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="52" align="center" />
      <el-table-column label="登记信息" min-width="230">
        <template #default="scope"><div class="record-cell"><strong>{{ scope.row.companyName }}</strong><small>{{ scope.row.recordNo }}</small></div></template>
      </el-table-column>
      <el-table-column label="类型" prop="recordType" width="120"><template #default="scope"><dict-tag :options="market_business_type" :value="scope.row.recordType" /></template></el-table-column>
      <el-table-column label="联系人" min-width="145"><template #default="scope"><div class="contact-cell"><span>{{ scope.row.contactName || '-' }}</span><small>{{ scope.row.contactPhone || scope.row.contactEmail || '-' }}</small></div></template></el-table-column>
      <el-table-column label="关联智能体" prop="agentName" min-width="160" show-overflow-tooltip />
      <el-table-column label="负责人" prop="ownerName" width="110" />
      <el-table-column label="状态" prop="recordStatus" width="110"><template #default="scope"><dict-tag :options="market_business_status" :value="scope.row.recordStatus" /></template></el-table-column>
      <el-table-column label="下次跟进" prop="nextFollowTime" width="165"><template #default="scope">{{ parseTime(scope.row.nextFollowTime) || '-' }}</template></el-table-column>
      <el-table-column label="登记时间" prop="createTime" width="165"><template #default="scope">{{ parseTime(scope.row.createTime) }}</template></el-table-column>
      <el-table-column label="操作" width="145" fixed="right" align="center"><template #default="scope"><el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['market:business:edit']">处理</el-button><el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['market:business:remove']">删除</el-button></template></el-table-column>
    </el-table>
    <div v-if="!loading && !businessList.length" class="business-empty"><strong>暂无商务登记</strong><span>线下收到咨询、上架或实施需求后，在这里新增记录。</span></div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="860px" append-to-body destroy-on-close>
      <el-form ref="businessRef" :model="form" :rules="rules" label-width="96px">
        <el-divider content-position="left">登记信息</el-divider>
        <el-row :gutter="18">
          <el-col :span="12"><el-form-item label="登记类型" prop="recordType"><el-select v-model="form.recordType" style="width:100%"><el-option v-for="dict in market_business_type" :key="dict.value" :label="dict.label" :value="dict.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="处理状态" prop="recordStatus"><el-select v-model="form.recordStatus" style="width:100%"><el-option v-for="dict in market_business_status" :key="dict.value" :label="dict.label" :value="dict.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="单位名称" prop="companyName"><el-input v-model="form.companyName" placeholder="客户或供应商名称" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="关联智能体"><el-select v-model="form.agentId" clearable filterable style="width:100%"><el-option v-for="item in agentOptions" :key="item.agentId" :label="item.agentName" :value="item.agentId" /></el-select></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="联系人"><el-input v-model="form.contactName" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="联系电话"><el-input v-model="form.contactPhone" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="联系邮箱"><el-input v-model="form.contactEmail" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="负责人"><el-select v-model="form.ownerId" clearable filterable style="width:100%"><el-option v-for="item in ownerOptions" :key="item.userId" :label="item.nickName" :value="item.userId" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="下次跟进"><el-date-picker v-model="form.nextFollowTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择时间" style="width:100%" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="需求说明"><el-input v-model="form.requirement" type="textarea" :rows="4" placeholder="记录范围、预算、部署环境和期望时间等" /></el-form-item></el-col>
        </el-row>
        <el-divider content-position="left">处理结果</el-divider>
        <el-form-item label="结果摘要"><el-input v-model="form.resultSummary" type="textarea" :rows="3" placeholder="记录已确认事项、下一步动作或关闭原因" /></el-form-item>
        <el-form-item label="内部备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer><div class="dialog-footer"><el-button type="primary" @click="submitForm">保存登记</el-button><el-button @click="cancel">取消</el-button></div></template>
    </el-dialog>
  </div>
</template>

<script setup name="MarketBusiness">
import { listBusiness, getBusiness, addBusiness, updateBusiness, delBusiness } from '@/api/market/business'
import { listAgent } from '@/api/market/agent'
import { listUser } from '@/api/system/user'

const { proxy } = getCurrentInstance()
const { market_business_type, market_business_status } = useDict('market_business_type', 'market_business_status')
const businessList = ref([])
const agentOptions = ref([])
const ownerOptions = ref([])
const loading = ref(false)
const open = ref(false)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref('')

const emptyForm = () => ({ recordId: undefined, recordType: 'INQUIRY', sourceType: 'OFFLINE', companyName: '', contactName: '', contactPhone: '', contactEmail: '', agentId: undefined, ownerId: undefined, recordStatus: 'PENDING', requirement: '', nextFollowTime: undefined, resultSummary: '', remark: '' })
const data = reactive({
  form: emptyForm(),
  queryParams: { pageNum: 1, pageSize: 10, companyName: undefined, recordType: undefined, recordStatus: undefined },
  rules: { recordType: [{ required: true, message: '请选择登记类型', trigger: 'change' }], companyName: [{ required: true, message: '单位名称不能为空', trigger: 'blur' }], recordStatus: [{ required: true, message: '请选择处理状态', trigger: 'change' }] }
})
const { form, queryParams, rules } = toRefs(data)

function getList() { loading.value = true; listBusiness(queryParams.value).then(res => { businessList.value = res.rows; total.value = res.total }).finally(() => { loading.value = false }) }
function loadOptions() { listAgent({ pageNum: 1, pageSize: 100 }).then(res => { agentOptions.value = res.rows || [] }); listUser({ pageNum: 1, pageSize: 100, status: '0' }).then(res => { ownerOptions.value = res.rows || [] }) }
function reset() { form.value = emptyForm(); proxy.resetForm('businessRef') }
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
function handleSelectionChange(selection) { ids.value = selection.map(item => item.recordId); single.value = selection.length !== 1; multiple.value = !selection.length }
function handleAdd() { reset(); title.value = '新增商务登记'; open.value = true }
function handleUpdate(row) { reset(); const id = row.recordId || ids.value[0]; getBusiness(id).then(res => { form.value = { ...emptyForm(), ...res.data }; title.value = `处理登记 · ${res.data.recordNo}`; open.value = true }) }
function cancel() { open.value = false; reset() }
function submitForm() { proxy.$refs.businessRef.validate(valid => { if (!valid) return; const request = form.value.recordId ? updateBusiness(form.value) : addBusiness(form.value); request.then(() => { proxy.$modal.msgSuccess(form.value.recordId ? '登记已更新' : '登记已创建'); open.value = false; getList() }) }) }
function handleDelete(row) { const targetIds = row.recordId || ids.value; proxy.$modal.confirm('是否删除所选商务登记？').then(() => delBusiness(targetIds)).then(() => { proxy.$modal.msgSuccess('已删除'); getList() }) }

getList()
loadOptions()
</script>

<style scoped lang="scss">
.business-banner { display:grid; grid-template-columns:1fr auto auto; gap:26px; align-items:center; padding:22px 24px; margin-bottom:18px; color:#fff; border-radius:16px; background:linear-gradient(118deg,#083f92,#1175d3 62%,#18a2e8); box-shadow:0 14px 34px rgba(18,102,227,.18); }
.business-banner p { margin:0 0 4px; font-size:10px; font-weight:850; letter-spacing:.18em; opacity:.68; }.business-banner h2 { margin:0 0 5px; font-size:24px; }.business-banner span { font-size:13px; opacity:.78; }
.business-banner :deep(.el-button) { color:#0759b7; border-color:#fff; background:#fff; }
.status-route { display:flex; align-items:center; gap:8px; padding:12px 15px; border:1px solid rgba(255,255,255,.2); border-radius:11px; background:rgba(255,255,255,.08); }.status-route span { padding:5px 7px; color:#fff; border-radius:6px; background:rgba(255,255,255,.1); font-size:11px; font-weight:750; opacity:1; }.status-route i { font-style:normal; opacity:.55; }
.query-panel { padding:16px 18px 0; margin-bottom:14px; border:1px solid #dce9f8; border-radius:12px; background:#f8fbff; }
.record-cell strong,.record-cell small,.contact-cell span,.contact-cell small { display:block; }.record-cell small,.contact-cell small { margin-top:4px; color:#879ab1; font-size:11px; }
.business-empty { margin-top:-1px; padding:32px; text-align:center; color:#70849c; border:1px dashed #c9dced; border-radius:0 0 12px 12px; }.business-empty strong,.business-empty span { display:block; }.business-empty strong { margin-bottom:6px; color:#274561; }
@media (max-width:1100px) { .business-banner { grid-template-columns:1fr auto; }.status-route { grid-column:1/-1; grid-row:2; justify-content:center; } }
</style>
