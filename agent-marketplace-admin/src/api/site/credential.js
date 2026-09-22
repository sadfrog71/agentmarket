import request from '@/utils/request'

export function listSiteCredentials(query) { return request({ url: '/site/credential/list', method: 'get', params: query }) }
export function getSiteCredential(credentialId) { return request({ url: `/site/credential/${credentialId}`, method: 'get' }) }
export function addSiteCredential(data) { return request({ url: '/site/credential', method: 'post', data }) }
export function updateSiteCredential(data) { return request({ url: '/site/credential', method: 'put', data }) }
export function publishSiteCredential(credentialId, reason = '') { return request({ url: `/site/credential/${credentialId}/publish`, method: 'post', data: { reason } }) }
export function unpublishSiteCredential(credentialId, reason = '') { return request({ url: `/site/credential/${credentialId}/unpublish`, method: 'post', data: { reason } }) }
