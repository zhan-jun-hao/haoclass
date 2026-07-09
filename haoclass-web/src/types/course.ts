export interface CourseEpisode {
  id: string | number
  courseId?: string
  title: string
  videoUrl?: string
  durationSeconds?: number
  freePreview?: 0 | 1
  sort?: number
  status?: CourseStatus
  duration?: string
}

export interface CoursePlay {
  courseId: string
  episodeId: string
  episodeTitle: string
  videoUrl: string
  durationSeconds: number
}

export interface ClientMyCourse {
  courseId: string
  title: string
  coverUrl: string
  teacherName: string
  episodeCount: number
  finishedEpisodeCount: number
  learningPercent: number
  lastEpisodeId?: string
  lastEpisodeTitle?: string
  lastProgressSeconds?: number
  lastLearnTime?: string
  grantTime?: string
  expireTime?: string
}

export type CourseStatus = 0 | 1 | 2

export type ChargeType = 0 | 1 | 2

export type CourseSearchSortType = 0 | 1 | 2 | 3 | 4

export interface Course {
  id: string
  categoryName: string
  title: string
  subtitle: string
  coverUrl: string
  teacherName: string
  price: number
  summary: string
  detail?: string
  episodeCount: number
  buyCount: number
  sort: number
  status: CourseStatus
  chargeType: ChargeType
  episodes?: CourseEpisode[]
}

export interface CoursePageQuery {
  current: number
  size: number
  categoryName?: string
  title?: string
  status?: CourseStatus
}

export interface CourseSearchQuery {
  /**
   * 当前页码
   */
  current: number

  /**
   * 每页条数
   */
  size: number

  /**
   * 搜索关键词，匹配课程标题、副标题、摘要、讲师名称等字段
   */
  keyword?: string

  /**
   * 课程分类名称
   */
  categoryName?: string

  /**
   * 收费类型：0免费 1付费 2VIP免费
   */
  chargeType?: ChargeType | ''

  /**
   * 排序类型：0综合 1最新 2销量 3价格升序 4价格降序
   */
  sortType?: CourseSearchSortType
}

export interface CourseSearchResult extends Course {
  /**
   * ES返回的高亮课程标题
   */
  highlightTitle?: string

  /**
   * ES返回的高亮课程副标题
   */
  highlightSubtitle?: string

  /**
   * ES返回的高亮课程摘要
   */
  highlightSummary?: string
}

export interface CourseFormPayload {
  categoryName: string
  title: string
  subtitle: string
  coverUrl: string
  teacherName: string
  summary: string
  detail: string
  price: number
  sort: number
  status?: CourseStatus
  chargeType?: ChargeType
}

export interface CourseEpisodeFormPayload {
  title: string
  videoUrl: string
  durationSeconds: number
  freePreview: 0 | 1
  sort: number
  status?: CourseStatus
}

export interface PageResult<T> {
  total: number
  curPage: number
  size: number
  pages: number
  records: T[]
}
