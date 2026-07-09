import type { PageResult } from './course'

export type CourseOrderStatus = 0 | 1 | 2 | 3 | 4

export type CourseOrderPayType = 0 | 1 | 2 | 3

export interface CourseOrder {
  id: string
  orderNo: string
  userId: string
  courseId: string
  courseTitle: string
  coverUrl: string
  coursePrice: number
  userCouponId?: string
  couponName?: string
  discountAmount: number
  payAmount: number
  status: CourseOrderStatus
  payType: CourseOrderPayType
  thirdTradeNo?: string
  expireTime?: string
  payTime?: string
  cancelTime?: string
  refundTime?: string
  createTime?: string
  updateTime?: string
}

export interface AdminCourseOrderPageQuery {
  current: number
  size: number
  orderNo?: string
  userId?: string
  courseId?: string
  status?: CourseOrderStatus
  payType?: CourseOrderPayType
  createTimeStart?: string
  createTimeEnd?: string
}

export interface ClientCourseOrderPageQuery {
  current: number
  size: number
  status?: CourseOrderStatus
}

export interface CourseOrderCreatePayload {
  courseId: string
  userCouponId?: string
}

export interface CourseOrderAvailableCoupon {
  userCouponId: string
  couponName: string
  thresholdAmount: number
  discountAmount: number
  payAmount: number
  validEndTime: string
}

export interface CourseOrderPayResult {
  id: string
  orderNo: string
  status: CourseOrderStatus
  paymentNo: string
  payAmount: number
  payChannel: number
  paymentStatus: number
  expireTime?: string
  payTime?: string
}

export interface CourseOrderRefundResult {
  id: string
  orderNo: string
  status: CourseOrderStatus
  refundNo: string
  refundAmount: number
  refundStatus: number
  refundTime?: string
}

export type CourseOrderPageResult = PageResult<CourseOrder>
