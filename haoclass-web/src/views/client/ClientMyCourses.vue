<template>
  <section class="episode-section my-courses-page">
    <div class="section-head">
      <div>
        <h1>我的课程</h1>
        <p>查看已拥有的课程和最近学习进度</p>
      </div>
      <el-button :loading="loading" @click="loadMyCourses">刷新</el-button>
    </div>

    <el-skeleton v-if="loading" :rows="8" animated />

    <el-empty v-else-if="courses.length === 0" description="暂无已拥有课程">
      <router-link to="/client">
        <el-button type="primary">去选课程</el-button>
      </router-link>
    </el-empty>

    <div v-else class="my-course-list">
      <article v-for="course in courses" :key="course.courseId" class="my-course-item">
        <img :src="resolveCourseCover(course)" :alt="course.title" @error="useFallbackCourseCover" />

        <div class="my-course-main">
          <div class="my-course-title">
            <h2>{{ course.title }}</h2>
            <el-tag :type="course.learningPercent >= 100 ? 'success' : 'info'">
              {{ course.learningPercent || 0 }}%
            </el-tag>
          </div>

          <p class="teacher">讲师：{{ course.teacherName || '-' }}</p>

          <el-progress
            :percentage="safePercent(course.learningPercent)"
            :stroke-width="10"
            :show-text="false"
          />

          <div class="progress-meta">
            <span>已完成 {{ course.finishedEpisodeCount || 0 }} / {{ course.episodeCount || 0 }} 集</span>
            <span>最近学习：{{ course.lastLearnTime || '暂未开始' }}</span>
          </div>

          <p class="last-episode">
            {{ course.lastEpisodeTitle ? `上次学到：${course.lastEpisodeTitle}` : '还没有学习记录' }}
          </p>
        </div>

        <div class="my-course-actions">
          <el-button type="primary" @click="goStudy(course)">
            {{ course.lastEpisodeId ? '继续学习' : '开始学习' }}
          </el-button>
          <el-button @click="goDetail(course)">课程详情</el-button>
        </div>
      </article>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { listClientMyCourses } from '@/api/course'
import { resolveCourseCover, useFallbackCourseCover } from '@/utils/courseCover'
import type { ClientMyCourse } from '@/types/course'

const router = useRouter()
const loading = ref(false)
const courses = ref<ClientMyCourse[]>([])

async function loadMyCourses() {
  loading.value = true

  try {
    courses.value = await listClientMyCourses()
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '我的课程加载失败')
  } finally {
    loading.value = false
  }
}

function safePercent(percent?: number) {
  return Math.min(100, Math.max(0, percent || 0))
}

function goStudy(course: ClientMyCourse) {
  const path = `/client/courses/${course.courseId}/watch`
  if (course.lastEpisodeId) {
    router.push({ path, query: { episodeId: course.lastEpisodeId } })
    return
  }

  router.push(path)
}

function goDetail(course: ClientMyCourse) {
  router.push(`/client/courses/${course.courseId}`)
}

onMounted(loadMyCourses)
</script>

<style scoped>
.my-courses-page .section-head h1 {
  margin: 0 0 6px;
}

.my-courses-page .section-head p {
  margin: 0;
  color: #6b7280;
}

.my-course-list {
  display: grid;
  gap: 16px;
}

.my-course-item {
  display: grid;
  grid-template-columns: 180px minmax(0, 1fr) 150px;
  gap: 18px;
  align-items: center;
  padding: 16px;
  border: 1px solid #e8ebf2;
  border-radius: 8px;
  background: #fff;
}

.my-course-item img {
  width: 180px;
  aspect-ratio: 16 / 10;
  object-fit: cover;
  border-radius: 6px;
}

.my-course-main {
  min-width: 0;
}

.my-course-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 8px;
}

.my-course-title h2 {
  margin: 0;
  font-size: 20px;
  line-height: 1.35;
  color: #111827;
}

.teacher,
.last-episode,
.progress-meta {
  color: #6b7280;
}

.teacher {
  margin: 0 0 12px;
}

.progress-meta {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 10px;
  font-size: 13px;
}

.last-episode {
  margin: 10px 0 0;
}

.my-course-actions {
  display: grid;
  gap: 10px;
}

@media (max-width: 820px) {
  .my-course-item {
    grid-template-columns: 1fr;
  }

  .my-course-item img {
    width: 100%;
  }

  .progress-meta {
    flex-direction: column;
  }
}
</style>
