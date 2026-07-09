<template>
  <section class="client-home">
    <div class="hero">
      <div>
        <p class="eyebrow">课程平台</p>
        <h1>覆盖课程选购、在线学习与进度追踪的一体化学习平台</h1>
        <p>围绕课程展示、订单支付、权益开通、播放学习和互动评价构建完整闭环，支撑后续优惠券、会员权益与运营管理持续扩展。</p>
      </div>
    </div>

    <div class="course-search-panel">
      <el-form class="course-search-form" :inline="true" @submit.prevent>
        <el-form-item label="关键词">
          <el-input
            v-model="query.keyword"
            clearable
            placeholder="搜索课程、讲师、摘要"
            style="width: 260px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-input
            v-model="query.categoryName"
            clearable
            placeholder="例如 JavaSE、Redis"
            style="width: 180px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="收费">
          <el-select v-model="query.chargeType" clearable placeholder="全部" style="width: 140px">
            <el-option label="免费" :value="0" />
            <el-option label="付费" :value="1" />
            <el-option label="VIP免费" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-select v-model="query.sortType" style="width: 140px">
            <el-option label="综合" :value="0" />
            <el-option label="最新" :value="1" />
            <el-option label="销量" :value="2" />
            <el-option label="价格升序" :value="3" />
            <el-option label="价格降序" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="section-head">
      <div>
        <h2>课程列表</h2>
        <p class="section-tip">当前通过 Elasticsearch 搜索已上架课程，共 {{ total }} 条</p>
      </div>
    </div>

    <el-empty v-if="!loading && courses.length === 0" description="暂无匹配课程" />

    <div v-else v-loading="loading" class="course-grid">
      <article v-for="course in courses" :key="course.id" class="course-card">
        <img :src="resolveCourseCover(course)" :alt="course.title" @error="useFallbackCourseCover" />
        <div class="course-body">
          <div class="course-card-head">
            <span class="tag">{{ course.categoryName }}</span>
            <span class="charge-tag">{{ chargeText(course.chargeType) }}</span>
          </div>
          <h3 v-html="displayTitle(course)"></h3>
          <p class="course-subtitle" v-html="displaySubtitle(course)"></p>
          <p v-html="displaySummary(course)"></p>
          <div class="course-meta">
            <strong>{{ formatPriceText(course.price, course.chargeType) }}</strong>
            <span>共 {{ course.episodeCount }} 集 · {{ course.buyCount }} 人学习</span>
          </div>
          <router-link :to="`/client/courses/${course.id}`">
            <el-button type="primary">查看详情</el-button>
          </router-link>
        </div>
      </article>
    </div>

    <div class="client-pagination">
      <el-pagination
        v-model:current-page="query.current"
        v-model:page-size="query.size"
        :total="total"
        :page-sizes="[6, 12, 24, 48]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadCourses"
        @current-change="loadCourses"
      />
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { searchClientCourses } from '@/api/course'
import { resolveCourseCover, useFallbackCourseCover } from '@/utils/courseCover'
import type { ChargeType, CourseSearchQuery, CourseSearchResult } from '@/types/course'

type ClientCourseSearchForm = Omit<CourseSearchQuery, 'chargeType'> & {
  chargeType?: ChargeType | ''
}

const loading = ref(false)
const courses = ref<CourseSearchResult[]>([])
const total = ref(0)

const query = reactive<ClientCourseSearchForm>({
  current: 1,
  size: 12,
  keyword: '',
  categoryName: '',
  chargeType: '',
  sortType: 0
})

function buildQuery(): CourseSearchQuery {
  return {
    current: query.current,
    size: query.size,
    keyword: query.keyword?.trim() || undefined,
    categoryName: query.categoryName?.trim() || undefined,
    chargeType: query.chargeType === '' ? undefined : query.chargeType,
    sortType: query.sortType
  }
}

async function loadCourses() {
  loading.value = true

  try {
    const page = await searchClientCourses(buildQuery())
    courses.value = page.records || []
    total.value = page.total || 0
    query.current = page.curPage || query.current
    query.size = page.size || query.size
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '课程搜索失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.current = 1
  loadCourses()
}

function handleReset() {
  query.current = 1
  query.size = 12
  query.keyword = ''
  query.categoryName = ''
  query.chargeType = ''
  query.sortType = 0
  loadCourses()
}

function formatPriceText(price: number, chargeType: ChargeType) {
  if (chargeType === 0) {
    return '免费'
  }

  return `￥${(price / 100).toFixed(2)}`
}

function chargeText(chargeType: ChargeType) {
  const map: Record<ChargeType, string> = {
    0: '免费',
    1: '付费',
    2: 'VIP免费'
  }

  return map[chargeType] || '未知'
}

function displayTitle(course: CourseSearchResult) {
  return safeHighlight(course.highlightTitle, course.title)
}

function displaySubtitle(course: CourseSearchResult) {
  return safeHighlight(course.highlightSubtitle, course.subtitle)
}

function displaySummary(course: CourseSearchResult) {
  return safeHighlight(course.highlightSummary, course.summary)
}

function safeHighlight(value: string | undefined, fallback: string) {
  // 只允许后端约定的 <em> 高亮标签，其它内容按普通文本展示，避免把课程文案里的HTML直接渲染出来。
  return escapeHtml(value || fallback)
    .split('&lt;em&gt;')
    .join('<em>')
    .split('&lt;/em&gt;')
    .join('</em>')
}

function escapeHtml(value: string) {
  return value
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
}

onMounted(loadCourses)
</script>
