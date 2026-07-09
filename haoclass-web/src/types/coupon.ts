import type { PageResult } from './course'

export type CouponTemplateStatus = 0 | 1 | 2
export type UserCouponStatus = 0 | 1 | 2 | 3

export interface CouponTemplate {
  id: string
  couponName: string
  description?: string
  thresholdAmount: number
  discountAmount: number
  totalStock: number
  receivedCount: number
  receiveStartTime: string
  receiveEndTime: string
  validStartTime: string
  validEndTime: string
  status: CouponTemplateStatus
  createdUser?: string
  updatedUser?: string
  createTime?: string
  updateTime?: string
}

export interface CouponTemplatePageQuery {
  current: number
  size: number
  couponName?: string
  status?: CouponTemplateStatus
  createTimeStart?: string
  createTimeEnd?: string
}

export interface CouponTemplatePayload {
  couponName: string
  description?: string
  thresholdAmount: number
  discountAmount: number
  totalStock: number
  receiveStartTime: string
  receiveEndTime: string
  validStartTime: string
  validEndTime: string
}

export type CouponTemplatePageResult = PageResult<CouponTemplate>

export interface ClientCouponTemplate {
  id: string
  couponName: string
  thresholdAmount: number
  discountAmount: number
  totalStock: number
  receiveStartTime: string
  receiveEndTime: string
  validEndTime: string
  status: CouponTemplateStatus
  createTime?: string
  updateTime?: string
  received: boolean
}

export interface ClientMyCoupon {
  id: string
  couponTemplateId: string
  couponName: string
  thresholdAmount: number
  discountAmount: number
  status: UserCouponStatus
  orderId?: string
  lockTime?: string
  lockExpireTime?: string
  useTime?: string
  validStartTime: string
  validEndTime: string
  createTime: string
}

export interface ClientMyCouponPageQuery {
  current: number
  size: number
  status?: UserCouponStatus
}

export type ClientMyCouponPageResult = PageResult<ClientMyCoupon>
