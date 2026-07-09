/**
 * 管理端知识入库请求对象。
 */
export interface AdminAiKnowledgeIngestPayload {
  /**
   * 知识标题
   */
  title: string

  /**
   * 知识来源，例如产品设计书、后台配置、人工录入
   */
  source?: string

  /**
   * 知识正文
   */
  content: string
}

/**
 * 管理端知识入库响应对象。
 */
export interface AdminAiKnowledgeIngestResult {
  /**
   * 本次入库后生成的知识片段数量
   */
  segmentCount: number
}

/**
 * C端AI客服提问请求对象。
 */
export interface ClientAiChatPayload {
  /**
   * 会话ID。首次提问可以不传，后端会返回新的会话ID
   */
  conversationId?: string

  /**
   * 用户问题
   */
  question: string

  /**
   * 从向量库召回的相似片段数量
   */
  topK?: number
}

/**
 * AI回答引用的知识片段。
 */
export interface ClientAiReference {
  /**
   * 相似度分数
   */
  score: number

  /**
   * 知识片段内容
   */
  text: string
}

/**
 * C端AI客服回答响应对象。
 */
export interface ClientAiChatResult {
  /**
   * 会话ID
   */
  conversationId: string

  /**
   * AI回答内容
   */
  answer: string

  /**
   * AI回答参考的知识片段
   */
  references: ClientAiReference[]
}
