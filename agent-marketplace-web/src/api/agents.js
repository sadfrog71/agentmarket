import { seedAgents } from './seedAgents'

const baseUrl = import.meta.env.VITE_API_BASE_URL || ''

async function request(path) {
  const response = await fetch(`${baseUrl}${path}`)
  if (!response.ok) throw new Error(`HTTP ${response.status}`)
  return response.json()
}

export async function fetchAgents(params = {}) {
  const query = new URLSearchParams({ pageNum: '1', pageSize: '100', ...params })
  try {
    const result = await request(`/open/agents?${query}`)
    return result.rows || []
  } catch (error) {
    if (import.meta.env.DEV) return seedAgents
    throw error
  }
}

export async function fetchAgentDetail(agentId) {
  try {
    const result = await request(`/open/agents/${agentId}`)
    return result.data
  } catch (error) {
    if (import.meta.env.DEV) return seedAgents.find(item => item.agentId === Number(agentId))
    throw error
  }
}
