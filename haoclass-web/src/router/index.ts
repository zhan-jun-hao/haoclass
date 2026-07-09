import { createRouter, createWebHistory } from 'vue-router'
import ClientLayout from '@/layouts/ClientLayout.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'
import ClientHome from '@/views/client/ClientHome.vue'
import CourseDetail from '@/views/client/CourseDetail.vue'
import ClientOrders from '@/views/client/ClientOrders.vue'
import ClientMyCourses from '@/views/client/ClientMyCourses.vue'
import ClientMyComments from '@/views/client/ClientMyComments.vue'
import ClientCoupons from '@/views/client/ClientCoupons.vue'
import ClientAiChat from '@/views/client/ClientAiChat.vue'
import WatchCourse from '@/views/client/WatchCourse.vue'
import LoginPage from '@/views/LoginPage.vue'
import AdminDashboard from '@/views/admin/AdminDashboard.vue'
import AdminCourses from '@/views/admin/AdminCourses.vue'
import AdminOrders from '@/views/admin/AdminOrders.vue'
import AdminRefunds from '@/views/admin/AdminRefunds.vue'
import AdminComments from '@/views/admin/AdminComments.vue'
import AdminUsers from '@/views/admin/AdminUsers.vue'
import AdminCoupons from '@/views/admin/AdminCoupons.vue'
import AdminDeadMessages from '@/views/admin/AdminDeadMessages.vue'
import AdminAiKnowledge from '@/views/admin/AdminAiKnowledge.vue'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/client' },
    { path: '/login', component: LoginPage },
    {
      path: '/client',
      component: ClientLayout,
      children: [
        { path: '', component: ClientHome },
        { path: 'my/courses', component: ClientMyCourses, meta: { requiresAuth: true } },
        { path: 'my/comments', component: ClientMyComments, meta: { requiresAuth: true } },
        { path: 'coupons', component: ClientCoupons, meta: { requiresAuth: true } },
        { path: 'ai-chat', component: ClientAiChat, meta: { requiresAuth: true } },
        { path: 'orders', component: ClientOrders, meta: { requiresAuth: true } },
        { path: 'courses/:id', component: CourseDetail },
        { path: 'courses/:id/watch', component: WatchCourse, meta: { requiresAuth: true } }
      ]
    },
    {
      path: '/admin',
      component: AdminLayout,
      meta: { requiresAuth: true },
      children: [
        { path: '', component: AdminDashboard },
        { path: 'courses', component: AdminCourses },
        { path: 'orders', component: AdminOrders },
        { path: 'refunds', component: AdminRefunds },
        { path: 'mq-dead-messages', component: AdminDeadMessages },
        { path: 'comments', component: AdminComments },
        { path: 'users', component: AdminUsers },
        { path: 'coupons', component: AdminCoupons },
        { path: 'ai-knowledge', component: AdminAiKnowledge }
      ]
    }
  ]
})

router.beforeEach((to) => {
  const authStore = useAuthStore()

  if (to.meta.requiresAuth && !authStore.token) {
    return {
      path: '/login',
      query: { redirect: to.fullPath }
    }
  }

  if (to.path.startsWith('/admin') && authStore.token && authStore.role !== '1') {
    return '/client'
  }

  if (to.path === '/login' && authStore.token) {
    return authStore.role === '1' ? '/admin' : '/client'
  }

  return true
})

export default router
