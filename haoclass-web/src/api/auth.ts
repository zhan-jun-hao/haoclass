import { http, unwrapResult, type ApiResult } from './http'

export interface LoginPayload {
  phone: string
  password: string
}

export interface LoginResult {
  tokenType: string
  accessToken: string
  expiresIn: number
  userId: string
  phone: string
  nickname: string
  role: number
}

export async function adminLogin(payload: LoginPayload) {
  const response = await http.post<ApiResult<LoginResult>>('/main/admin/auth/login', payload)
  return unwrapResult(response.data)
}

export async function clientLogin(payload: LoginPayload) {
  const response = await http.post<ApiResult<LoginResult>>('/main/client/auth/login', payload)
  return unwrapResult(response.data)
}
