<template>
  <section class="page-block">
    <div class="section-head">
      <h1>用户管理</h1>
      <el-button type="primary" @click="openCreateDialog">新增用户</el-button>
    </div>

    <el-form class="admin-filter" :inline="true" @submit.prevent>
      <el-form-item label="手机号">
        <el-input v-model="query.phone" clearable placeholder="请输入手机号" />
      </el-form-item>
      <el-form-item label="昵称">
        <el-input v-model="query.nickname" clearable placeholder="请输入昵称" />
      </el-form-item>
      <el-form-item label="角色">
        <el-select v-model="query.role" clearable placeholder="全部" style="width: 120px">
          <el-option label="普通用户" :value="0" />
          <el-option label="管理员" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" clearable placeholder="全部" style="width: 120px">
          <el-option label="禁用" :value="0" />
          <el-option label="正常" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="users" style="width: 100%">
      <el-table-column prop="nickname" label="昵称" min-width="140" />
      <el-table-column prop="phone" label="手机号" width="150" />
      <el-table-column label="角色" width="110">
        <template #default="{ row }">{{ roleText(row.role) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="vipExpireTime" label="VIP到期时间" width="180" />
      <el-table-column prop="lastLoginTime" label="最后登录时间" width="180" />
      <el-table-column label="操作" width="340" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" @click="openPasswordDialog(row)">重置密码</el-button>
          <el-button
            v-if="row.status === 1"
            size="small"
            type="warning"
            @click="handleDisable(row)"
          >
            禁用
          </el-button>
          <el-button v-else size="small" type="success" @click="handleEnable(row)">启用</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="admin-pagination">
      <el-pagination
        v-model:current-page="query.current"
        v-model:page-size="query.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadUsers"
        @current-change="loadUsers"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑用户' : '新增用户'" width="560px">
      <el-form :model="form" label-width="100px">
        <el-form-item v-if="!editingId" label="手机号" required>
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="昵称" required>
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="头像">
          <el-input v-model="form.avatarUrl" placeholder="请输入头像URL" />
        </el-form-item>
        <el-form-item v-if="!editingId" label="初始密码" required>
          <el-input v-model="form.password" type="password" placeholder="请输入初始密码" show-password />
        </el-form-item>
        <div class="form-grid">
          <el-form-item label="角色" required>
            <el-select v-model="form.role" style="width: 100%">
              <el-option label="普通用户" :value="0" />
              <el-option label="管理员" :value="1" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="form.status" style="width: 100%">
              <el-option label="禁用" :value="0" />
              <el-option label="正常" :value="1" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item label="VIP到期">
          <el-date-picker
            v-model="form.vipExpireTime"
            type="datetime"
            value-format="YYYY-MM-DDTHH:mm:ss"
            placeholder="请选择到期时间"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="passwordDialogVisible" title="重置密码" width="420px">
      <el-form label-width="90px">
        <el-form-item label="新密码" required>
          <el-input v-model="passwordForm.password" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleResetPassword">确认重置</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createAdminUser,
  deleteAdminUser,
  disableAdminUser,
  enableAdminUser,
  getAdminUserDetail,
  listAdminUsers,
  resetAdminUserPassword,
  updateAdminUser
} from '@/api/user'
import type { User, UserCreatePayload, UserPageQuery, UserRole, UserStatus } from '@/types/user'

type UserQueryForm = Omit<UserPageQuery, 'role' | 'status'> & {
  role?: UserRole | ''
  status?: UserStatus | ''
}

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const passwordDialogVisible = ref(false)
const editingId = ref('')
const passwordUserId = ref('')
const users = ref<User[]>([])
const total = ref(0)

const query = reactive<UserQueryForm>({
  current: 1,
  size: 10,
  phone: '',
  nickname: '',
  role: '',
  status: ''
})

const form = reactive<UserCreatePayload>({
  phone: '',
  nickname: '',
  avatarUrl: '',
  password: '',
  role: 0,
  status: 1,
  vipExpireTime: ''
})

const passwordForm = reactive({
  password: ''
})

function buildQuery(): UserPageQuery {
  return {
    current: query.current,
    size: query.size,
    phone: query.phone?.trim() || undefined,
    nickname: query.nickname?.trim() || undefined,
    role: query.role === '' ? undefined : query.role,
    status: query.status === '' ? undefined : query.status
  }
}

async function loadUsers() {
  loading.value = true

  try {
    const page = await listAdminUsers(buildQuery())
    users.value = page.records
    total.value = page.total
    query.current = page.curPage || query.current
    query.size = page.size || query.size
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '用户列表加载失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.current = 1
  loadUsers()
}

function handleReset() {
  query.current = 1
  query.size = 10
  query.phone = ''
  query.nickname = ''
  query.role = ''
  query.status = ''
  loadUsers()
}

function resetForm() {
  form.phone = ''
  form.nickname = ''
  form.avatarUrl = ''
  form.password = ''
  form.role = 0
  form.status = 1
  form.vipExpireTime = ''
}

function openCreateDialog() {
  editingId.value = ''
  resetForm()
  dialogVisible.value = true
}

async function openEditDialog(user: User) {
  loading.value = true

  try {
    const detail = await getAdminUserDetail(user.id)
    editingId.value = detail.id
    form.phone = detail.phone
    form.nickname = detail.nickname
    form.avatarUrl = detail.avatarUrl || ''
    form.password = ''
    form.role = detail.role
    form.status = detail.status
    form.vipExpireTime = detail.vipExpireTime || ''
    dialogVisible.value = true
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '用户详情加载失败')
  } finally {
    loading.value = false
  }
}

function openPasswordDialog(user: User) {
  passwordUserId.value = user.id
  passwordForm.password = ''
  passwordDialogVisible.value = true
}

async function handleSubmit() {
  if (!form.nickname.trim()) {
    ElMessage.warning('请输入昵称')
    return
  }

  if (!editingId.value && (!form.phone.trim() || !form.password.trim())) {
    ElMessage.warning('请输入手机号和初始密码')
    return
  }

  saving.value = true

  try {
    if (editingId.value) {
      await updateAdminUser(editingId.value, {
        nickname: form.nickname,
        avatarUrl: form.avatarUrl,
        role: form.role,
        status: form.status,
        vipExpireTime: form.vipExpireTime || undefined
      })
      ElMessage.success('用户已更新')
    } else {
      await createAdminUser({
        phone: form.phone,
        nickname: form.nickname,
        avatarUrl: form.avatarUrl,
        password: form.password,
        role: form.role,
        status: form.status,
        vipExpireTime: form.vipExpireTime || undefined
      })
      ElMessage.success('用户已新增')
    }

    dialogVisible.value = false
    loadUsers()
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  } finally {
    saving.value = false
  }
}

async function handleResetPassword() {
  if (!passwordForm.password.trim()) {
    ElMessage.warning('请输入新密码')
    return
  }

  saving.value = true

  try {
    await resetAdminUserPassword(passwordUserId.value, { password: passwordForm.password })
    ElMessage.success('密码已重置')
    passwordDialogVisible.value = false
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '密码重置失败')
  } finally {
    saving.value = false
  }
}

async function handleDelete(user: User) {
  await ElMessageBox.confirm(`确认删除用户“${user.nickname || user.phone}”吗？`, '删除用户', {
    type: 'warning'
  })

  try {
    await deleteAdminUser(user.id)
    ElMessage.success('用户已删除')
    loadUsers()
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '删除失败')
  }
}

async function handleEnable(user: User) {
  try {
    await enableAdminUser(user.id)
    ElMessage.success('用户已启用')
    loadUsers()
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '启用失败')
  }
}

async function handleDisable(user: User) {
  try {
    await disableAdminUser(user.id)
    ElMessage.success('用户已禁用')
    loadUsers()
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '禁用失败')
  }
}

function roleText(role: UserRole) {
  return role === 1 ? '管理员' : '普通用户'
}

function statusText(status: UserStatus) {
  return status === 1 ? '正常' : '禁用'
}

onMounted(loadUsers)
</script>
