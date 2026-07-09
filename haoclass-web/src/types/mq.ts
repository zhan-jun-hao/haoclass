import type { PageResult } from './course'

export type MqDeadMessageStatus = 0 | 1 | 2 | 3

export interface MqDeadMessagePageQuery {
  current: number
  size: number
  bizType?: string
  bizId?: string
  status?: MqDeadMessageStatus
  sourceQueue?: string
  createTimeStart?: string
  createTimeEnd?: string
}

export interface MqDeadMessageBasic {
  id: string
  bizType: string
  bizId: string
  sourceQueue: string
  deadReason: string
  lastError: string
  status: MqDeadMessageStatus
  retryCount: number
  createTime?: string
}

export interface MqDeadMessageDetail extends MqDeadMessageBasic {
  sourceExchange: string
  sourceRoutingKey: string
  deadQueue: string
  deadExchange: string
  deadRoutingKey: string
  messageBody: string
  headers: string
  lastRetryTime?: string
  updateTime?: string
}

export type MqDeadMessagePageResult = PageResult<MqDeadMessageBasic>
