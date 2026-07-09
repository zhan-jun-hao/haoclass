import { http, unwrapResult, type ApiResult } from './http'

export interface CourseLearningProgress {
  id?: string
  userId?: string
  courseId: string
  episodeId: string
  progressSeconds: number
  finished: 0 | 1
  finishTime?: string
  lastLearnTime?: string
  updateTime?: string
}

export interface CourseLearningProgressPayload {
  courseId: string
  episodeId: string
  progressSeconds: number
}

export async function getEpisodeLearningProgress(courseId: string, episodeId: string) {
  const response = await http.get<ApiResult<CourseLearningProgress>>(
    `/main/client/learning-progress/courses/${courseId}/episodes/${episodeId}`
  )
  return unwrapResult(response.data)
}

export async function listCourseLearningProgress(courseId: string) {
  const response = await http.get<ApiResult<CourseLearningProgress[]>>(
    `/main/client/learning-progress/courses/${courseId}`
  )
  return unwrapResult(response.data)
}

export async function reportLearningProgress(payload: CourseLearningProgressPayload) {
  const response = await http.post<ApiResult<void>>('/main/client/learning-progress', payload)
  return unwrapResult(response.data)
}
