const baseUrl = import.meta.env.VITE_API_BASE_URL || ''

export async function fetchSiteContent(contentKey) {
  try {
    const response = await fetch(`${baseUrl}/open/content/${encodeURIComponent(contentKey)}`)
    if (!response.ok) return null
    const result = await response.json()
    return result.code === 200 ? result.data : null
  } catch {
    return null
  }
}
