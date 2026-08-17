import request from '@/utils/request'

export function listCategory(query) {
  return request({ url: '/market/category/list', method: 'get', params: query })
}

export function listCategoryOptions() {
  return request({ url: '/market/category/options', method: 'get' })
}

export function getCategory(categoryId) {
  return request({ url: `/market/category/${categoryId}`, method: 'get' })
}

export function addCategory(data) {
  return request({ url: '/market/category', method: 'post', data })
}

export function updateCategory(data) {
  return request({ url: '/market/category', method: 'put', data })
}

export function delCategory(categoryIds) {
  return request({ url: `/market/category/${categoryIds}`, method: 'delete' })
}
