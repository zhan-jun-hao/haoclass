import { http, unwrapResult, type ApiResult } from './http'
import type {
  AdminAiKnowledgeIngestPayload,
  AdminAiKnowledgeIngestResult,
  ClientAiChatPayload,
  ClientAiChatResult
} from '@/types/ai'

const adminKnowledgePath = '/ai/admin/knowledge'
const clientChatPath = '/ai/client/chat'

/**
 * 管理端知识入库。
 */
export async function ingestAdminAiKnowledge(payload: AdminAiKnowledgeIngestPayload) {
  const response = await http.post<ApiResult<AdminAiKnowledgeIngestResult>>(
    `${adminKnowledgePath}/ingest`,
    payload,
    {
      // 知识入库需要调用向量模型，时间会比普通CRUD接口长一些。
      timeout: 60000
    }
  )
  return unwrapResult(response.data)
}

/**
 * C端AI客服提问。
 */
export async function askClientAi(payload: ClientAiChatPayload) {
  const response = await http.post<ApiResult<ClientAiChatResult>>(clientChatPath, payload, {
    // 大模型生成回答可能超过全局10秒超时，所以这里单独放宽。
    timeout: 60000
  })
  return unwrapResult(response.data)
}
