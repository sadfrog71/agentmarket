import request from '@/utils/request'

export function listSitePages(query) {
  return request({ url: '/site/page/list', method: 'get', params: query })
}

export function getSitePage(pageId) {
  return request({ url: `/site/page/${pageId}`, method: 'get' })
}

export function addSitePage(data) {
  return request({ url: '/site/page', method: 'post', data })
}

export function updateSitePage(data) {
  return request({ url: '/site/page', method: 'put', data })
}

export function publishSitePage(pageId, reason = '') {
  return request({ url: `/site/page/${pageId}/publish`, method: 'post', data: { reason } })
}

export function unpublishSitePage(pageId, reason = '') {
  return request({ url: `/site/page/${pageId}/unpublish`, method: 'post', data: { reason } })
}
