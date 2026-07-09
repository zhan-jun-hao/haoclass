import { http, unwrapResult, type ApiResult } from './http'
import type {
  ClientMyCourse,
  Course,
  CourseEpisode,
  CourseEpisodeFormPayload,
  CourseFormPayload,
  CoursePageQuery,
  CoursePlay,
  CourseSearchQuery,
  CourseSearchResult,
  PageResult
} from '@/types/course'

export async function listAdminCourses(params: CoursePageQuery) {
  const response = await http.get<ApiResult<PageResult<Course>>>('/main/admin/courses', { params })
  return unwrapResult(response.data)
}

export async function getAdminCourseDetail(id: string) {
  const response = await http.get<ApiResult<Course>>(`/main/admin/courses/${id}`)
  return unwrapResult(response.data)
}

export async function createAdminCourse(payload: CourseFormPayload) {
  const response = await http.post<ApiResult<void>>('/main/admin/courses', payload)
  return unwrapResult(response.data)
}

export async function updateAdminCourse(id: string, payload: CourseFormPayload) {
  const response = await http.put<ApiResult<void>>(`/main/admin/courses/${id}`, payload)
  return unwrapResult(response.data)
}

export async function deleteAdminCourse(id: string) {
  const response = await http.delete<ApiResult<void>>(`/main/admin/courses/${id}`)
  return unwrapResult(response.data)
}

export async function publishAdminCourse(id: string) {
  const response = await http.put<ApiResult<void>>(`/main/admin/courses/${id}/publish`)
  return unwrapResult(response.data)
}

export async function unpublishAdminCourse(id: string) {
  const response = await http.put<ApiResult<void>>(`/main/admin/courses/${id}/unpublish`)
  return unwrapResult(response.data)
}

export async function listAdminCourseEpisodes(courseId: string) {
  const response = await http.get<ApiResult<CourseEpisode[]>>(`/main/admin/courses/${courseId}/episodes`)
  return unwrapResult(response.data)
}

export async function getAdminCourseEpisodeDetail(courseId: string, episodeId: string) {
  const response = await http.get<ApiResult<CourseEpisode>>(`/main/admin/courses/${courseId}/episodes/${episodeId}`)
  return unwrapResult(response.data)
}

export async function createAdminCourseEpisode(courseId: string, payload: CourseEpisodeFormPayload) {
  const response = await http.post<ApiResult<void>>(`/main/admin/courses/${courseId}/episodes`, payload)
  return unwrapResult(response.data)
}

export async function updateAdminCourseEpisode(
  courseId: string,
  episodeId: string,
  payload: CourseEpisodeFormPayload
) {
  const response = await http.put<ApiResult<void>>(`/main/admin/courses/${courseId}/episodes/${episodeId}`, payload)
  return unwrapResult(response.data)
}

export async function deleteAdminCourseEpisode(courseId: string, episodeId: string) {
  const response = await http.delete<ApiResult<void>>(`/main/admin/courses/${courseId}/episodes/${episodeId}`)
  return unwrapResult(response.data)
}

export async function rebuildAdminCourseSearchIndex() {
  const response = await http.post<ApiResult<number>>('/main/admin/search/courses/rebuild')
  return unwrapResult(response.data)
}

export async function syncAdminCourseSearchIndex(id: string) {
  const response = await http.put<ApiResult<void>>(`/main/admin/search/courses/${id}/sync`)
  return unwrapResult(response.data)
}

export async function deleteAdminCourseSearchIndex(id: string) {
  const response = await http.delete<ApiResult<void>>(`/main/admin/search/courses/${id}`)
  return unwrapResult(response.data)
}

export async function listClientCourses(params?: Partial<CoursePageQuery>) {
  const response = await http.get<ApiResult<PageResult<Course>>>('/main/client/courses', {
    params: {
      current: params?.current ?? 1,
      size: params?.size ?? 20,
      categoryName: params?.categoryName,
      title: params?.title
    }
  })
  return unwrapResult(response.data)
}

export async function searchClientCourses(params?: Partial<CourseSearchQuery>) {
  const response = await http.get<ApiResult<PageResult<CourseSearchResult>>>('/main/client/courses/search', {
    params: {
      current: params?.current ?? 1,
      size: params?.size ?? 12,
      keyword: params?.keyword,
      categoryName: params?.categoryName,
      chargeType: params?.chargeType === '' ? undefined : params?.chargeType,
      sortType: params?.sortType ?? 0
    }
  })
  return unwrapResult(response.data)
}

export async function getClientCourseDetail(id: string) {
  const response = await http.get<ApiResult<Course>>(`/main/client/courses/${id}`)
  return unwrapResult(response.data)
}

export async function listClientCourseEpisodes(courseId: string) {
  const response = await http.get<ApiResult<CourseEpisode[]>>(`/main/client/courses/${courseId}/episodes`)
  return unwrapResult(response.data)
}

export async function getClientCoursePlay(courseId: string, episodeId: string) {
  const response = await http.get<ApiResult<CoursePlay>>(`/main/client/play/courses/${courseId}/episodes/${episodeId}`)
  return unwrapResult(response.data)
}

export async function listClientMyCourses() {
  const response = await http.get<ApiResult<ClientMyCourse[]>>('/main/client/my/courses')
  return unwrapResult(response.data)
}

export async function listCourses(params?: Partial<CoursePageQuery>) {
  const response = await listClientCourses(params)

  return response.records
}

export async function getCourseDetail(id: string) {
  const [course, episodes] = await Promise.all([
    getClientCourseDetail(id),
    listClientCourseEpisodes(id)
  ])

  return {
    ...course,
    episodes
  }
}
