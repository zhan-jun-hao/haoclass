<template>
  <section v-if="loading" class="episode-section">
    <el-skeleton :rows="8" animated />
  </section>

  <section v-else-if="errorMessage" class="episode-section">
    <el-alert :title="errorMessage" type="error" show-icon :closable="false" />
    <el-button class="retry-button" type="primary" @click="loadCourse">重新加载</el-button>
  </section>

  <section v-else-if="!course" class="episode-section">
    <el-empty description="课程不存在" />
  </section>

  <section v-else class="watch-page">
    <div class="video-panel">
      <div class="player-shell">
        <video
          v-if="activePlay?.videoUrl"
          ref="videoRef"
          :key="activePlay.episodeId"
          class="course-video"
          :src="activePlay.videoUrl"
          controls
          preload="metadata"
          @loadedmetadata="handleLoadedMetadata"
          @timeupdate="handleTimeUpdate"
          @pause="reportCurrentProgress(true)"
          @ended="reportCurrentProgress(true)"
          @error="handleVideoError"
        />
        <div v-else class="video-placeholder">
          {{ activeEpisode ? '正在加载视频' : '暂无可播放章节' }}
        </div>
      </div>

      <h1>{{ course.title }}</h1>
      <p v-if="activeEpisode">
        第 {{ activeEpisode.sort || 1 }} 集 / 共 {{ course.episodeCount }} 集 ·
        {{ activePlay?.episodeTitle || activeEpisode.title }}
      </p>
      <p v-else>该课程暂时没有已上架集数</p>

      <CourseComments
        v-if="activeEpisode"
        :key="String(activeEpisode.id)"
        :course-id="String(course.id)"
        :episode-id="String(activeEpisode.id)"
      />
    </div>

    <aside class="episode-list">
      <h2>选集</h2>
      <button
        v-for="episode in course.episodes || []"
        :key="episode.id"
        :class="{ active: String(episode.id) === String(activeEpisode?.id) }"
        @click="selectEpisode(episode)"
      >
        <span class="episode-title">{{ episode.title }}</span>
        <span class="episode-side">
          <el-tag size="small" :type="episodeStatusType(episode)">
            {{ episodeStatusText(episode) }}
          </el-tag>
          <small>{{ formatDuration(episode.durationSeconds) }}</small>
        </span>
      </button>
    </aside>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import CourseComments from '@/components/client/CourseComments.vue'
import { getClientCoursePlay, getCourseDetail } from '@/api/course'
import {
  getEpisodeLearningProgress,
  listCourseLearningProgress,
  reportLearningProgress,
  type CourseLearningProgress
} from '@/api/learningProgress'
import type { Course, CourseEpisode, CoursePlay } from '@/types/course'

const route = useRoute()
const course = ref<Course | null>(null)
const activeEpisode = ref<CourseEpisode | null>(null)
const activePlay = ref<CoursePlay | null>(null)
const videoRef = ref<HTMLVideoElement | null>(null)
const loading = ref(false)
const errorMessage = ref('')
const resumeSeconds = ref(0)
const progressMap = ref<Record<string, CourseLearningProgress>>({})
let lastReportedSeconds = 0
let reporting = false

async function loadCourse() {
  loading.value = true
  errorMessage.value = ''
  activePlay.value = null

  try {
    const detail = await getCourseDetail(String(route.params.id))
    course.value = detail
    await loadCourseProgress(String(route.params.id))

    const queryEpisodeId = route.query.episodeId ? String(route.query.episodeId) : ''
    const targetEpisode = detail.episodes?.find((episode) => String(episode.id) === queryEpisodeId)
    const firstEpisode = targetEpisode || detail.episodes?.[0] || null

    if (firstEpisode) {
      await selectEpisode(firstEpisode)
    } else {
      activeEpisode.value = null
    }
  } catch (error) {
    console.error(error)
    course.value = null
    activeEpisode.value = null
    activePlay.value = null
    errorMessage.value = '课程播放页加载失败，请确认课程和集数已上架'
    ElMessage.error(errorMessage.value)
  } finally {
    loading.value = false
  }
}

async function selectEpisode(episode: CourseEpisode) {
  activeEpisode.value = episode
  activePlay.value = null
  resumeSeconds.value = 0
  lastReportedSeconds = 0

  try {
    const courseId = String(route.params.id)
    const episodeId = String(episode.id)
    const [play, progress] = await Promise.all([
      getClientCoursePlay(courseId, episodeId),
      getEpisodeLearningProgress(courseId, episodeId)
    ])

    activePlay.value = play
    resumeSeconds.value = progress.progressSeconds ?? 0
    lastReportedSeconds = resumeSeconds.value
    setEpisodeProgress(progress)
  } catch (error) {
    console.error(error)
    ElMessage.error('视频加载失败，请确认你有该课程的观看权限')
  }
}

async function loadCourseProgress(courseId: string) {
  const progressList = await listCourseLearningProgress(courseId)
  progressMap.value = progressList.reduce<Record<string, CourseLearningProgress>>((map, progress) => {
    map[String(progress.episodeId)] = progress
    return map
  }, {})
}

function setEpisodeProgress(progress: CourseLearningProgress) {
  progressMap.value = {
    ...progressMap.value,
    [String(progress.episodeId)]: progress
  }
}

function handleLoadedMetadata() {
  const video = videoRef.value
  if (!video || resumeSeconds.value <= 0) {
    return
  }

  const safeSeconds = Math.min(resumeSeconds.value, Math.floor(video.duration || resumeSeconds.value))
  video.currentTime = safeSeconds
}

function handleTimeUpdate() {
  reportCurrentProgress(false)
}

async function reportCurrentProgress(force: boolean) {
  const video = videoRef.value
  const play = activePlay.value
  if (!video || !play || reporting) {
    return
  }

  const currentSeconds = Math.floor(video.ended ? video.duration : video.currentTime)
  if (!force && Math.abs(currentSeconds - lastReportedSeconds) < 10) {
    return
  }

  reporting = true
  try {
    await reportLearningProgress({
      courseId: play.courseId,
      episodeId: play.episodeId,
      progressSeconds: currentSeconds
    })
    lastReportedSeconds = currentSeconds
    setEpisodeProgress({
      courseId: play.courseId,
      episodeId: play.episodeId,
      progressSeconds: currentSeconds,
      finished: isFinished(currentSeconds, play.durationSeconds)
    })
  } catch (error) {
    console.error(error)
  } finally {
    reporting = false
  }
}

function getEpisodeProgress(episode: CourseEpisode) {
  return progressMap.value[String(episode.id)]
}

function episodeStatusText(episode: CourseEpisode) {
  const progress = getEpisodeProgress(episode)
  if (!progress || !progress.progressSeconds) {
    return '未开始'
  }

  if (progress.finished === 1) {
    return '已学完'
  }

  return `学习中 ${formatDuration(progress.progressSeconds)}`
}

function episodeStatusType(episode: CourseEpisode) {
  const progress = getEpisodeProgress(episode)
  if (progress?.finished === 1) {
    return 'success'
  }

  if ((progress?.progressSeconds || 0) > 0) {
    return 'warning'
  }

  return 'info'
}

function isFinished(progressSeconds: number, durationSeconds?: number) {
  if (!durationSeconds || durationSeconds <= 0) {
    return 0
  }

  return durationSeconds - progressSeconds <= 5 ? 1 : 0
}

function handleVideoError() {
  ElMessage.error('视频文件无法播放，请检查 videoUrl 是否正确')
}

function formatDuration(seconds?: number) {
  const safeSeconds = seconds ?? 0
  const minutes = Math.floor(safeSeconds / 60)
  const remainSeconds = safeSeconds % 60
  return `${String(minutes).padStart(2, '0')}:${String(remainSeconds).padStart(2, '0')}`
}

onMounted(loadCourse)

watch(() => route.params.id, loadCourse)
</script>

<style scoped>
.episode-section {
  padding: 32px;
}

.retry-button {
  margin-top: 16px;
}

.watch-page {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 320px;
  gap: 24px;
  padding: 24px;
}

.video-panel {
  min-width: 0;
}

.player-shell {
  width: 100%;
  aspect-ratio: 16 / 9;
  background: #111827;
  overflow: hidden;
}

.course-video {
  width: 100%;
  height: 100%;
  display: block;
  background: #111827;
}

.video-placeholder {
  height: 100%;
  display: grid;
  place-items: center;
  color: #d1d5db;
  font-size: 15px;
}

.video-panel h1 {
  margin: 18px 0 8px;
  font-size: 24px;
  line-height: 1.3;
  color: #111827;
}

.video-panel p {
  margin: 0;
  color: #6b7280;
}

.episode-list {
  border-left: 1px solid #e5e7eb;
  padding-left: 20px;
}

.episode-list h2 {
  margin: 0 0 14px;
  font-size: 18px;
  color: #111827;
}

.episode-list button {
  width: 100%;
  min-height: 58px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  border: 0;
  border-bottom: 1px solid #eef2f7;
  background: transparent;
  color: #374151;
  cursor: pointer;
  text-align: left;
}

.episode-list button.active {
  color: #2563eb;
  font-weight: 600;
}

.episode-list small {
  flex: 0 0 auto;
  color: #9ca3af;
}

.episode-title {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.episode-side {
  display: inline-flex;
  flex: 0 0 auto;
  align-items: center;
  gap: 8px;
}

@media (max-width: 900px) {
  .watch-page {
    grid-template-columns: 1fr;
  }

  .episode-list {
    border-left: 0;
    padding-left: 0;
  }
}
</style>
