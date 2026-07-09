<template>
  <div class="app-shell">
    <header class="topbar">
      <router-link class="brand" to="/client">好课</router-link>
      <nav class="nav-links">
        <router-link to="/client">课程</router-link>
        <router-link to="/client/my/courses">我的课程</router-link>
        <router-link to="/client/my/comments">我的评论</router-link>
        <router-link to="/client/coupons">优惠券</router-link>
        <router-link to="/client/ai-chat">AI客服</router-link>
        <router-link to="/client/orders">我的订单</router-link>
        <router-link to="/client">VIP</router-link>
        <template v-if="authStore.token">
          <span class="user-name">{{ authStore.nickname || authStore.phone }}</span>
          <button type="button" class="logout-button" @click="handleLogout">退出</button>
        </template>
        <router-link v-else to="/login">登录</router-link>
      </nav>
    </header>
    <main>
      <router-view />
    </main>
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

<style scoped>
.user-name {
  max-width: 120px;
  overflow: hidden;
  color: #1f2937;
  font-weight: 600;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.logout-button {
  padding: 0;
  border: 0;
  background: transparent;
  color: #6b7280;
  cursor: pointer;
  font: inherit;
}

.logout-button:hover {
  color: #2563eb;
}

@media (max-width: 820px) {
  .user-name {
    display: none;
  }
}
</style>
