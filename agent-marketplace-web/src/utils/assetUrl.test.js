import assert from 'node:assert/strict'
import test from 'node:test'

import { normalizeManagedAssetUrl } from './assetUrl.js'

test('rewrites admin upload routes to the public profile route', () => {
  assert.equal(
    normalizeManagedAssetUrl('/prod-api/profile/upload/2026/08/19/qr.png'),
    '/profile/upload/2026/08/19/qr.png'
  )
})

test('leaves canonical and external image routes unchanged', () => {
  assert.equal(normalizeManagedAssetUrl('/profile/upload/qr.png'), '/profile/upload/qr.png')
  assert.equal(normalizeManagedAssetUrl('https://cdn.example.com/qr.png'), 'https://cdn.example.com/qr.png')
  assert.equal(normalizeManagedAssetUrl('/prod-api/common/download?id=1'), '/prod-api/common/download?id=1')
})
