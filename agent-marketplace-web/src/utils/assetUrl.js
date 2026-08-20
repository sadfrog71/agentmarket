const ADMIN_PROFILE_PREFIX = '/prod-api/profile/'
const PUBLIC_PROFILE_PREFIX = '/profile/'

export function normalizeManagedAssetUrl(value) {
  const source = String(value || '').trim()
  if (!source.startsWith(ADMIN_PROFILE_PREFIX)) return source
  return `${PUBLIC_PROFILE_PREFIX}${source.slice(ADMIN_PROFILE_PREFIX.length)}`
}
