import type { PageResult } from './course'

export type PaymentBizType = 1

export type PaymentChannel = 1 | 2 | 3

export type PaymentRefundStatus = 0 | 1 | 2 | 3 | 4

export interface AdminPaymentRefundOrderPageQuery {
  current: number
  size: number
  refundNo?: string
  paymentNo?: string
  bizType?: PaymentBizType
  bizOrderNo?: string
  userId?: string
  payChannel?: PaymentChannel
  status?: PaymentRefundStatus
  thirdRefundNo?: string
  createTimeStart?: string
  createTimeEnd?: string
}

export interface PaymentRefundOrder {
  id: string
  refundNo: string
  paymentNo: string
  bizType: PaymentBizType
  bizOrderId?: string
  bizOrderNo: string
  userId: string
  payAmount: number
  refundAmount: number
  currency?: string
  payChannel: PaymentChannel
  status: PaymentRefundStatus
  thirdTradeNo?: string
  thirdRefundNo?: string
  refundReason?: string
  failureReason?: string
  applyTime?: string
  refundTime?: string
  closeTime?: string
  createTime?: string
  updateTime?: string
}

export type PaymentRefundOrderPageResult = PageResult<PaymentRefundOrder>
