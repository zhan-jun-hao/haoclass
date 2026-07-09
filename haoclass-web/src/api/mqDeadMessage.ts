import { http, unwrapResult, type ApiResult } from './http'
import type {
  MqDeadMessageDetail,
  MqDeadMessagePageQuery,
  MqDeadMessagePageResult
} from '@/types/mq'

export async function listAdminMqDeadMessages(params: MqDeadMessagePageQuery) {
  const response = await http.get<ApiResult<MqDeadMessagePageResult>>('/main/admin/mq-dead-messages/page', { params })
  return unwrapResult(response.data)
}

export async function getAdminMqDeadMessageDetail(id: string) {
  const response = await http.get<ApiResult<MqDeadMessageDetail>>(`/main/admin/mq-dead-messages/${id}`)
  return unwrapResult(response.data)
}

export async function retryAdminMqDeadMessage(id: string) {
  // 人工重试：把死信表里的原始消息重新投回原业务交换机和路由键。
  const response = await http.post<ApiResult<void>>(`/main/admin/mq-dead-messages/${id}/retry`)
  return unwrapResult(response.data)
}

export async function ignoreAdminMqDeadMessage(id: string) {
  // 标记忽略：表示这条死信已经人工确认，不再继续处理。
  const response = await http.post<ApiResult<void>>(`/main/admin/mq-dead-messages/${id}/ignore`)
  return unwrapResult(response.data)
}
