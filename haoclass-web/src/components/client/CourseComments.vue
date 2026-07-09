<template>
  <section class="comments-panel">
    <div class="comments-heading">
      <div>
        <h2>本集讨论</h2>
        <p>{{ total }} 条已发布评论</p>
      </div>
      <el-button :loading="loading" @click="loadComments">刷新</el-button>
    </div>

    <div class="comment-composer">
      <el-input
        v-model="content"
        type="textarea"
        :rows="3"
        maxlength="1000"
        show-word-limit
        placeholder="说说你对本集内容的理解或疑问"
      />
      <div class="composer-actions">
        <span>评论将在审核通过后公开展示</span>
        <el-button type="primary" :loading="submitting" :disabled="!content.trim()" @click="submitComment">
          发表评论
        </el-button>
      </div>
    </div>

    <el-skeleton v-if="loading && comments.length === 0" :rows="6" animated />
    <el-empty v-else-if="comments.length === 0" description="还没有评论，来留下第一条讨论吧" />

    <div v-else class="comment-list">
      <article v-for="comment in comments" :key="comment.id" class="comment-item">
        <div class="comment-row">
          <el-avatar :size="38" :src="comment.avatarUrl">
            {{ avatarText(comment.nickname) }}
          </el-avatar>

          <div class="comment-main">
            <div class="comment-meta">
              <strong>{{ comment.nickname || '学员' }}</strong>
              <el-tag v-if="isMine(comment)" size="small" type="info">我</el-tag>
              <time>{{ comment.createTime }}</time>
            </div>
            <p class="comment-content">{{ comment.content }}</p>
            <div class="comment-actions">
              <button
                type="button"
                :class="{ active: comment.liked }"
                :disabled="isActing(comment.id)"
                @click="toggleLike(comment)"
              >
                {{ comment.liked ? '已赞' : '点赞' }} {{ comment.likeCount || 0 }}
              </button>
              <button type="button" @click="startReply(comment, comment.id)">回复</button>
              <button v-if="isMine(comment)" type="button" @click="removeComment(comment, comment.id)">
                删除
              </button>
            </div>
          </div>
        </div>

        <div v-if="replyTarget?.rootId === comment.id" class="reply-composer">
          <el-input
            v-model="replyContent"
            type="textarea"
            :rows="2"
            maxlength="1000"
            show-word-limit
            :placeholder="`回复 ${replyTarget.nickname || '学员'}`"
          />
          <div class="reply-composer-actions">
            <el-button @click="cancelReply">取消</el-button>
            <el-button
              type="primary"
              :loading="submittingReply"
              :disabled="!replyContent.trim()"
              @click="submitReply"
            >
              提交回复
            </el-button>
          </div>
        </div>

        <button
          v-if="comment.replyCount > 0"
          type="button"
          class="reply-toggle"
          @click="toggleReplies(comment)"
        >
          {{ expandedReplies[comment.id] ? '收起回复' : `查看 ${comment.replyCount} 条回复` }}
        </button>

        <div v-if="expandedReplies[comment.id]" class="reply-list">
          <el-skeleton v-if="replyLoading[comment.id]" :rows="3" animated />
          <div v-else v-for="reply in replies[comment.id] || []" :key="reply.id" class="reply-item">
            <el-avatar :size="30" :src="reply.avatarUrl">
              {{ avatarText(reply.nickname) }}
            </el-avatar>
            <div class="comment-main">
              <div class="comment-meta">
                <strong>{{ reply.nickname || '学员' }}</strong>
                <span v-if="reply.replyToNickname">回复 {{ reply.replyToNickname }}</span>
                <el-tag v-if="isMine(reply)" size="small" type="info">我</el-tag>
                <time>{{ reply.createTime }}</time>
              </div>
              <p class="comment-content">{{ reply.content }}</p>
              <div class="comment-actions">
                <button
                  type="button"
                  :class="{ active: reply.liked }"
                  :disabled="isActing(reply.id)"
                  @click="toggleLike(reply)"
                >
                  {{ reply.liked ? '已赞' : '点赞' }} {{ reply.likeCount || 0 }}
                </button>
                <button type="button" @click="startReply(reply, comment.id)">回复</button>
                <button v-if="isMine(reply)" type="button" @click="removeComment(reply, comment.id)">
                  删除
                </button>
              </div>
            </div>
          </div>
        </div>
      </article>
    </div>

    <el-pagination
      v-if="total > query.size"
      v-model:current-page="query.current"
      class="comment-pagination"
      :page-size="query.size"
      :total="total"
      layout="prev, pager, next"
      @current-change="loadComments"
    />
  </section>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createClientCourseComment,
  deleteClientCourseComment,
  likeClientCourseComment,
  listClientCourseComments,
  unlikeClientCourseComment
} from '@/api/comment'
import { useAuthStore } from '@/stores/auth'
import type { CourseComment } from '@/types/comment'

const props = defineProps<{
  courseId: string
  episodeId: string
}>()

const authStore = useAuthStore()
const comments = ref<CourseComment[]>([])
const total = ref(0)
const loading = ref(false)
const content = ref('')
const submitting = ref(false)
const replyTarget = ref<(CourseComment & { rootId: string }) | null>(null)
const replyContent = ref('')
const submittingReply = ref(false)
const replies = reactive<Record<string, CourseComment[]>>({})
const replyLoading = reactive<Record<string, boolean>>({})
const expandedReplies = reactive<Record<string, boolean>>({})
const actingIds = reactive<Record<string, boolean>>({})

const query = reactive({
  current: 1,
  size: 10
})

async function loadComments() {
  loading.value = true

  try {
    const page = await listClientCourseComments({
      current: query.current,
      size: query.size,
      courseId: props.courseId,
      episodeId: props.episodeId,
      rootId: '0'
    })
    comments.value = page.records
    total.value = page.total
    query.current = page.curPage || query.current
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '评论加载失败')
  } finally {
    loading.value = false
  }
}

async function submitComment() {
  const value = content.value.trim()
  if (!value) {
    return
  }

  submitting.value = true
  try {
    await createClientCourseComment({
      courseId: props.courseId,
      episodeId: props.episodeId,
      content: value
    })
    content.value = ''
    ElMessage.success('评论已提交，审核通过后将公开展示')
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '评论提交失败')
  } finally {
    submitting.value = false
  }
}

function startReply(comment: CourseComment, rootId: string) {
  replyTarget.value = {
    ...comment,
    rootId
  }
  replyContent.value = ''
}

function cancelReply() {
  replyTarget.value = null
  replyContent.value = ''
}

async function submitReply() {
  const target = replyTarget.value
  const value = replyContent.value.trim()
  if (!target || !value) {
    return
  }

  submittingReply.value = true
  try {
    await createClientCourseComment({
      courseId: props.courseId,
      episodeId: props.episodeId,
      parentId: target.id,
      content: value
    })
    cancelReply()
    ElMessage.success('回复已提交，审核通过后将公开展示')
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '回复提交失败')
  } finally {
    submittingReply.value = false
  }
}

async function toggleLike(comment: CourseComment) {
  actingIds[comment.id] = true
  try {
    if (comment.liked) {
      await unlikeClientCourseComment(comment.id)
      comment.liked = false
      comment.likeCount = Math.max(0, (comment.likeCount || 0) - 1)
    } else {
      await likeClientCourseComment(comment.id)
      comment.liked = true
      comment.likeCount = (comment.likeCount || 0) + 1
    }
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '点赞操作失败')
  } finally {
    delete actingIds[comment.id]
  }
}

async function toggleReplies(comment: CourseComment) {
  if (expandedReplies[comment.id]) {
    expandedReplies[comment.id] = false
    return
  }

  expandedReplies[comment.id] = true
  await loadReplies(comment.id)
}

async function loadReplies(rootId: string) {
  replyLoading[rootId] = true
  try {
    const page = await listClientCourseComments({
      current: 1,
      size: 100,
      courseId: props.courseId,
      episodeId: props.episodeId,
      rootId
    })
    replies[rootId] = page.records
  } catch (error) {
    console.error(error)
    expandedReplies[rootId] = false
    ElMessage.error(error instanceof Error ? error.message : '回复加载失败')
  } finally {
    replyLoading[rootId] = false
  }
}

async function removeComment(comment: CourseComment, rootId: string) {
  try {
    await ElMessageBox.confirm('确认删除这条评论吗？', '删除评论', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
    await deleteClientCourseComment(comment.id)
    ElMessage.success('评论已删除')

    if (comment.id === rootId) {
      await loadComments()
    } else {
      const rootComment = comments.value.find((item) => item.id === rootId)
      if (rootComment) {
        rootComment.replyCount = Math.max(0, rootComment.replyCount - 1)
      }
      await loadReplies(rootId)
    }
  } catch (error) {
    if (error === 'cancel') {
      return
    }

    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '删除评论失败')
  }
}

function isMine(comment: CourseComment) {
  return String(comment.userId) === String(authStore.userId)
}

function isActing(id: string) {
  return Boolean(actingIds[id])
}

function avatarText(nickname?: string) {
  return nickname?.trim().slice(0, 1) || '学'
}

watch(
  () => [props.courseId, props.episodeId],
  () => {
    query.current = 1
    comments.value = []
    Object.keys(replies).forEach((key) => delete replies[key])
    Object.keys(expandedReplies).forEach((key) => delete expandedReplies[key])
    cancelReply()
    loadComments()
  },
  { immediate: true }
)
</script>

<style scoped>
.comments-panel {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
}

.comments-heading,
.composer-actions,
.reply-composer-actions,
.comment-meta,
.comment-actions {
  display: flex;
  align-items: center;
}

.comments-heading {
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

.comments-heading h2 {
  margin: 0;
  font-size: 20px;
  color: #111827;
}

.comments-heading p {
  margin: 5px 0 0;
  color: #6b7280;
  font-size: 13px;
}

.comment-composer {
  padding: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #f8fafc;
}

.composer-actions {
  justify-content: space-between;
  gap: 12px;
  margin-top: 12px;
  color: #6b7280;
  font-size: 13px;
}

.comment-list {
  margin-top: 12px;
}

.comment-item {
  padding: 20px 0;
  border-bottom: 1px solid #eef2f7;
}

.comment-row,
.reply-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.comment-main {
  min-width: 0;
  flex: 1;
}

.comment-meta {
  min-height: 24px;
  flex-wrap: wrap;
  gap: 8px;
  color: #6b7280;
  font-size: 13px;
}

.comment-meta strong {
  color: #1f2937;
  font-size: 14px;
}

.comment-meta time {
  margin-left: auto;
}

.comment-content {
  margin: 8px 0 10px;
  color: #374151;
  line-height: 1.7;
  white-space: pre-wrap;
  overflow-wrap: anywhere;
}

.comment-actions {
  gap: 18px;
}

.comment-actions button,
.reply-toggle {
  padding: 0;
  border: 0;
  background: transparent;
  color: #6b7280;
  cursor: pointer;
}

.comment-actions button:hover,
.comment-actions button.active,
.reply-toggle:hover {
  color: #2563eb;
}

.comment-actions button:disabled {
  cursor: wait;
  opacity: 0.55;
}

.reply-composer {
  margin: 14px 0 0 50px;
  padding: 12px;
  border-left: 3px solid #dbeafe;
  background: #f8fafc;
}

.reply-composer-actions {
  justify-content: flex-end;
  gap: 8px;
  margin-top: 10px;
}

.reply-toggle {
  margin: 14px 0 0 50px;
  color: #2563eb;
}

.reply-list {
  display: grid;
  gap: 16px;
  margin: 16px 0 0 50px;
  padding: 14px;
  background: #f8fafc;
}

.comment-pagination {
  justify-content: center;
  margin-top: 22px;
}

@media (max-width: 640px) {
  .composer-actions {
    align-items: flex-end;
    flex-direction: column;
  }

  .comment-meta time {
    width: 100%;
    margin-left: 0;
  }

  .reply-composer,
  .reply-toggle,
  .reply-list {
    margin-left: 0;
  }
}
</style>
