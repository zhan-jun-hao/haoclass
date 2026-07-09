import { http, unwrapResult, type ApiResult } from './http'
import type {
  AdminCourseCommentPageQuery,
  CourseComment,
  CourseCommentPage,
  CourseCommentPageQuery,
  CourseCommentPayload,
  MyCourseCommentPage,
  MyCourseCommentPageQuery
} from '@/types/comment'

export async function listAdminCourseComments(params: AdminCourseCommentPageQuery) {
  const response = await http.get<ApiResult<CourseCommentPage>>('/main/admin/course-comments', { params })
  return unwrapResult(response.data)
}

export async function getAdminCourseCommentDetail(id: string) {
  const response = await http.get<ApiResult<CourseComment>>(`/main/admin/course-comments/${id}`)
  return unwrapResult(response.data)
}

export async function approveAdminCourseComment(id: string) {
  const response = await http.put<ApiResult<void>>(`/main/admin/course-comments/${id}/approve`)
  return unwrapResult(response.data)
}

export async function rejectAdminCourseComment(id: string) {
  const response = await http.put<ApiResult<void>>(`/main/admin/course-comments/${id}/reject`)
  return unwrapResult(response.data)
}

export async function hideAdminCourseComment(id: string) {
  const response = await http.put<ApiResult<void>>(`/main/admin/course-comments/${id}/hide`)
  return unwrapResult(response.data)
}

export async function deleteAdminCourseComment(id: string) {
  const response = await http.delete<ApiResult<void>>(`/main/admin/course-comments/${id}`)
  return unwrapResult(response.data)
}

export async function listClientCourseComments(params: CourseCommentPageQuery) {
  const response = await http.get<ApiResult<CourseCommentPage>>('/main/client/course-comments', { params })
  return unwrapResult(response.data)
}

export async function createClientCourseComment(payload: CourseCommentPayload) {
  const response = await http.post<ApiResult<CourseComment>>('/main/client/course-comments', payload)
  return unwrapResult(response.data)
}

export async function deleteClientCourseComment(id: string) {
  const response = await http.delete<ApiResult<void>>(`/main/client/course-comments/${id}`)
  return unwrapResult(response.data)
}

export async function likeClientCourseComment(id: string) {
  const response = await http.put<ApiResult<void>>(`/main/client/course-comments/${id}/like`)
  return unwrapResult(response.data)
}

export async function unlikeClientCourseComment(id: string) {
  const response = await http.delete<ApiResult<void>>(`/main/client/course-comments/${id}/like`)
  return unwrapResult(response.data)
}

export async function listClientMyComments(params: MyCourseCommentPageQuery) {
  const response = await http.get<ApiResult<MyCourseCommentPage>>('/main/client/my/comment', { params })
  return unwrapResult(response.data)
}
