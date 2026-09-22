<template>
  <Transition name="site-turn">
    <main v-if="loading" class="site-loading" aria-live="polite">
      <span class="sr-only">Loading</span>
      <div class="site-loading-mark" aria-hidden="true">
        <i></i><i></i><b>平行数字</b>
      </div>
      <p aria-hidden="true">avatar-tech</p>
    </main>
  </Transition>
  <main v-if="!loading && error" class="site-error"><p class="eyebrow">PARALLEL DIGITAL</p><h1>{{ error }}</h1><a class="button" href="/index.html">返回首页 <span>↗</span></a></main>
  <div v-else-if="!loading && page" id="top" class="site-render-root" v-html="page.bodyHtml"></div>
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

async function revealLoadedPage() {
  loading.value = false
  await nextTick()
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
    await revealLoadedPage()
    initialiseLegacyInteractions({
      articles: Array.isArray(articlePayload.data) ? articlePayload.data.map(toLegacyArticle) : [],
      certificates: Array.isArray(credentialPayload.data) && credentialPayload.data.length
        ? credentialPayload.data.map(toLegacyCredential)
        : Array.isArray(pageData.certificates) ? pageData.certificates : [],
      newsCategory: pageData.newsCategory
    })
  } catch (reason) {
    error.value = reason.message || '官网页面暂时无法访问'
    await revealLoadedPage()
  } finally {
    if (loading.value) await revealLoadedPage()
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
.site-loading{position:fixed;inset:0;z-index:10000;display:grid;place-content:center;justify-items:center;gap:24px;overflow:hidden;contain:paint;padding:32px;text-align:center;background:radial-gradient(circle at 82% 82%,#3d78ff 0,transparent 32%),repeating-linear-gradient(90deg,transparent 0 95px,rgba(255,255,255,.045) 96px 97px),linear-gradient(145deg,#10244a 0%,#173e9f 54%,#2456e8 100%);color:#fff;transform:translateZ(0)}.site-loading:after{content:"";position:absolute;right:-22vmax;bottom:-25vmax;width:58vmax;height:58vmax;border:1px solid rgba(255,255,255,.22);background:linear-gradient(135deg,rgba(255,255,255,.16),rgba(255,255,255,0) 48%);transform:rotate(45deg);box-shadow:-24px -24px 70px rgba(5,19,56,.24)}.site-loading-mark{position:relative;z-index:1;width:78px;height:78px;border:1px solid rgba(255,255,255,.38);transform:rotate(45deg);animation:site-mark-breathe 1.8s cubic-bezier(.4,0,.2,1) infinite}.site-loading-mark i{position:absolute;display:block;background:#fff}.site-loading-mark i:first-child{left:17px;right:17px;top:25px;height:1px;animation:site-mark-line 1.8s ease-in-out infinite}.site-loading-mark i:nth-child(2){top:17px;bottom:17px;left:25px;width:1px;animation:site-mark-line 1.8s .18s ease-in-out infinite}.site-loading-mark b{position:absolute;left:50%;top:50%;width:88px;text-align:center;font:500 11px/1 "PingFang SC","Microsoft YaHei",sans-serif;letter-spacing:1.5px;transform:translate(-50%,-50%) rotate(-45deg)}.site-loading p{position:relative;z-index:1;margin:0;font:500 10px/1.2 "Helvetica Neue",sans-serif;letter-spacing:4px;color:rgba(255,255,255,.78)}.site-turn-leave-active{transition:clip-path .46s cubic-bezier(.72,0,.2,1)}.site-turn-leave-from{clip-path:polygon(-15% -15%,115% -15%,115% 115%,-15% 115%)}.site-turn-leave-to{clip-path:polygon(100% 100%,100% 100%,100% 100%,100% 100%)}.site-error{min-height:100vh;display:grid;place-content:center;gap:16px;padding:32px;text-align:center;background:#f6fbfc;color:#172b3a}.site-error h1{max-width:650px;margin:0;font-size:clamp(28px,4vw,48px);line-height:1.25}.site-error .button{justify-self:center}@keyframes site-mark-breathe{0%,100%{transform:rotate(45deg) scale(.94);opacity:.78}50%{transform:rotate(45deg) scale(1.025);opacity:1}}@keyframes site-mark-line{0%,100%{transform:scaleX(.55);opacity:.55}50%{transform:scaleX(1);opacity:1}}@media(prefers-reduced-motion:reduce){.site-loading-mark,.site-loading-mark i{animation:none}.site-turn-leave-active{transition:none}}
</style>
