let cleanup = () => {}

function escapeHtml(value) {
  return String(value ?? '').replace(/[&<>"']/g, character => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[character])
}

function register(controller, target, event, handler) {
  target?.addEventListener(event, handler, { signal: controller.signal })
}

function removeBrandEnglishMarkers() {
  const walker = document.createTreeWalker(document.body, NodeFilter.SHOW_TEXT)
  const removable = []
  let node = walker.nextNode()
  while (node) {
    if (/PARALLEL\s+DIGITAL/i.test(node.nodeValue || '')) {
      node.nodeValue = node.nodeValue.replace(/PARALLEL\s+DIGITAL/gi, '').replace(/[ \t]{2,}/g, ' ')
      if (!node.nodeValue.trim()) removable.push(node.parentElement)
    }
    node = walker.nextNode()
  }
  removable.forEach(element => {
    if (element?.classList.contains('eyebrow') && !element.textContent.trim()) element.remove()
  })
  document.querySelectorAll('.footer-bottom > span').forEach(element => {
    if (!element.textContent.trim()) element.remove()
  })
}

function hydrateRefreshHeader() {
  const image = document.querySelector('.refresh-home-header .brand img')
  if (!image) return
  image.src = '/visuals/parallel-logo-reverse.png'
  image.alt = '平行数字'
}

export function initialiseLegacyInteractions({ articles = [], certificates = [], newsCategory = '' } = {}) {
  cleanup()
  const controller = new AbortController()
  cleanup = () => controller.abort()

  removeBrandEnglishMarkers()
  hydrateRefreshHeader()

  const menuButton = document.querySelector('.menu-toggle')
  const mainNav = document.querySelector('#main-nav')
  register(controller, menuButton, 'click', () => {
    const open = menuButton.getAttribute('aria-expanded') !== 'true'
    menuButton.setAttribute('aria-expanded', String(open))
    menuButton.setAttribute('aria-label', open ? '收起导航' : '展开导航')
    mainNav?.classList.toggle('open', open)
  })
  register(controller, document, 'keydown', event => {
    if (event.key === 'Escape' && mainNav?.classList.contains('open')) {
      mainNav.classList.remove('open')
      menuButton?.setAttribute('aria-expanded', 'false')
      menuButton?.focus()
    }
  })
  mainNav?.querySelectorAll('a').forEach(link => register(controller, link, 'click', () => {
    mainNav.classList.remove('open')
    menuButton?.setAttribute('aria-expanded', 'false')
  }))

  const navItems = [...document.querySelectorAll('.nav-item')]
  const closeSubmenus = except => navItems.forEach(item => {
    if (item !== except) {
      item.classList.remove('expanded')
      item.querySelector('.submenu-toggle')?.setAttribute('aria-expanded', 'false')
    }
  })
  navItems.forEach(item => {
    const button = item.querySelector('.submenu-toggle')
    if (!button) return
    const setOpen = open => {
      if (open) closeSubmenus(item)
      item.classList.toggle('expanded', open)
      button.setAttribute('aria-expanded', String(open))
    }
    register(controller, button, 'click', () => setOpen(!item.classList.contains('expanded')))
    register(controller, item, 'mouseenter', () => { if (matchMedia('(hover:hover) and (min-width:981px)').matches) setOpen(true) })
    register(controller, item, 'mouseleave', () => { if (matchMedia('(hover:hover) and (min-width:981px)').matches) setOpen(false) })
  })
  register(controller, document, 'click', event => { if (!event.target.closest('.nav-item')) closeSubmenus() })

  register(controller, document, 'click', event => {
    if (event.defaultPrevented || event.button !== 0 || event.metaKey || event.ctrlKey || event.shiftKey || event.altKey) return
    if (matchMedia('(prefers-reduced-motion: reduce)').matches) return
    const link = event.target.closest('a[href]')
    if (!link || link.target || link.hasAttribute('download')) return
    const next = new URL(link.href, window.location.href)
    if (next.origin !== window.location.origin || !next.pathname.endsWith('.html')) return
    if (next.pathname === window.location.pathname && next.hash) return
    event.preventDefault()
    if (document.documentElement.classList.contains('site-route-leaving')) return
    document.documentElement.classList.add('site-route-leaving')
    window.setTimeout(() => window.location.assign(next.href), 340)
  })

  document.querySelectorAll('[role=tablist]').forEach(list => {
    const buttons = [...list.querySelectorAll('[role=tab]')]
    const activate = button => buttons.forEach(candidate => {
      const selected = candidate === button
      candidate.setAttribute('aria-selected', String(selected))
      candidate.tabIndex = selected ? 0 : -1
      const panel = document.getElementById(candidate.dataset.tab)
      if (panel) panel.hidden = !selected
    })
    buttons.forEach((button, index) => {
      register(controller, button, 'click', () => activate(button))
      register(controller, button, 'keydown', event => {
        const keyMap = { ArrowRight: 1, ArrowDown: 1, ArrowLeft: -1, ArrowUp: -1 }
        if (!(event.key in keyMap) && event.key !== 'Home' && event.key !== 'End') return
        event.preventDefault()
        const next = event.key === 'Home' ? 0 : event.key === 'End' ? buttons.length - 1 : (index + keyMap[event.key] + buttons.length) % buttons.length
        activate(buttons[next]); buttons[next].focus()
      })
    })
  })

  const categories = { corp: '企业新闻', tech: '技术科普', ind: '行业资讯' }
  const newsRow = article => `<a class="news-row" href="${escapeHtml(article.legacyPath)}"><time datetime="${escapeHtml(article.publishedDate)}">${escapeHtml(article.publishedDate || '日期未标注')}</time><div><span class="mini-label">${categories[article.categoryCode] || '新闻动态'}</span><h3>${escapeHtml(article.title)}</h3></div><b aria-hidden="true">↗</b></a>`
  const homeNews = document.getElementById('home-news')
  if (homeNews) homeNews.innerHTML = articles.slice(0, 3).map(newsRow).join('')
  const categoryNews = document.getElementById('category-news')
  if (categoryNews) {
    categoryNews.innerHTML = articles.filter(item => !newsCategory || item.categoryCode === newsCategory).map(newsRow).join('')
  }

  const newsResults = document.getElementById('news-results')
  if (newsResults) {
    let activeCategory = 'all'
    const search = document.getElementById('news-search')
    const drawNews = () => {
      const keyword = (search?.value || '').trim().toLowerCase()
      const filtered = articles.filter(item => (activeCategory === 'all' || item.categoryCode === activeCategory)
        && String(item.title || '').toLowerCase().includes(keyword))
      newsResults.innerHTML = filtered.length ? filtered.map(newsRow).join('') : '<p class="empty-state">未找到相关文章，请尝试其他关键词。</p>'
      const count = document.getElementById('news-count')
      if (count) count.textContent = `共 ${filtered.length} 篇文章`
    }
    document.querySelectorAll('[data-news-filter]').forEach(button => {
      const key = button.dataset.newsFilter
      const count = document.getElementById(`count-${key}`)
      if (count) count.textContent = String(articles.filter(item => key === 'all' || item.categoryCode === key).length)
      register(controller, button, 'click', () => {
        activeCategory = key
        document.querySelectorAll('[data-news-filter]').forEach(candidate => candidate.setAttribute('aria-pressed', String(candidate === button)))
        drawNews()
      })
    })
    register(controller, search, 'input', drawNews)
    drawNews()
  }

  const caseCards = [...document.querySelectorAll('.case-card[data-category]')]
  document.querySelectorAll('[data-case-filter]').forEach(button => register(controller, button, 'click', () => {
    const category = button.dataset.caseFilter
    document.querySelectorAll('[data-case-filter]').forEach(candidate => candidate.setAttribute('aria-pressed', String(candidate === button)))
    caseCards.forEach(card => { card.hidden = category !== 'all' && card.dataset.category !== category })
  }))

  const viewer = document.getElementById('image-viewer')
  if (viewer) {
    const close = () => viewer.close?.()
    register(controller, viewer.querySelector('.dialog-close'), 'click', close)
    document.querySelectorAll('.image-expand').forEach(button => register(controller, button, 'click', () => {
      const source = button.querySelector('img')
      const image = document.getElementById('image-viewer-img')
      if (!source || !image) return
      image.src = source.src; image.alt = source.alt
      document.getElementById('image-original').href = source.src
      document.getElementById('image-viewer-title').textContent = button.dataset.imageTitle || source.alt
      const caption = button.closest('figure')?.querySelector('figcaption > span')
      document.getElementById('image-viewer-caption').textContent = caption?.textContent || ''
      viewer.showModal?.()
    }))
  }

  const certificateDialog = document.getElementById('cert-dialog')
  if (certificateDialog && certificates.length) {
    let kind = 'soft'
    const input = document.getElementById('cert-search')
    const draw = () => {
      const keyword = (input?.value || '').trim().toLowerCase()
      const items = certificates.filter(item => item.kind === kind && item.name.toLowerCase().includes(keyword))
      document.getElementById('cert-count').textContent = `现有归档资料 ${items.length} 项`
      document.getElementById('cert-list').innerHTML = items.map(item => `<a class="cert-row" href="${escapeHtml(item.url)}" target="_blank" rel="noopener noreferrer">${escapeHtml(item.name)}<span>查看证书 ↗</span></a>`).join('') || '<p class="empty-state">未找到匹配证书。</p>'
    }
    register(controller, document.querySelector('[data-cert-open]'), 'click', () => { draw(); certificateDialog.showModal?.() })
    register(controller, certificateDialog.querySelector('.dialog-close'), 'click', () => certificateDialog.close?.())
    register(controller, input, 'input', draw)
    document.querySelectorAll('[data-cert-kind]').forEach(button => register(controller, button, 'click', () => {
      kind = button.dataset.certKind
      document.querySelectorAll('[data-cert-kind]').forEach(candidate => candidate.setAttribute('aria-pressed', String(candidate === button)))
      draw()
    }))
  }
}

export function disposeLegacyInteractions() {
  cleanup()
  cleanup = () => {}
}
