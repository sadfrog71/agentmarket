import request from '@/utils/request'

export function listAgent(query) {
  return request({ url: '/market/agent/list', method: 'get', params: query })
}

export function getAgent(agentId) {
  return request({ url: `/market/agent/${agentId}`, method: 'get' })
}

export function addAgent(data) {
  return request({ url: '/market/agent', method: 'post', data })
}

export function updateAgent(data) {
  return request({ url: '/market/agent', method: 'put', data })
}

export function delAgent(agentIds) {
  return request({ url: `/market/agent/${agentIds}`, method: 'delete' })
}
