<template>
  <main v-if="loading" class="site-loading" aria-live="polite"><span></span><p>正在加载官网内容…</p></main>
  <main v-else-if="error" class="site-error"><p class="eyebrow">PARALLEL DIGITAL</p><h1>{{ error }}</h1><a class="button" href="/index.html">返回首页 <span>↗</span></a></main>
  <div v-else id="top" class="site-render-root" v-html="page.bodyHtml"></div>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { disposeLegacyInteractions, initialiseLegacyInteractions } from './legacyInteractions'

const loading = ref(true)
const error = ref('')
const page = ref(null)

function currentLegacyPath() {
  return window.location.pathname === '/' ? '/index.html' : window.location.pathname
}

async function loadPage() {
  loading.value = true
  error.value = ''
  try {
    const [pageResponse, articleResponse, credentialResponse] = await Promise.all([
      fetch(`/api/open/site/v1/pages?path=${encodeURIComponent(currentLegacyPath())}`, { cache: 'no-store' }),
      fetch('/api/open/site/v1/articles?limit=60', { cache: 'no-store' }),
      fetch('/api/open/site/v1/credentials?limit=100', { cache: 'no-store' })
    ])
    const response = pageResponse
    const payload = await response.json()
    if (!response.ok || payload.code !== 200 || !payload.data) throw new Error(payload.msg || '该官网页面尚未发布')
    page.value = payload.data
    document.title = page.value.title || '平行数字'
    const articlePayload = articleResponse.ok ? await articleResponse.json() : { data: [] }
    const credentialPayload = credentialResponse.ok ? await credentialResponse.json() : { data: [] }
    const pageData = parsePageData(page.value.pageDataJson)
    // Render the immutable template before binding its dynamic content slots.
    // Otherwise nextTick still sees the loading state and homepage/news blocks remain empty.
    loading.value = false
    await nextTick()
    initialiseLegacyInteractions({
      articles: Array.isArray(articlePayload.data) ? articlePayload.data.map(toLegacyArticle) : [],
      certificates: Array.isArray(credentialPayload.data) && credentialPayload.data.length
        ? credentialPayload.data.map(toLegacyCredential)
        : Array.isArray(pageData.certificates) ? pageData.certificates : [],
      newsCategory: pageData.newsCategory
    })
  } catch (reason) {
    error.value = reason.message || '官网页面暂时无法访问'
  } finally {
    loading.value = false
  }
}

function parsePageData(value) {
  if (!value) return {}
  try {
    const parsed = JSON.parse(value)
    return parsed && typeof parsed === 'object' ? parsed : {}
  } catch {
    return {}
  }
}

function toLegacyArticle(article) {
  return {
    ...article,
    publishedDate: article.publishedAt ? String(article.publishedAt).slice(0, 10) : ''
  }
}

function toLegacyCredential(credential) {
  return {
    kind: credential.credentialType === 'PATENT' ? 'patent' : 'soft',
    name: credential.title,
    url: credential.documentUrl ? `/api${credential.documentUrl}` : ''
  }
}

onMounted(loadPage)
onBeforeUnmount(disposeLegacyInteractions)
</script>

<style>
.site-loading,.site-error{min-height:100vh;display:grid;place-content:center;gap:16px;padding:32px;text-align:center;background:#f6fbfc;color:#172b3a}.site-loading span{width:32px;height:32px;margin:auto;border:3px solid #c9e6ec;border-top-color:#057f9a;border-radius:50%;animation:site-spin .8s linear infinite}.site-error h1{max-width:650px;margin:0;font-size:clamp(28px,4vw,48px);line-height:1.25}.site-error .button{justify-self:center}@keyframes site-spin{to{transform:rotate(360deg)}}
</style>
