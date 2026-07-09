<template>
  <div class="admin-shell">
    <aside class="admin-sidebar">
      <div class="admin-logo">好课后台</div>
      <router-link to="/admin">统计</router-link>
      <router-link to="/admin/courses">课程管理</router-link>
      <router-link to="/admin/orders">订单管理</router-link>
      <router-link to="/admin/refunds">退款管理</router-link>
      <router-link to="/admin/mq-dead-messages">异常消息管理</router-link>
      <router-link to="/admin/comments">评论管理</router-link>
      <router-link to="/admin/coupons">优惠券管理</router-link>
      <router-link to="/admin/ai-knowledge">AI知识库</router-link>
      <router-link to="/admin/users">用户管理</router-link>
    </aside>
    <section class="admin-main">
      <header class="admin-header">
        <span>管理端</span>
        <div class="admin-actions">
          <span>{{ authStore.nickname || authStore.phone }}</span>
          <router-link to="/client">返回客户端</router-link>
          <el-button size="small" @click="handleLogout">退出</el-button>
        </div>
      </header>
      <router-view />
    </section>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>
