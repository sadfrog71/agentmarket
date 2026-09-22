import request from '@/utils/request'

export function listSiteMedia(query) { return request({ url: '/site/media/list', method: 'get', params: query }) }
export function uploadSiteMedia(data) { return request({ url: '/site/media/upload', method: 'post', data, headers: { 'Content-Type': 'multipart/form-data' } }) }
