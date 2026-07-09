import { http, unwrapResult, type ApiResult } from './http'
import type {
  CouponTemplate,
  CouponTemplatePageQuery,
  CouponTemplatePageResult,
  CouponTemplatePayload,
  ClientCouponTemplate,
  ClientMyCouponPageQuery,
  ClientMyCouponPageResult
} from '@/types/coupon'

const adminTemplatePath = '/coupon/admin/templates'
const clientTemplatePath = '/coupon/client/templates'
const clientMyCouponPath = '/coupon/client/my-coupons'

export async function listAdminCouponTemplates(params: CouponTemplatePageQuery) {
  const response = await http.get<ApiResult<CouponTemplatePageResult>>(adminTemplatePath, { params })
  return unwrapResult(response.data)
}

export async function getAdminCouponTemplateDetail(id: string) {
  const response = await http.get<ApiResult<CouponTemplate>>(`${adminTemplatePath}/${id}`)
  return unwrapResult(response.data)
}

export async function createAdminCouponTemplate(payload: CouponTemplatePayload) {
  const response = await http.post<ApiResult<void>>(adminTemplatePath, payload)
  return unwrapResult(response.data)
}

export async function updateAdminCouponTemplate(id: string, payload: CouponTemplatePayload) {
  const response = await http.put<ApiResult<void>>(`${adminTemplatePath}/${id}`, payload)
  return unwrapResult(response.data)
}

export async function deleteAdminCouponTemplate(id: string) {
  const response = await http.delete<ApiResult<void>>(`${adminTemplatePath}/${id}`)
  return unwrapResult(response.data)
}

export async function publishAdminCouponTemplate(id: string) {
  const response = await http.put<ApiResult<void>>(`${adminTemplatePath}/${id}/publish`)
  return unwrapResult(response.data)
}

export async function stopAdminCouponTemplate(id: string) {
  const response = await http.put<ApiResult<void>>(`${adminTemplatePath}/${id}/stop`)
  return unwrapResult(response.data)
}

export async function listClientCouponTemplates() {
  const response = await http.get<ApiResult<ClientCouponTemplate[]>>(clientTemplatePath)
  return unwrapResult(response.data)
}

export async function receiveClientCouponTemplate(id: string) {
  const response = await http.post<ApiResult<void>>(`${clientTemplatePath}/${id}/receive`)
  return unwrapResult(response.data)
}

export async function listClientMyCoupons(params: ClientMyCouponPageQuery) {
  const response = await http.get<ApiResult<ClientMyCouponPageResult>>(clientMyCouponPath, { params })
  return unwrapResult(response.data)
}
