<template>
  <section class="episode-section my-comments-page">
    <div class="section-head">
      <div>
        <h1>我的评论</h1>
        <p>查看自己发表的评论及审核状态</p>
      </div>
      <el-button :loading="loading" @click="loadComments">刷新</el-button>
    </div>

    <el-form class="comment-filter" :inline="true" @submit.prevent>
      <el-form-item label="评论内容">
        <el-input
          v-model="query.content"
          clearable
          placeholder="搜索评论内容"
          style="width: 220px"
          @keyup.enter="handleSearch"
        />
      </el-form-item>
      <el-form-item label="审核状态">
        <el-select v-model="query.status" clearable placeholder="全部" style="width: 130px">
          <el-option label="待审核" :value="0" />
          <el-option label="已发布" :value="1" />
          <el-option label="已隐藏" :value="2" />
          <el-option label="已驳回" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="发表时间">
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

    <el-skeleton v-if="loading && comments.length === 0" :rows="8" animated />

    <el-empty v-else-if="comments.length === 0" description="暂无符合条件的评论">
      <router-link to="/client/my/courses">
        <el-button type="primary">去学习并参与讨论</el-button>
      </router-link>
    </el-empty>

    <div v-else v-loading="loading" class="my-comment-list">
      <article v-for="comment in comments" :key="comment.id" class="my-comment-item">
        <div class="comment-context">
          <button type="button" class="course-link" @click="goToEpisode(comment)">
            <strong>{{ comment.courseTitle || '课程已不可见' }}</strong>
            <span>{{ comment.episodeTitle || '章节已不可见' }}</span>
          </button>
          <el-tag :type="statusTagType(comment.status)">
            {{ statusText(comment.status) }}
          </el-tag>
        </div>

        <p class="comment-content">{{ comment.content }}</p>

        <div class="comment-footer">
          <div class="comment-meta">
            <span>点赞 {{ comment.likeCount || 0 }}</span>
            <time>{{ comment.createTime }}</time>
          </div>
          <div class="comment-actions">
            <el-button size="small" @click="goToEpisode(comment)">查看章节</el-button>
            <el-button
              size="small"
              type="danger"
              :loading="deletingId === comment.id"
              @click="removeComment(comment)"
            >
              删除
            </el-button>
          </div>
        </div>
      </article>
    </div>

    <div v-if="total > 0" class="comment-pagination">
      <el-pagination
        v-model:current-page="query.current"
        v-model:page-size="query.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handlePageSizeChange"
        @current-change="loadComments"
      />
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteClientCourseComment, listClientMyComments } from '@/api/comment'
import type {
  CourseCommentStatus,
  MyCourseComment,
  MyCourseCommentPageQuery
} from '@/types/comment'

type MyCommentQueryForm = Omit<
  MyCourseCommentPageQuery,
  'status' | 'createTimeStart' | 'createTimeEnd'
> & {
  status?: CourseCommentStatus | ''
}

const router = useRouter()
const loading = ref(false)
const deletingId = ref('')
const comments = ref<MyCourseComment[]>([])
const total = ref(0)
const createTimeRange = ref<[string, string] | []>([])

const query = reactive<MyCommentQueryForm>({
  current: 1,
  size: 10,
  status: '',
  content: ''
})

function buildQuery(): MyCourseCommentPageQuery {
  const [createTimeStart, createTimeEnd] = createTimeRange.value
  return {
    current: query.current,
    size: query.size,
    status: query.status === '' ? undefined : query.status,
    content: query.content?.trim() || undefined,
    createTimeStart,
    createTimeEnd
  }
}

async function loadComments() {
  loading.value = true
  try {
    const page = await listClientMyComments(buildQuery())
    comments.value = page.records
    total.value = page.total
    query.current = page.curPage || query.current
    query.size = page.size || query.size
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '我的评论加载失败')
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
  query.status = ''
  query.content = ''
  createTimeRange.value = []
  loadComments()
}

function handlePageSizeChange() {
  query.current = 1
  loadComments()
}

function goToEpisode(comment: MyCourseComment) {
  router.push({
    path: `/client/courses/${comment.courseId}/watch`,
    query: { episodeId: comment.episodeId }
  })
}

async function removeComment(comment: MyCourseComment) {
  try {
    await ElMessageBox.confirm('删除后无法恢复，确认删除这条评论吗？', '删除评论', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })

    deletingId.value = comment.id
    await deleteClientCourseComment(comment.id)
    ElMessage.success('评论已删除')

    if (comments.value.length === 1 && query.current > 1) {
      query.current -= 1
    }
    await loadComments()
  } catch (error) {
    if (error === 'cancel') {
      return
    }

    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '删除评论失败')
  } finally {
    deletingId.value = ''
  }
}

function statusText(status: CourseCommentStatus) {
  const statusMap: Record<CourseCommentStatus, string> = {
    0: '待审核',
    1: '已发布',
    2: '已隐藏',
    3: '已驳回'
  }
  return statusMap[status] || '未知状态'
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
.my-comments-page .section-head h1 {
  margin: 0 0 6px;
}

.my-comments-page .section-head p {
  margin: 0;
  color: #6b7280;
}

.comment-filter {
  margin-bottom: 8px;
  padding: 14px 16px 0;
  border: 1px solid #e5e7eb;
  background: #f8fafc;
}

.my-comment-list {
  display: grid;
}

.my-comment-item {
  padding: 20px 0;
  border-bottom: 1px solid #e5e7eb;
}

.comment-context,
.comment-footer,
.comment-meta,
.comment-actions {
  display: flex;
  align-items: center;
}

.comment-context,
.comment-footer {
  justify-content: space-between;
  gap: 16px;
}

.course-link {
  min-width: 0;
  padding: 0;
  border: 0;
  background: transparent;
  cursor: pointer;
  text-align: left;
}

.course-link strong,
.course-link span {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-link strong {
  color: #1f2937;
  font-size: 15px;
}

.course-link span {
  margin-top: 5px;
  color: #6b7280;
  font-size: 13px;
}

.course-link:hover strong {
  color: #2563eb;
}

.comment-content {
  margin: 16px 0;
  color: #374151;
  line-height: 1.8;
  white-space: pre-wrap;
  overflow-wrap: anywhere;
}

.comment-meta,
.comment-actions {
  gap: 14px;
}

.comment-meta {
  color: #6b7280;
  font-size: 13px;
}

.comment-pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

@media (max-width: 720px) {
  .comment-filter :deep(.el-form-item),
  .comment-filter :deep(.el-input),
  .comment-filter :deep(.el-select),
  .comment-filter :deep(.el-date-editor) {
    width: 100% !important;
  }

  .comment-footer {
    align-items: flex-start;
    flex-direction: column;
  }

  .comment-pagination {
    justify-content: center;
    overflow-x: auto;
  }
}
</style>
