import type { Course } from '@/types/course'

export const mockCourses: Course[] = [
  {
    id: '1',
    title: '命理入门基础课',
    categoryName: '经典课程',
    coverUrl: 'https://images.unsplash.com/photo-1519681393784-d120267933ba?auto=format&fit=crop&w=900&q=80',
    price: 19900,
    summary: '从基础概念、排盘方法到案例理解，建立完整的入门知识框架。',
    subtitle: '建立完整入门知识框架',
    teacherName: '讲师',
    detail: '从基础概念、排盘方法到案例理解，建立完整的入门知识框架。',
    episodeCount: 12,
    buyCount: 0,
    sort: 100,
    status: 1,
    chargeType: 1,
    episodes: [
      { id: 1, title: '第一讲：课程导学', duration: '12:30' },
      { id: 2, title: '第二讲：基础概念', duration: '18:42' },
      { id: 3, title: '第三讲：案例拆解', duration: '24:16' }
    ]
  },
  {
    id: '2',
    title: '风水实战案例课',
    categoryName: '实战提升',
    coverUrl: 'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=900&q=80',
    price: 29900,
    summary: '围绕真实空间案例，学习判断思路、调整方法和落地流程。',
    subtitle: '真实空间案例拆解',
    teacherName: '玄明老师',
    detail: '围绕真实空间案例，学习判断思路、调整方法和落地流程。',
    episodeCount: 18,
    buyCount: 0,
    sort: 90,
    status: 1,
    chargeType: 1,
    episodes: [
      { id: 1, title: '第一讲：案例分析方法', duration: '16:08' },
      { id: 2, title: '第二讲：户型判断', duration: '21:35' },
      { id: 3, title: '第三讲：布局调整', duration: '26:50' }
    ]
  },
  {
    id: '3',
    title: '择日应用精讲',
    categoryName: '专题课程',
    coverUrl: 'https://images.unsplash.com/photo-1506784983877-45594efa4cbe?auto=format&fit=crop&w=900&q=80',
    price: 15900,
    summary: '讲清择日的应用场景、避坑要点和常见问题。',
    subtitle: '择日应用场景和避坑要点',
    teacherName: '云台老师',
    detail: '讲清择日的应用场景、避坑要点和常见问题。',
    episodeCount: 9,
    buyCount: 0,
    sort: 80,
    status: 1,
    chargeType: 1,
    episodes: [
      { id: 1, title: '第一讲：择日应用边界', duration: '13:10' },
      { id: 2, title: '第二讲：常见误区', duration: '19:21' },
      { id: 3, title: '第三讲：案例练习', duration: '22:44' }
    ]
  }
]
