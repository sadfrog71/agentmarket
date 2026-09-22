<template>
  <div class="app-container">
    <section class="media-banner"><div><p>OFFICIAL SITE OPERATIONS</p><h2>官网媒体库</h2><span>上传后的媒体先保留在官网专用存储中；只有被已发布页面、新闻或证书引用时，前台才可访问。</span></div><el-upload :show-file-list="false" :http-request="upload" accept="application/pdf,image/jpeg,image/png,image/webp"><el-button type="primary" icon="Upload" v-hasPermi="['site:media:add']">上传媒体</el-button></el-upload></section>
    <el-alert title="仅支持 PDF、JPG、PNG、WEBP，单文件不超过 25MB。上传成功后，请复制媒体编号到需要引用它的内容草稿中。" type="info" :closable="false" show-icon class="mb16"/>
    <el-form ref="queryRef" :model="queryParams" :inline="true"><el-form-item label="文件名"><el-input v-model="queryParams.originalFilename" clearable @keyup.enter="handleQuery" /></el-form-item><el-form-item label="类型"><el-select v-model="queryParams.mediaKind" clearable><el-option label="图片" value="IMAGE"/><el-option label="文档" value="DOCUMENT"/></el-select></el-form-item><el-form-item><el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item></el-form>
    <el-table v-loading="loading" :data="mediaList"><el-table-column label="文件" min-width="250"><template #default="scope"><strong>{{ scope.row.originalFilename }}</strong><small>{{ scope.row.mimeType }} · {{ formatSize(scope.row.fileSize) }}</small></template></el-table-column><el-table-column label="类型" width="100"><template #default="scope"><el-tag effect="plain">{{ scope.row.mediaKind === 'DOCUMENT' ? '文档' : '图片' }}</el-tag></template></el-table-column><el-table-column label="媒体编号" min-width="300"><template #default="scope"><div class="media-id"><code>{{ scope.row.publicId }}</code><el-button link type="primary" size="small" @click="copy(scope.row.publicId)">复制</el-button></div></template></el-table-column><el-table-column label="状态" width="105"><template #default="scope"><el-tag type="success" effect="plain">{{ scope.row.mediaState === 'AVAILABLE' ? '可引用' : scope.row.mediaState }}</el-tag></template></el-table-column><el-table-column label="上传时间" width="170"><template #default="scope">{{ parseTime(scope.row.createTime) }}</template></el-table-column></el-table>
    <div v-if="!loading && !mediaList.length" class="empty"><strong>尚未导入或上传官网媒体</strong><span>迁移包执行后，既有图片、二维码和证书文件会出现在这里。</span></div><pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup name="SiteMedia">
import { listSiteMedia, uploadSiteMedia } from '@/api/site/media'
const { proxy } = getCurrentInstance()
const loading = ref(false), mediaList = ref([]), total = ref(0)
const queryParams = reactive({ pageNum: 1, pageSize: 20, originalFilename: undefined, mediaKind: undefined })
function getList() { loading.value = true; listSiteMedia(queryParams).then(res => { mediaList.value = res.rows || []; total.value = res.total || 0 }).finally(() => { loading.value = false }) }
function handleQuery() { queryParams.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
function upload(option) { const form = new FormData(); form.append('file', option.file); uploadSiteMedia(form).then(res => { option.onSuccess(res); proxy.$modal.msgSuccess(`媒体已上传，编号：${res.data.publicId}`); getList() }).catch(error => option.onError(error)) }
function copy(value) { navigator.clipboard?.writeText(value).then(() => proxy.$modal.msgSuccess('媒体编号已复制')).catch(() => proxy.$modal.msgWarning('当前浏览器不支持自动复制，请手动复制')) }
function formatSize(size) { if (!size) return '0 B'; if (size < 1024) return `${size} B`; if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)} KB`; return `${(size / 1024 / 1024).toFixed(1)} MB` }
getList()
</script>

<style scoped lang="scss">.media-banner{display:flex;justify-content:space-between;gap:24px;align-items:center;padding:22px 24px;margin-bottom:18px;color:#fff;border-radius:16px;background:linear-gradient(120deg,#10294f,#116f9d 60%,#24a4bb)}.media-banner p{margin:0 0 4px;font-size:11px;letter-spacing:1.4px;opacity:.75}.media-banner h2{margin:0 0 5px;font-size:24px}.media-banner span{font-size:13px;opacity:.9}.media-banner strong,.media-banner small{display:block}.media-id{display:flex;align-items:center;gap:8px}.media-id code{font-size:12px;color:#526277}.empty{padding:54px 0;text-align:center;color:#909399}.empty strong,.empty span{display:block}.empty span{margin-top:8px;font-size:13px}@media(max-width:700px){.media-banner{display:block}.media-banner .el-upload{margin-top:14px}}</style>
