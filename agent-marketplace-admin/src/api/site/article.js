import request from '@/utils/request'

export function listSiteArticles(query) { return request({ url: '/site/article/list', method: 'get', params: query }) }
export function getSiteArticle(articleId) { return request({ url: `/site/article/${articleId}`, method: 'get' }) }
export function addSiteArticle(data) { return request({ url: '/site/article', method: 'post', data }) }
export function updateSiteArticle(data) { return request({ url: '/site/article', method: 'put', data }) }
export function publishSiteArticle(articleId, reason = '') { return request({ url: `/site/article/${articleId}/publish`, method: 'post', data: { reason } }) }
export function unpublishSiteArticle(articleId, reason = '') { return request({ url: `/site/article/${articleId}/unpublish`, method: 'post', data: { reason } }) }
