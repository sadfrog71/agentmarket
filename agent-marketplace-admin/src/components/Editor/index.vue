<template>
  <div>
    <el-upload
      :action="uploadUrl"
      :before-upload="handleBeforeUpload"
      :on-success="handleUploadSuccess"
      :on-error="handleUploadError"
      name="file"
      :show-file-list="false"
      :headers="headers"
      class="editor-img-uploader"
      v-if="type == 'url'"
    >
      <i ref="uploadRef" class="editor-img-uploader"></i>
    </el-upload>
  </div>
  <div class="editor">
    <quill-editor
      ref="quillEditorRef"
      v-model:content="content"
      contentType="html"
      @textChange="(e) => $emit('update:modelValue', content)"
      :options="options"
      :style="styles"
    />
  </div>
</template>

<script setup>
import axios from 'axios'
import { QuillEditor } from "@vueup/vue-quill"
import "@vueup/vue-quill/dist/vue-quill.snow.css"
import { getToken } from "@/utils/auth"

const { proxy } = getCurrentInstance()

const quillEditorRef = ref()
let quillInstance
const uploadUrl = ref(import.meta.env.VITE_APP_BASE_API + "/common/upload") // 上传的图片服务器地址
const headers = ref({
  Authorization: "Bearer " + getToken()
})

const props = defineProps({
  /* 编辑器的内容 */
  modelValue: {
    type: String,
  },
  /* 高度 */
  height: {
    type: Number,
    default: null,
  },
  /* 最小高度 */
  minHeight: {
    type: Number,
    default: null,
  },
  /* 只读 */
  readOnly: {
    type: Boolean,
    default: false,
  },
  /* 上传文件大小限制(MB) */
  fileSize: {
    type: Number,
    default: 5,
  },
  /* 类型（base64格式、url格式） */
  type: {
    type: String,
    default: "url",
  },
  /* 启用公众号、秀米等富文本粘贴清理 */
  wechatPaste: {
    type: Boolean,
    default: false,
  }
})

const options = ref({
  theme: "snow",
  bounds: document.body,
  debug: "warn",
  modules: {
    // 工具栏配置
    toolbar: [
      ["bold", "italic", "underline", "strike"],      // 加粗 斜体 下划线 删除线
      ["blockquote", "code-block"],                   // 引用  代码块
      [{ list: "ordered" }, { list: "bullet" }],      // 有序、无序列表
      [{ indent: "-1" }, { indent: "+1" }],           // 缩进
      [{ size: ["small", false, "large", "huge"] }],  // 字体大小
      [{ header: [1, 2, 3, 4, 5, 6, false] }],        // 标题
      [{ color: [] }, { background: [] }],            // 字体颜色、字体背景颜色
      [{ align: [] }],                                // 对齐方式
      ["clean"],                                      // 清除文本格式
      ["link", "image", "video"]                      // 链接、图片、视频
    ],
  },
  placeholder: "请输入内容",
  readOnly: props.readOnly
})

const styles = computed(() => {
  let style = {}
  if (props.minHeight) {
    style.minHeight = `${props.minHeight}px`
  }
  if (props.height) {
    style.height = `${props.height}px`
  }
  return style
})

const content = ref("")
watch(() => props.modelValue, (v) => {
  if (v !== content.value) {
    content.value = v == undefined ? "<p></p>" : v
  }
}, { immediate: true })

// 如果设置了上传地址则自定义图片上传事件；所有编辑器均监听粘贴事件，供新闻编辑器清理公众号富文本。
onMounted(() => {
  quillInstance = quillEditorRef.value.getQuill()
  if (props.type == 'url') {
    let toolbar = quillInstance.getModule("toolbar")
    toolbar.addHandler("image", (value) => {
      if (value) {
        proxy.$refs.uploadRef.click()
      } else {
        quillInstance.format("image", false)
      }
    })
  }
  quillInstance.root.addEventListener('paste', handlePasteCapture, true)
})

onBeforeUnmount(() => {
  quillInstance?.root?.removeEventListener('paste', handlePasteCapture, true)
})

// 上传前校检格式和大小
function handleBeforeUpload(file) {
  const type = ["image/jpeg", "image/jpg", "image/png", "image/svg"]
  const isJPG = type.includes(file.type)
  //检验文件格式
  if (!isJPG) {
    proxy.$modal.msgError(`图片格式错误!`)
    return false
  }
  // 校检文件大小
  if (props.fileSize) {
    const isLt = file.size / 1024 / 1024 < props.fileSize
    if (!isLt) {
      proxy.$modal.msgError(`上传文件大小不能超过 ${props.fileSize} MB!`)
      return false
    }
  }
  return true
}

// 上传成功处理
function handleUploadSuccess(res, file) {
  // 如果上传成功
  if (res.code == 200) {
    // 获取富文本实例
    let quill = toRaw(quillEditorRef.value).getQuill()
    // 获取光标位置
    let length = quill.selection.savedRange.index
    // 只保存跨站可用的资源路径，不将管理端 API 前缀写入富文本
    quill.insertEmbed(length, "image", res.fileName)
    // 调整光标到最后
    quill.setSelection(length + 1)
  } else {
    proxy.$modal.msgError("图片插入失败")
  }
}

// 上传失败处理
function handleUploadError() {
  proxy.$modal.msgError("图片插入失败")
}

// 复制粘贴图片处理
function handlePasteCapture(e) {
  const clipboard = e.clipboardData || window.clipboardData
  const html = props.wechatPaste && clipboard?.getData('text/html')
  if (html) {
    e.preventDefault()
    insertWechatHtml(html)
    return
  }
  if (props.type !== 'url') {
    return
  }
  if (clipboard && clipboard.items) {
    for (let i = 0; i < clipboard.items.length; i++) {
      const item = clipboard.items[i]
      if (item.type.indexOf('image') !== -1) {
        e.preventDefault()
        const file = item.getAsFile()
        insertImage(file)
      }
    }
  }
}

function insertWechatHtml(html) {
  const safeHtml = sanitizeWechatHtml(html)
  if (!safeHtml || !quillInstance) {
    return
  }
  const range = quillInstance.getSelection(true) || { index: quillInstance.getLength(), length: 0 }
  quillInstance.deleteText(range.index, range.length, 'user')
  quillInstance.clipboard.dangerouslyPasteHTML(range.index, safeHtml, 'user')
}

function sanitizeWechatHtml(source) {
  const doc = new DOMParser().parseFromString(source, 'text/html')
  doc.body.querySelectorAll('script, style, iframe, object, embed, link, base, form, input, button').forEach((node) => node.remove())
  const allowedAttributes = new Set(['class', 'style', 'href', 'src', 'alt', 'title', 'width', 'height', 'colspan', 'rowspan', 'target'])
  const allowedStyleProperties = new Set([
    'color', 'background-color', 'font-size', 'font-weight', 'font-style', 'font-family',
    'text-align', 'line-height', 'letter-spacing', 'text-decoration', 'text-indent',
    'margin', 'margin-top', 'margin-right', 'margin-bottom', 'margin-left',
    'padding', 'padding-top', 'padding-right', 'padding-bottom', 'padding-left',
    'border', 'border-top', 'border-right', 'border-bottom', 'border-left',
    'border-radius', 'display', 'vertical-align', 'max-width', 'width', 'height'
  ])

  Array.from(doc.body.querySelectorAll('*')).forEach((node) => {
    if (node.tagName === 'IMG' && !node.getAttribute('src')) {
      node.setAttribute('src', node.getAttribute('data-src') || '')
    }
    Array.from(node.attributes).forEach((attribute) => {
      const name = attribute.name.toLowerCase()
      const value = attribute.value.trim()
      if (!allowedAttributes.has(name) || name.startsWith('on')) {
        node.removeAttribute(attribute.name)
      } else if (name === 'class') {
        const safeClasses = value.split(/\s+/).filter((item) => /^(ql-(align|indent|size|font|direction)-[a-z0-9-]+)$/i.test(item))
        if (safeClasses.length) {
          node.setAttribute('class', safeClasses.join(' '))
        } else {
          node.removeAttribute('class')
        }
      } else if ((name === 'href' || name === 'src') && !isSafeUrl(value)) {
        node.removeAttribute(attribute.name)
      }
    })
    if (node.tagName === 'IMG' && !node.getAttribute('src')) {
      node.remove()
      return
    }
    if (node.hasAttribute('style')) {
      Array.from(node.style).forEach((property) => {
        const value = node.style.getPropertyValue(property).toLowerCase()
        if (!allowedStyleProperties.has(property) || /url\s*\(|expression\s*\(|@import|javascript:|data:/.test(value)) {
          node.style.removeProperty(property)
        }
      })
      if (!node.getAttribute('style')?.trim()) {
        node.removeAttribute('style')
      }
    }
    if (node.tagName === 'A' && node.getAttribute('href')) {
      node.setAttribute('target', '_blank')
      node.setAttribute('rel', 'noopener noreferrer')
    }
  })
  return doc.body.innerHTML
}

function isSafeUrl(value) {
  return /^(https?:|mailto:|tel:|\/|#)/i.test(value) && !/^\/\//.test(value)
}

function insertImage(file) {
  const formData = new FormData()
  formData.append("file", file)
  axios.post(uploadUrl.value, formData, { headers: { "Content-Type": "multipart/form-data", Authorization: headers.value.Authorization } }).then(res => {
    handleUploadSuccess(res.data)
  })
}
</script>

<style>
.editor-img-uploader {
  display: none;
}
.editor, .ql-toolbar {
  white-space: pre-wrap !important;
  line-height: normal !important;
}
.quill-img {
  display: none;
}
.ql-snow .ql-tooltip[data-mode="link"]::before {
  content: "请输入链接地址:";
}
.ql-snow .ql-tooltip.ql-editing a.ql-action::after {
  border-right: 0px;
  content: "保存";
  padding-right: 0px;
}
.ql-snow .ql-tooltip[data-mode="video"]::before {
  content: "请输入视频地址:";
}
.ql-snow .ql-picker.ql-size .ql-picker-label::before,
.ql-snow .ql-picker.ql-size .ql-picker-item::before {
  content: "14px";
}
.ql-snow .ql-picker.ql-size .ql-picker-label[data-value="small"]::before,
.ql-snow .ql-picker.ql-size .ql-picker-item[data-value="small"]::before {
  content: "10px";
}
.ql-snow .ql-picker.ql-size .ql-picker-label[data-value="large"]::before,
.ql-snow .ql-picker.ql-size .ql-picker-item[data-value="large"]::before {
  content: "18px";
}
.ql-snow .ql-picker.ql-size .ql-picker-label[data-value="huge"]::before,
.ql-snow .ql-picker.ql-size .ql-picker-item[data-value="huge"]::before {
  content: "32px";
}
.ql-snow .ql-picker.ql-header .ql-picker-label::before,
.ql-snow .ql-picker.ql-header .ql-picker-item::before {
  content: "文本";
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="1"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="1"]::before {
  content: "标题1";
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="2"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="2"]::before {
  content: "标题2";
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="3"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="3"]::before {
  content: "标题3";
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="4"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="4"]::before {
  content: "标题4";
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="5"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="5"]::before {
  content: "标题5";
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="6"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="6"]::before {
  content: "标题6";
}
.ql-snow .ql-picker.ql-font .ql-picker-label::before,
.ql-snow .ql-picker.ql-font .ql-picker-item::before {
  content: "标准字体";
}
.ql-snow .ql-picker.ql-font .ql-picker-label[data-value="serif"]::before,
.ql-snow .ql-picker.ql-font .ql-picker-item[data-value="serif"]::before {
  content: "衬线字体";
}
.ql-snow .ql-picker.ql-font .ql-picker-label[data-value="monospace"]::before,
.ql-snow .ql-picker.ql-font .ql-picker-item[data-value="monospace"]::before {
  content: "等宽字体";
}
</style>
