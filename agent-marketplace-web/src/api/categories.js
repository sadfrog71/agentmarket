const baseUrl = import.meta.env.VITE_API_BASE_URL || ''

export async function fetchCategories() {
  const response = await fetch(`${baseUrl}/open/categories`)
  if (!response.ok) throw new Error(`HTTP ${response.status}`)
  const result = await response.json()
  if (result.code !== 200) throw new Error(result.msg || '分类接口返回异常')
  return result.data || []
}
