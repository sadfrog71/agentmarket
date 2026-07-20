import request from '@/utils/request'

export function listContent(query) {
  return request({ url: '/market/content/list', method: 'get', params: query })
}

export function getContent(contentId) {
  return request({ url: `/market/content/${contentId}`, method: 'get' })
}

export function addContent(data) {
  return request({ url: '/market/content', method: 'post', data })
}

export function updateContent(data) {
  return request({ url: '/market/content', method: 'put', data })
}

export function delContent(contentIds) {
  return request({ url: `/market/content/${contentIds}`, method: 'delete' })
}
