import { http, unwrapResult, type ApiResult } from './http'
import { mockSuccessRefund } from './refund'
import type {
  AdminCourseOrderPageQuery,
  ClientCourseOrderPageQuery,
  CourseOrder,
  CourseOrderAvailableCoupon,
  CourseOrderCreatePayload,
  CourseOrderPageResult,
  CourseOrderPayResult,
  CourseOrderRefundResult
} from '@/types/order'

const PAID_ORDER_STATUS = 1
const REFUNDED_ORDER_STATUS = 3
const DEFAULT_PAY_POLL_INTERVAL = 2000
const DEFAULT_PAY_POLL_TIMES = 45

export async function listAdminOrders(params: AdminCourseOrderPageQuery) {
  const response = await http.get<ApiResult<CourseOrderPageResult>>('/main/admin/orders', { params })
  return unwrapResult(response.data)
}

export async function getAdminOrderDetail(id: string) {
  const response = await http.get<ApiResult<CourseOrder>>(`/main/admin/orders/${id}`)
  return unwrapResult(response.data)
}

export async function listClientOrders(params: ClientCourseOrderPageQuery) {
  const response = await http.get<ApiResult<CourseOrderPageResult>>('/main/client/orders', { params })
  return unwrapResult(response.data)
}

export async function getClientOrderDetail(id: string) {
  const response = await http.get<ApiResult<CourseOrder>>(`/main/client/orders/${id}`)
  return unwrapResult(response.data)
}

export async function createClientOrder(payload: CourseOrderCreatePayload) {
  const response = await http.post<ApiResult<CourseOrder>>('/main/client/orders', payload)
  return unwrapResult(response.data)
}

export async function listClientOrderAvailableCoupons(courseId: string) {
  const response = await http.get<ApiResult<CourseOrderAvailableCoupon[]>>('/main/client/orders/available', {
    params: { courseId }
  })
  return unwrapResult(response.data)
}

export async function createClientOrderPayment(id: string) {
  // 第一步：让 main-service 校验课程订单，并远程调用 pay-service 创建支付单。
  const response = await http.post<ApiResult<CourseOrderPayResult>>(`/main/client/orders/${id}/mock-pay`)
  return unwrapResult(response.data)
}

export async function mockSuccessPayment(paymentNo: string) {
  // 第二步：模拟用户已经完成支付。真实接微信支付后，这一步会变成微信支付回调。
  const response = await http.post<ApiResult<void>>(`/pay/client/payments/${paymentNo}/mock-success`)
  return unwrapResult(response.data)
}

export async function waitClientOrderPaid(id: string) {
  // 第三步：支付成功后，pay-service 会通过 MQ 异步通知 main-service 履约。
  // 所以前端不能立刻认为课程已开通，要轮询课程订单状态，等 course_order 变成已支付。
  for (let i = 0; i < DEFAULT_PAY_POLL_TIMES; i += 1) {
    const order = await getClientOrderDetail(id)

    if (order.status === PAID_ORDER_STATUS) {
      return order
    }

    await sleep(DEFAULT_PAY_POLL_INTERVAL)
  }

  throw new Error('支付已完成，订单履约还在处理中，请稍后刷新订单状态')
}

export async function mockPayClientOrder(id: string) {
  // 为页面封装完整流程：创建支付单 -> 模拟支付成功 -> 等待 main-service 履约完成。
  const payment = await createClientOrderPayment(id)
  await mockSuccessPayment(payment.paymentNo)
  return waitClientOrderPaid(id)
}

export async function createClientOrderRefund(id: string) {
  // 第一步：让 main-service 校验课程订单，并远程调用 pay-service 创建退款单。
  const response = await http.post<ApiResult<CourseOrderRefundResult>>(`/main/client/orders/${id}/mock-refund`)
  return unwrapResult(response.data)
}

export async function waitClientOrderRefunded(id: string) {
  // 第二步：退款成功后，pay-service 会通过 MQ 异步通知 main-service 回收权益。
  // 所以前端要轮询课程订单状态，等 course_order 变成已退款。
  for (let i = 0; i < DEFAULT_PAY_POLL_TIMES; i += 1) {
    const order = await getClientOrderDetail(id)

    if (order.status === REFUNDED_ORDER_STATUS) {
      return order
    }

    await sleep(DEFAULT_PAY_POLL_INTERVAL)
  }

  throw new Error('退款已提交，订单退款履约还在处理中，请稍后刷新订单状态')
}

export async function mockRefundClientOrder(id: string) {
  // 为页面封装完整流程：创建退款单 -> 模拟退款成功回调 -> MQ履约 -> 订单已退款。
  const refund = await createClientOrderRefund(id)
  await mockSuccessRefund(refund.refundNo)
  return waitClientOrderRefunded(id)
}

export async function cancelClientOrder(id: string) {
  const response = await http.post<ApiResult<void>>(`/main/client/orders/${id}/cancel`)
  return unwrapResult(response.data)
}

function sleep(ms: number) {
  return new Promise((resolve) => window.setTimeout(resolve, ms))
}
