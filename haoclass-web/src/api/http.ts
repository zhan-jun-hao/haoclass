import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

export const http = axios.create({
  baseURL: '/api',
  timeout: 10000
})

export interface ApiResult<T> {
  code: number
  msg: string
  data: T
}

export function unwrapResult<T>(body: ApiResult<T>): T {
  if (body.code !== 200) {
    throw new Error(body.msg || '请求失败')
  }

  return body.data
}

http.interceptors.request.use((config) => {
  const authStore = useAuthStore()

  if (authStore.token) {
    config.headers.Authorization = `Bearer ${authStore.token}`
  }

  return config
})

http.interceptors.response.use(
  (response) => response,
  (error) => {
    // 后端统一返回 Result 时，优先把 msg 抛给页面展示，避免只看到 Axios 默认的 503 文案。
    const message = error?.response?.data?.msg

    if (error?.response?.status === 401) {
      const authStore = useAuthStore()
      authStore.logout()
      ElMessage.warning(message || '登录已过期，请重新登录')

      if (window.location.pathname !== '/login') {
        const redirect = encodeURIComponent(`${window.location.pathname}${window.location.search}`)
        window.location.assign(`/login?redirect=${redirect}`)
      }
    }

    if (message) {
      return Promise.reject(new Error(message))
    }

    return Promise.reject(error)
  }
)
