import type { PageResult } from './course'

export type CourseCommentStatus = 0 | 1 | 2 | 3

export interface CourseComment {
  id: string
  courseId: string
  episodeId: string
  userId: string
  nickname?: string
  avatarUrl?: string
  parentId: string
  rootId: string
  replyToNickname?: string
  content: string
  likeCount: number
  liked: boolean
  replyCount: number
  status: CourseCommentStatus
  createTime: string
  updateTime?: string
  createdUser?: string
  updatedUser?: string
}

export interface CourseCommentPageQuery {
  current: number
  size: number
  courseId: string
  episodeId: string
  rootId: string
}

export interface CourseCommentPayload {
  courseId: string
  episodeId: string
  parentId?: string
  content: string
}

export interface AdminCourseCommentPageQuery {
  current: number
  size: number
  courseId?: string
  episodeId?: string
  userId?: string
  rootId?: string
  status?: CourseCommentStatus
  content?: string
  createTimeStart?: string
  createTimeEnd?: string
}

export interface MyCourseComment {
  id: string
  courseId: string
  episodeId: string
  courseTitle: string
  episodeTitle: string
  content: string
  likeCount: number
  status: CourseCommentStatus
  createTime: string
}

export interface MyCourseCommentPageQuery {
  current: number
  size: number
  status?: CourseCommentStatus
  content?: string
  createTimeStart?: string
  createTimeEnd?: string
}

export type CourseCommentPage = PageResult<CourseComment>

export type MyCourseCommentPage = PageResult<MyCourseComment>
