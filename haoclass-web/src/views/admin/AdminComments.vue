<template>
  <section class="page-block">
    <div class="section-head">
      <h1>评论管理</h1>
      <el-button :loading="loading" @click="loadComments">刷新</el-button>
    </div>

    <el-form class="admin-filter" :inline="true" @submit.prevent>
      <el-form-item label="内容">
        <el-input v-model="query.content" clearable placeholder="搜索评论内容" style="width: 220px" />
      </el-form-item>
      <el-form-item label="课程ID">
        <el-input v-model="query.courseId" clearable placeholder="请输入课程ID" style="width: 180px" />
      </el-form-item>
      <el-form-item label="章节ID">
        <el-input v-model="query.episodeId" clearable placeholder="请输入章节ID" style="width: 180px" />
      </el-form-item>
      <el-form-item label="用户ID">
        <el-input v-model="query.userId" clearable placeholder="请输入用户ID" style="width: 180px" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" clearable placeholder="全部" style="width: 130px">
          <el-option label="待审核" :value="0" />
          <el-option label="已发布" :value="1" />
          <el-option label="已隐藏" :value="2" />
          <el-option label="已驳回" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间">
        <el-date-picker
          v-model="createTimeRange"
          type="datetimerange"
          value-format="YYYY-MM-DD HH:mm:ss"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          range-separator="至"
          style="width: 360px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="comments" style="width: 100%">
      <el-table-column prop="content" label="评论内容" min-width="280" show-overflow-tooltip />
      <el-table-column prop="userId" label="用户ID" min-width="150" show-overflow-tooltip />
      <el-table-column prop="courseId" label="课程ID" min-width="150" show-overflow-tooltip />
      <el-table-column prop="episodeId" label="章节ID" min-width="150" show-overflow-tooltip />
      <el-table-column label="类型" width="90">
        <template #default="{ row }">{{ String(row.rootId) === '0' ? '一级评论' : '回复' }}</template>
      </el-table-column>
      <el-table-column prop="likeCount" label="点赞数" width="90" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openDetail(row)">详情</el-button>
          <el-button v-if="row.status !== 1" size="small" type="success" @click="changeStatus(row, 'approve')">
            通过
          </el-button>
          <el-button v-if="row.status !== 3" size="small" type="warning" @click="changeStatus(row, 'reject')">
            驳回
          </el-button>
          <el-button v-if="row.status === 1" size="small" @click="changeStatus(row, 'hide')">隐藏</el-button>
          <el-button size="small" type="danger" @click="removeComment(row)">删除</el-button>
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
        @size-change="loadComments"
        @current-change="loadComments"
      />
    </div>

    <el-dialog v-model="detailVisible" title="评论详情" width="680px">
      <el-descriptions v-if="detail" :column="2" border>
        <el-descriptions-item label="评论ID" :span="2">{{ detail.id }}</el-descriptions-item>
        <el-descriptions-item label="课程ID">{{ detail.courseId }}</el-descriptions-item>
        <el-descriptions-item label="章节ID">{{ detail.episodeId }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ detail.userId }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ statusText(detail.status) }}</el-descriptions-item>
        <el-descriptions-item label="父评论ID">{{ detail.parentId }}</el-descriptions-item>
        <el-descriptions-item label="根评论ID">{{ detail.rootId }}</el-descriptions-item>
        <el-descriptions-item label="点赞数">{{ detail.likeCount }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="评论内容" :span="2">
          <div class="detail-content">{{ detail.content }}</div>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  approveAdminCourseComment,
  deleteAdminCourseComment,
  getAdminCourseCommentDetail,
  hideAdminCourseComment,
  listAdminCourseComments,
  rejectAdminCourseComment
} from '@/api/comment'
import type {
  AdminCourseCommentPageQuery,
  CourseComment,
  CourseCommentStatus
} from '@/types/comment'

type CommentQueryForm = Omit<
  AdminCourseCommentPageQuery,
  'courseId' | 'episodeId' | 'userId' | 'status' | 'createTimeStart' | 'createTimeEnd'
> & {
  courseId?: string
  episodeId?: string
  userId?: string
  status?: CourseCommentStatus | ''
}

const loading = ref(false)
const comments = ref<CourseComment[]>([])
const total = ref(0)
const detailVisible = ref(false)
const detail = ref<CourseComment | null>(null)
const createTimeRange = ref<[string, string] | []>([])

const query = reactive<CommentQueryForm>({
  current: 1,
  size: 10,
  courseId: '',
  episodeId: '',
  userId: '',
  status: '',
  content: ''
})

function buildQuery(): AdminCourseCommentPageQuery {
  const [createTimeStart, createTimeEnd] = createTimeRange.value
  return {
    current: query.current,
    size: query.size,
    courseId: query.courseId?.trim() || undefined,
    episodeId: query.episodeId?.trim() || undefined,
    userId: query.userId?.trim() || undefined,
    status: query.status === '' ? undefined : query.status,
    content: query.content?.trim() || undefined,
    createTimeStart,
    createTimeEnd
  }
}

async function loadComments() {
  loading.value = true
  try {
    const page = await listAdminCourseComments(buildQuery())
    comments.value = page.records
    total.value = page.total
    query.current = page.curPage || query.current
    query.size = page.size || query.size
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '评论列表加载失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.current = 1
  loadComments()
}

function handleReset() {
  query.current = 1
  query.size = 10
  query.courseId = ''
  query.episodeId = ''
  query.userId = ''
  query.status = ''
  query.content = ''
  createTimeRange.value = []
  loadComments()
}

async function openDetail(comment: CourseComment) {
  try {
    detail.value = await getAdminCourseCommentDetail(comment.id)
    detailVisible.value = true
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '评论详情加载失败')
  }
}

async function changeStatus(comment: CourseComment, action: 'approve' | 'reject' | 'hide') {
  const actionText = action === 'approve' ? '通过' : action === 'reject' ? '驳回' : '隐藏'
  try {
    await ElMessageBox.confirm(`确认${actionText}这条评论吗？`, `${actionText}评论`, {
      type: action === 'approve' ? 'success' : 'warning',
      confirmButtonText: actionText,
      cancelButtonText: '取消'
    })

    if (action === 'approve') {
      await approveAdminCourseComment(comment.id)
    } else if (action === 'reject') {
      await rejectAdminCourseComment(comment.id)
    } else {
      await hideAdminCourseComment(comment.id)
    }

    ElMessage.success(`评论已${actionText}`)
    await loadComments()
  } catch (error) {
    if (error === 'cancel') {
      return
    }
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : `${actionText}评论失败`)
  }
}

async function removeComment(comment: CourseComment) {
  try {
    await ElMessageBox.confirm('删除后客户端将无法看到该评论，确认删除吗？', '删除评论', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
    await deleteAdminCourseComment(comment.id)
    ElMessage.success('评论已删除')
    await loadComments()
  } catch (error) {
    if (error === 'cancel') {
      return
    }
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '删除评论失败')
  }
}

function statusText(status: CourseCommentStatus) {
  return {
    0: '待审核',
    1: '已发布',
    2: '已隐藏',
    3: '已驳回'
  }[status]
}

function statusTagType(status: CourseCommentStatus) {
  if (status === 1) {
    return 'success'
  }
  if (status === 0) {
    return 'warning'
  }
  if (status === 3) {
    return 'danger'
  }
  return 'info'
}

onMounted(loadComments)
</script>

<style scoped>
.detail-content {
  line-height: 1.7;
  white-space: pre-wrap;
  overflow-wrap: anywhere;
}
</style>
