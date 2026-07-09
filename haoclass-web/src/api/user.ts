import { http, unwrapResult, type ApiResult } from './http'
import type { PageResult } from '@/types/course'
import type { User, UserCreatePayload, UserPageQuery, UserPasswordPayload, UserUpdatePayload } from '@/types/user'

export async function listAdminUsers(params: UserPageQuery) {
  const response = await http.get<ApiResult<PageResult<User>>>('/main/admin/users', { params })
  return unwrapResult(response.data)
}

export async function getAdminUserDetail(id: string) {
  const response = await http.get<ApiResult<User>>(`/main/admin/users/${id}`)
  return unwrapResult(response.data)
}

export async function createAdminUser(payload: UserCreatePayload) {
  const response = await http.post<ApiResult<void>>('/main/admin/users', payload)
  return unwrapResult(response.data)
}

export async function updateAdminUser(id: string, payload: UserUpdatePayload) {
  const response = await http.put<ApiResult<void>>(`/main/admin/users/${id}`, payload)
  return unwrapResult(response.data)
}

export async function deleteAdminUser(id: string) {
  const response = await http.delete<ApiResult<void>>(`/main/admin/users/${id}`)
  return unwrapResult(response.data)
}

export async function enableAdminUser(id: string) {
  const response = await http.put<ApiResult<void>>(`/main/admin/users/${id}/enable`)
  return unwrapResult(response.data)
}

export async function disableAdminUser(id: string) {
  const response = await http.put<ApiResult<void>>(`/main/admin/users/${id}/disable`)
  return unwrapResult(response.data)
}

export async function resetAdminUserPassword(id: string, payload: UserPasswordPayload) {
  const response = await http.put<ApiResult<void>>(`/main/admin/users/${id}/password`, payload)
  return unwrapResult(response.data)
}
