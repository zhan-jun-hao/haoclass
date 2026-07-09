export const SPRING_CLOUD_MICROSERVICES_COVER = '/images/course-springcloud-microservices.png'

interface CourseCoverSource {
  title?: string
  subtitle?: string
  categoryName?: string
  coverUrl?: string
}

export function resolveCourseCover(course?: CourseCoverSource | null) {
  if (isSpringCloudMicroservicesCourse(course)) {
    return SPRING_CLOUD_MICROSERVICES_COVER
  }

  return course?.coverUrl?.trim() || SPRING_CLOUD_MICROSERVICES_COVER
}

export function useFallbackCourseCover(event: Event) {
  const image = event.target as HTMLImageElement | null

  if (!image || image.dataset.fallbackApplied === 'true') {
    return
  }

  image.dataset.fallbackApplied = 'true'
  image.src = SPRING_CLOUD_MICROSERVICES_COVER
}

function isSpringCloudMicroservicesCourse(course?: CourseCoverSource | null) {
  const keywords = [
    course?.title,
    course?.subtitle,
    course?.categoryName
  ]
    .filter(Boolean)
    .join(' ')
    .toLowerCase()

  return keywords.includes('springcloud') || keywords.includes('spring cloud') || keywords.includes('微服务')
}
