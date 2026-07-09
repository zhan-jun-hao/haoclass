<template>
  <section class="page-block">
    <div class="section-head">
      <h1>课程管理</h1>
      <div class="admin-toolbar">
        <el-button :loading="indexLoading" @click="handleRebuildIndex">重建搜索索引</el-button>
        <el-button type="primary" @click="openCreateDialog">新增课程</el-button>
      </div>
    </div>

    <el-form class="admin-filter" :inline="true" @submit.prevent>
      <el-form-item label="课程名称">
        <el-input v-model="query.title" clearable placeholder="请输入课程名称" />
      </el-form-item>
      <el-form-item label="课程分类">
        <el-input v-model="query.categoryName" clearable placeholder="例如 JavaSE" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" clearable placeholder="全部" style="width: 120px">
          <el-option label="草稿" :value="0" />
          <el-option label="上架" :value="1" />
          <el-option label="下架" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="courses" style="width: 100%">
      <el-table-column label="封面" width="116">
        <template #default="{ row }">
          <img
            class="admin-course-cover"
            :src="resolveCourseCover(row)"
            :alt="row.title"
            @error="useFallbackCourseCover"
          />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="课程名称" min-width="180" />
      <el-table-column prop="categoryName" label="分类" width="130" />
      <el-table-column prop="teacherName" label="讲师" width="120" />
      <el-table-column label="价格" width="120">
        <template #default="{ row }">￥{{ formatPrice(row.price) }}</template>
      </el-table-column>
      <el-table-column prop="episodeCount" label="集数" width="90" />
      <el-table-column prop="buyCount" label="销量" width="90" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="收费" width="110">
        <template #default="{ row }">{{ chargeText(row.chargeType) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="360" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" @click="openEpisodeDialog(row)">集数</el-button>
          <el-button size="small" @click="handleSyncIndex(row)">同步索引</el-button>
          <el-button
            v-if="row.status === 1"
            size="small"
            type="warning"
            @click="handleUnpublish(row)"
          >
            下架
          </el-button>
          <el-button v-else size="small" type="success" @click="handlePublish(row)">上架</el-button>
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
        @size-change="loadCourses"
        @current-change="loadCourses"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑课程' : '新增课程'" width="720px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="课程名称" required>
          <el-input v-model="form.title" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="课程分类" required>
          <el-input v-model="form.categoryName" placeholder="例如 JavaSE、Redis、MySQL" />
        </el-form-item>
        <el-form-item label="课程副标题" required>
          <el-input v-model="form.subtitle" placeholder="请输入课程副标题" />
        </el-form-item>
        <el-form-item label="封面地址" required>
          <el-input v-model="form.coverUrl" placeholder="例如 /images/course-springcloud-microservices.png">
            <template #append>
              <el-button @click="useLocalSpringCloudCover">本地封面</el-button>
            </template>
          </el-input>
          <div v-if="form.coverUrl" class="cover-preview">
            <img :src="form.coverUrl" alt="课程封面预览" @error="useFallbackCourseCover" />
          </div>
        </el-form-item>
        <el-form-item label="讲师名称" required>
          <el-input v-model="form.teacherName" placeholder="请输入讲师名称" />
        </el-form-item>
        <el-form-item label="课程摘要" required>
          <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="请输入课程摘要" />
        </el-form-item>
        <el-form-item label="课程详情" required>
          <el-input v-model="form.detail" type="textarea" :rows="4" placeholder="请输入课程详情" />
        </el-form-item>
        <div class="form-grid">
          <el-form-item label="价格(分)" required>
            <el-input-number v-model="form.price" :min="0" :step="100" style="width: 100%" />
          </el-form-item>
          <el-form-item label="排序" required>
            <el-input-number v-model="form.sort" :min="0" style="width: 100%" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="form.status" style="width: 100%">
              <el-option label="草稿" :value="0" />
              <el-option label="上架" :value="1" />
              <el-option label="下架" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="收费类型">
            <el-select v-model="form.chargeType" style="width: 100%">
              <el-option label="免费" :value="0" />
              <el-option label="付费" :value="1" />
              <el-option label="VIP免费" :value="2" />
            </el-select>
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="episodeDialogVisible" :title="`${currentCourse?.title || ''} - 集数管理`" width="900px">
      <div class="section-head episode-head">
        <h3>集数列表</h3>
        <el-button type="primary" @click="openCreateEpisodeDialog">新增集数</el-button>
      </div>
      <el-table v-loading="episodeLoading" :data="episodes" style="width: 100%">
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="title" label="标题" min-width="180" />
        <el-table-column label="时长" width="100">
          <template #default="{ row }">{{ formatDuration(row.durationSeconds) }}</template>
        </el-table-column>
        <el-table-column label="试看" width="90">
          <template #default="{ row }">{{ row.freePreview === 1 ? '是' : '否' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status || 0)">{{ statusText(row.status || 0) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="videoUrl" label="视频地址" min-width="220" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEditEpisodeDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDeleteEpisode(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog v-model="episodeFormVisible" :title="editingEpisodeId ? '编辑集数' : '新增集数'" width="620px">
      <el-form :model="episodeForm" label-width="100px">
        <el-form-item label="标题" required>
          <el-input v-model="episodeForm.title" placeholder="请输入集数标题" />
        </el-form-item>
        <el-form-item label="视频地址">
          <el-input v-model="episodeForm.videoUrl" placeholder="请输入视频URL" />
        </el-form-item>
        <div class="form-grid">
          <el-form-item label="时长(秒)" required>
            <el-input-number v-model="episodeForm.durationSeconds" :min="0" style="width: 100%" />
          </el-form-item>
          <el-form-item label="排序" required>
            <el-input-number v-model="episodeForm.sort" :min="0" style="width: 100%" />
          </el-form-item>
          <el-form-item label="是否试看">
            <el-select v-model="episodeForm.freePreview" style="width: 100%">
              <el-option label="否" :value="0" />
              <el-option label="是" :value="1" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="episodeForm.status" style="width: 100%">
              <el-option label="草稿" :value="0" />
              <el-option label="上架" :value="1" />
              <el-option label="下架" :value="2" />
            </el-select>
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="episodeFormVisible = false">取消</el-button>
        <el-button type="primary" :loading="episodeSaving" @click="handleSubmitEpisode">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createAdminCourseEpisode,
  createAdminCourse,
  deleteAdminCourse,
  deleteAdminCourseEpisode,
  getAdminCourseEpisodeDetail,
  getAdminCourseDetail,
  listAdminCourseEpisodes,
  listAdminCourses,
  publishAdminCourse,
  rebuildAdminCourseSearchIndex,
  syncAdminCourseSearchIndex,
  unpublishAdminCourse,
  updateAdminCourse,
  updateAdminCourseEpisode
} from '@/api/course'
import {
  SPRING_CLOUD_MICROSERVICES_COVER,
  resolveCourseCover,
  useFallbackCourseCover
} from '@/utils/courseCover'
import type {
  ChargeType,
  Course,
  CourseEpisode,
  CourseEpisodeFormPayload,
  CourseFormPayload,
  CoursePageQuery,
  CourseStatus
} from '@/types/course'

type CourseQueryForm = Omit<CoursePageQuery, 'status'> & {
  status?: CourseStatus | ''
}

const loading = ref(false)
const saving = ref(false)
const indexLoading = ref(false)
const dialogVisible = ref(false)
const editingId = ref('')
const courses = ref<Course[]>([])
const total = ref(0)
const episodeDialogVisible = ref(false)
const episodeFormVisible = ref(false)
const episodeLoading = ref(false)
const episodeSaving = ref(false)
const currentCourse = ref<Course | null>(null)
const episodes = ref<CourseEpisode[]>([])
const editingEpisodeId = ref('')

const query = reactive<CourseQueryForm>({
  current: 1,
  size: 10,
  title: '',
  categoryName: '',
  status: ''
})

const form = reactive<CourseFormPayload>({
  title: '',
  categoryName: '',
  subtitle: '',
  coverUrl: '',
  teacherName: '',
  summary: '',
  detail: '',
  price: 0,
  sort: 0,
  status: 0,
  chargeType: 1
})

const episodeForm = reactive<CourseEpisodeFormPayload>({
  title: '',
  videoUrl: '',
  durationSeconds: 0,
  freePreview: 0,
  sort: 0,
  status: 0
})

function buildQuery(): CoursePageQuery {
  return {
    current: query.current,
    size: query.size,
    title: query.title?.trim() || undefined,
    categoryName: query.categoryName?.trim() || undefined,
    status: query.status === '' ? undefined : query.status
  }
}

async function loadCourses() {
  loading.value = true

  try {
    const page = await listAdminCourses(buildQuery())
    courses.value = page.records
    total.value = page.total
    query.current = page.curPage || query.current
    query.size = page.size || query.size
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '课程列表加载失败')
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
  query.size = 10
  query.title = ''
  query.categoryName = ''
  query.status = ''
  loadCourses()
}

function resetForm() {
  form.title = ''
  form.categoryName = ''
  form.subtitle = ''
  form.coverUrl = SPRING_CLOUD_MICROSERVICES_COVER
  form.teacherName = ''
  form.summary = ''
  form.detail = ''
  form.price = 0
  form.sort = 0
  form.status = 0
  form.chargeType = 1
}

function useLocalSpringCloudCover() {
  form.coverUrl = SPRING_CLOUD_MICROSERVICES_COVER
}

function openCreateDialog() {
  editingId.value = ''
  resetForm()
  dialogVisible.value = true
}

async function openEditDialog(course: Course) {
  loading.value = true

  try {
    const detail = await getAdminCourseDetail(course.id)
    editingId.value = detail.id
    form.title = detail.title
    form.categoryName = detail.categoryName
    form.subtitle = detail.subtitle
    form.coverUrl = detail.coverUrl
    form.teacherName = detail.teacherName
    form.summary = detail.summary
    form.detail = detail.detail || ''
    form.price = detail.price
    form.sort = detail.sort
    form.status = detail.status
    form.chargeType = detail.chargeType
    dialogVisible.value = true
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '课程详情加载失败')
  } finally {
    loading.value = false
  }
}

function resetEpisodeForm() {
  episodeForm.title = ''
  episodeForm.videoUrl = ''
  episodeForm.durationSeconds = 0
  episodeForm.freePreview = 0
  episodeForm.sort = episodes.value.length + 1
  episodeForm.status = 0
}

async function openEpisodeDialog(course: Course) {
  currentCourse.value = course
  episodeDialogVisible.value = true
  await loadEpisodes()
}

async function loadEpisodes() {
  if (!currentCourse.value) {
    return
  }

  episodeLoading.value = true

  try {
    episodes.value = await listAdminCourseEpisodes(currentCourse.value.id)
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '集数列表加载失败')
  } finally {
    episodeLoading.value = false
  }
}

function openCreateEpisodeDialog() {
  editingEpisodeId.value = ''
  resetEpisodeForm()
  episodeFormVisible.value = true
}

async function openEditEpisodeDialog(episode: CourseEpisode) {
  if (!currentCourse.value) {
    return
  }

  episodeLoading.value = true

  try {
    const detail = await getAdminCourseEpisodeDetail(currentCourse.value.id, String(episode.id))
    editingEpisodeId.value = String(detail.id)
    episodeForm.title = detail.title
    episodeForm.videoUrl = detail.videoUrl || ''
    episodeForm.durationSeconds = detail.durationSeconds || 0
    episodeForm.freePreview = detail.freePreview || 0
    episodeForm.sort = detail.sort || 0
    episodeForm.status = detail.status || 0
    episodeFormVisible.value = true
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '集数详情加载失败')
  } finally {
    episodeLoading.value = false
  }
}

async function handleSubmit() {
  saving.value = true

  try {
    const payload = { ...form }

    if (editingId.value) {
      await updateAdminCourse(editingId.value, payload)
      ElMessage.success('课程已更新')
    } else {
      await createAdminCourse(payload)
      ElMessage.success('课程已新增')
    }

    dialogVisible.value = false
    loadCourses()
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  } finally {
    saving.value = false
  }
}

async function handleDelete(course: Course) {
  await ElMessageBox.confirm(`确认删除课程「${course.title}」吗？`, '删除课程', {
    type: 'warning'
  })

  try {
    await deleteAdminCourse(course.id)
    ElMessage.success('课程已删除')
    loadCourses()
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '删除失败')
  }
}

async function handlePublish(course: Course) {
  try {
    await publishAdminCourse(course.id)
    ElMessage.success('课程已上架')
    loadCourses()
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '上架失败')
  }
}

async function handleUnpublish(course: Course) {
  try {
    await unpublishAdminCourse(course.id)
    ElMessage.success('课程已下架')
    loadCourses()
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '下架失败')
  }
}

async function handleRebuildIndex() {
  await ElMessageBox.confirm('确认重建所有已上架课程的搜索索引吗？', '重建搜索索引', {
    type: 'warning'
  })

  indexLoading.value = true

  try {
    const count = await rebuildAdminCourseSearchIndex()
    ElMessage.success(`搜索索引已重建，共同步 ${count} 门已上架课程`)
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '重建搜索索引失败')
  } finally {
    indexLoading.value = false
  }
}

async function handleSyncIndex(course: Course) {
  try {
    await syncAdminCourseSearchIndex(course.id)
    ElMessage.success('课程搜索索引已同步')
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '同步课程搜索索引失败')
  }
}

async function handleSubmitEpisode() {
  if (!currentCourse.value) {
    return
  }

  if (!episodeForm.title.trim()) {
    ElMessage.warning('请输入集数标题')
    return
  }

  episodeSaving.value = true

  try {
    const payload = { ...episodeForm }
    if (editingEpisodeId.value) {
      await updateAdminCourseEpisode(currentCourse.value.id, editingEpisodeId.value, payload)
      ElMessage.success('集数已更新')
    } else {
      await createAdminCourseEpisode(currentCourse.value.id, payload)
      ElMessage.success('集数已新增')
    }

    episodeFormVisible.value = false
    await loadEpisodes()
    await loadCourses()
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '集数保存失败')
  } finally {
    episodeSaving.value = false
  }
}

async function handleDeleteEpisode(episode: CourseEpisode) {
  if (!currentCourse.value) {
    return
  }

  await ElMessageBox.confirm(`确认删除集数“${episode.title}”吗？`, '删除集数', {
    type: 'warning'
  })

  try {
    await deleteAdminCourseEpisode(currentCourse.value.id, String(episode.id))
    ElMessage.success('集数已删除')
    await loadEpisodes()
    await loadCourses()
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '集数删除失败')
  }
}

function formatPrice(price: number) {
  return (price / 100).toFixed(2)
}

function formatDuration(seconds?: number) {
  const totalSeconds = seconds || 0
  const minute = Math.floor(totalSeconds / 60)
  const second = totalSeconds % 60
  return `${minute}:${String(second).padStart(2, '0')}`
}

function statusText(status: CourseStatus) {
  const map: Record<CourseStatus, string> = {
    0: '草稿',
    1: '上架',
    2: '下架'
  }

  return map[status] || '未知'
}

function statusTagType(status: CourseStatus) {
  if (status === 1) {
    return 'success'
  }

  if (status === 2) {
    return 'info'
  }

  return 'warning'
}

function chargeText(chargeType: ChargeType) {
  const map: Record<ChargeType, string> = {
    0: '免费',
    1: '付费',
    2: 'VIP免费'
  }

  return map[chargeType] || '未知'
}

onMounted(loadCourses)
</script>

<style scoped>
.admin-course-cover {
  width: 82px;
  aspect-ratio: 16 / 9;
  object-fit: cover;
  border: 1px solid #e8ebf2;
  border-radius: 6px;
}

.cover-preview {
  width: 240px;
  margin-top: 10px;
}

.cover-preview img {
  width: 100%;
  aspect-ratio: 16 / 9;
  object-fit: cover;
  border: 1px solid #e8ebf2;
  border-radius: 6px;
}
</style>
