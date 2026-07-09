<template>
  <div class="login-page">
    <section class="login-panel">
      <h1>{{ loginMode === 'admin' ? '后台登录' : '客户端登录' }}</h1>
      <el-segmented
        v-model="loginMode"
        class="login-mode"
        :options="[
          { label: '客户端', value: 'client' },
          { label: '后台', value: 'admin' }
        ]"
      />
      <el-form label-position="top" @submit.prevent>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-button class="full-button" type="primary" :loading="loading" @click="handleLogin">登录</el-button>
      </el-form>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { adminLogin, clientLogin } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const loading = ref(false)

const form = reactive({
  phone: '',
  password: ''
})
const loginMode = ref<'client' | 'admin'>(
  String(route.query.redirect || '').startsWith('/admin') ? 'admin' : 'client'
)

const defaultRedirect = computed(() => (loginMode.value === 'admin' ? '/admin' : '/client'))

async function handleLogin() {
  if (!form.phone.trim() || !form.password.trim()) {
    ElMessage.warning('请输入手机号和密码')
    return
  }

  loading.value = true

  try {
    const loginResult = await (loginMode.value === 'admin' ? adminLogin : clientLogin)({
      phone: form.phone.trim(),
      password: form.password
    })

    authStore.login({
      token: loginResult.accessToken,
      userId: loginResult.userId,
      phone: loginResult.phone,
      nickname: loginResult.nickname || loginResult.phone,
      role: String(loginResult.role)
    })

    ElMessage.success('登录成功')
    router.push(String(route.query.redirect || defaultRedirect.value))
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-panel h1 {
  margin: 0 0 18px;
}

.login-mode {
  width: 100%;
  margin-bottom: 20px;
}
</style>
