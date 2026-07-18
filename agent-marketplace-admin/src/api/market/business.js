import request from '@/utils/request'

export function listBusiness(query) {
  return request({ url: '/market/business/list', method: 'get', params: query })
}

export function getBusiness(recordId) {
  return request({ url: `/market/business/${recordId}`, method: 'get' })
}

export function addBusiness(data) {
  return request({ url: '/market/business', method: 'post', data })
}

export function updateBusiness(data) {
  return request({ url: '/market/business', method: 'put', data })
}

export function delBusiness(recordIds) {
  return request({ url: `/market/business/${recordIds}`, method: 'delete' })
}
