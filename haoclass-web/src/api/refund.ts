import { http, unwrapResult, type ApiResult } from './http'
import type {
  AdminPaymentRefundOrderPageQuery,
  PaymentRefundOrder,
  PaymentRefundOrderPageResult
} from '@/types/refund'

interface PayNotifyResult {
  code: 'SUCCESS' | 'FAIL'
  message: string
}

export async function listAdminRefundOrders(params: AdminPaymentRefundOrderPageQuery) {
  const response = await http.get<ApiResult<PaymentRefundOrderPageResult>>('/pay/admin/refunds', { params })
  return unwrapResult(response.data)
}

export async function getAdminRefundOrderDetail(refundNo: string) {
  const response = await http.get<ApiResult<PaymentRefundOrder>>(`/pay/admin/refunds/${refundNo}`)
  return unwrapResult(response.data)
}

export async function mockSuccessRefund(refundNo: string) {
  // 这个接口模拟“第三方支付平台通知退款成功”。
  // 它不是我们统一 Result<T> 格式，而是微信回调风格：{ code: 'SUCCESS', message: '成功' }。
  const response = await http.post<PayNotifyResult>('/pay/notify/mock/refund', { refundNo })

  if (response.data.code !== 'SUCCESS') {
    throw new Error(response.data.message || '模拟退款回调失败')
  }
}
