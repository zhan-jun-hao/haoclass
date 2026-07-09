<template>
  <section class="page-block">
    <div class="section-head">
      <h1>优惠券管理</h1>
      <div class="head-actions">
        <el-button :loading="loading" @click="loadCoupons">刷新</el-button>
        <el-button type="primary" @click="openCreate">新增优惠券</el-button>
      </div>
    </div>

    <el-form class="admin-filter" :inline="true" @submit.prevent>
      <el-form-item label="优惠券名称">
        <el-input v-model="query.couponName" clearable placeholder="请输入优惠券名称" style="width: 220px" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" clearable placeholder="全部" style="width: 130px">
          <el-option label="草稿" :value="0" />
          <el-option label="已发布" :value="1" />
          <el-option label="已停用" :value="2" />
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

    <el-table v-loading="loading" :data="coupons" style="width: 100%">
      <el-table-column prop="couponName" label="优惠券名称" min-width="180" show-overflow-tooltip />
      <el-table-column label="使用门槛" width="120">
        <template #default="{ row }">{{ thresholdText(row.thresholdAmount) }}</template>
      </el-table-column>
      <el-table-column label="优惠金额" width="110">
        <template #default="{ row }">￥{{ formatPrice(row.discountAmount) }}</template>
      </el-table-column>
      <el-table-column label="领取进度" width="130">
        <template #default="{ row }">{{ row.receivedCount || 0 }} / {{ row.totalStock }}</template>
      </el-table-column>
      <el-table-column prop="receiveStartTime" label="领取开始" width="170" />
      <el-table-column prop="receiveEndTime" label="领取结束" width="170" />
      <el-table-column prop="validEndTime" label="有效期至" width="170" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="300" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openDetail(row)">详情</el-button>
          <el-button v-if="row.status === 0" size="small" @click="openEdit(row)">编辑</el-button>
          <el-button v-if="row.status === 0" size="small" type="success" @click="publishCoupon(row)">发布</el-button>
          <el-button v-if="row.status === 1" size="small" type="warning" @click="stopCoupon(row)">停用</el-button>
          <el-button v-if="row.status !== 1" size="small" type="danger" @click="removeCoupon(row)">删除</el-button>
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
        @size-change="loadCoupons"
        @current-change="loadCoupons"
      />
    </div>

    <el-dialog
      v-model="formVisible"
      :title="editingId ? '编辑优惠券' : '新增优惠券'"
      width="720px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="110px">
        <el-form-item label="优惠券名称" prop="couponName">
          <el-input v-model="form.couponName" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="优惠券说明" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" maxlength="500" show-word-limit />
        </el-form-item>
        <div class="form-grid">
          <el-form-item label="使用门槛" prop="thresholdYuan">
            <el-input-number v-model="form.thresholdYuan" :min="0" :precision="2" :step="10" controls-position="right" />
            <span class="field-unit">元，0 为无门槛</span>
          </el-form-item>
          <el-form-item label="优惠金额" prop="discountYuan">
            <el-input-number v-model="form.discountYuan" :min="0.01" :precision="2" :step="1" controls-position="right" />
            <span class="field-unit">元</span>
          </el-form-item>
        </div>
        <el-form-item label="发行数量" prop="totalStock">
          <el-input-number v-model="form.totalStock" :min="1" :precision="0" controls-position="right" />
        </el-form-item>
        <el-form-item label="领取时间" prop="receiveTimeRange">
          <el-date-picker
            v-model="form.receiveTimeRange"
            type="datetimerange"
            value-format="YYYY-MM-DDTHH:mm:ss"
            start-placeholder="领取开始时间"
            end-placeholder="领取结束时间"
            range-separator="至"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="使用有效期" prop="validTimeRange">
          <el-date-picker
            v-model="form.validTimeRange"
            type="datetimerange"
            value-format="YYYY-MM-DDTHH:mm:ss"
            start-placeholder="有效期开始时间"
            end-placeholder="有效期结束时间"
            range-separator="至"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitForm">保存草稿</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="优惠券详情" width="720px">
      <el-descriptions v-if="detail" :column="2" border>
        <el-descriptions-item label="优惠券名称" :span="2">{{ detail.couponName }}</el-descriptions-item>
        <el-descriptions-item label="优惠金额">￥{{ formatPrice(detail.discountAmount) }}</el-descriptions-item>
        <el-descriptions-item label="使用门槛">{{ thresholdText(detail.thresholdAmount) }}</el-descriptions-item>
        <el-descriptions-item label="领取数量">{{ detail.receivedCount || 0 }} / {{ detail.totalStock }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ statusText(detail.status) }}</el-descriptions-item>
        <el-descriptions-item label="领取开始">{{ detail.receiveStartTime }}</el-descriptions-item>
        <el-descriptions-item label="领取结束">{{ detail.receiveEndTime }}</el-descriptions-item>
        <el-descriptions-item label="有效期开始">{{ detail.validStartTime }}</el-descriptions-item>
        <el-descriptions-item label="有效期结束">{{ detail.validEndTime }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detail.updateTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="优惠券说明" :span="2">
          <div class="detail-description">{{ detail.description || '-' }}</div>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createAdminCouponTemplate,
  deleteAdminCouponTemplate,
  getAdminCouponTemplateDetail,
  listAdminCouponTemplates,
  publishAdminCouponTemplate,
  stopAdminCouponTemplate,
  updateAdminCouponTemplate
} from '@/api/coupon'
import type {
  CouponTemplate,
  CouponTemplatePageQuery,
  CouponTemplatePayload,
  CouponTemplateStatus
} from '@/types/coupon'

type CouponQueryForm = Omit<CouponTemplatePageQuery, 'status' | 'createTimeStart' | 'createTimeEnd'> & {
  status: CouponTemplateStatus | ''
}

interface CouponForm {
  couponName: string
  description: string
  thresholdYuan: number
  discountYuan: number
  totalStock: number
  receiveTimeRange: [string, string] | []
  validTimeRange: [string, string] | []
}

const loading = ref(false)
const saving = ref(false)
const coupons = ref<CouponTemplate[]>([])
const total = ref(0)
const formVisible = ref(false)
const detailVisible = ref(false)
const editingId = ref('')
const detail = ref<CouponTemplate | null>(null)
const formRef = ref<FormInstance>()
const createTimeRange = ref<[string, string] | []>([])

const query = reactive<CouponQueryForm>({
  current: 1,
  size: 10,
  couponName: '',
  status: ''
})

const form = reactive<CouponForm>({
  couponName: '',
  description: '',
  thresholdYuan: 0,
  discountYuan: 1,
  totalStock: 100,
  receiveTimeRange: [],
  validTimeRange: []
})

const formRules: FormRules = {
  couponName: [{ required: true, message: '请输入优惠券名称', trigger: 'blur' }],
  discountYuan: [{ required: true, message: '请输入优惠金额', trigger: 'change' }],
  totalStock: [{ required: true, message: '请输入发行数量', trigger: 'change' }],
  receiveTimeRange: [{ required: true, message: '请选择领取时间', trigger: 'change' }],
  validTimeRange: [{ required: true, message: '请选择使用有效期', trigger: 'change' }]
}

function buildQuery(): CouponTemplatePageQuery {
  const [createTimeStart, createTimeEnd] = createTimeRange.value
  return {
    current: query.current,
    size: query.size,
    couponName: query.couponName?.trim() || undefined,
    status: query.status === '' ? undefined : query.status,
    createTimeStart,
    createTimeEnd
  }
}

async function loadCoupons() {
  loading.value = true
  try {
    const page = await listAdminCouponTemplates(buildQuery())
    coupons.value = page.records
    total.value = page.total
    query.current = page.curPage || query.current
    query.size = page.size || query.size
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '优惠券列表加载失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.current = 1
  loadCoupons()
}

function handleReset() {
  query.current = 1
  query.size = 10
  query.couponName = ''
  query.status = ''
  createTimeRange.value = []
  loadCoupons()
}

function resetForm() {
  form.couponName = ''
  form.description = ''
  form.thresholdYuan = 0
  form.discountYuan = 1
  form.totalStock = 100
  form.receiveTimeRange = []
  form.validTimeRange = []
  formRef.value?.clearValidate()
}

function openCreate() {
  editingId.value = ''
  resetForm()
  formVisible.value = true
}

async function openEdit(coupon: CouponTemplate) {
  loading.value = true
  try {
    const current = await getAdminCouponTemplateDetail(coupon.id)
    editingId.value = current.id
    form.couponName = current.couponName
    form.description = current.description || ''
    form.thresholdYuan = current.thresholdAmount / 100
    form.discountYuan = current.discountAmount / 100
    form.totalStock = current.totalStock
    form.receiveTimeRange = [current.receiveStartTime, current.receiveEndTime]
    form.validTimeRange = [current.validStartTime, current.validEndTime]
    formVisible.value = true
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '优惠券详情加载失败')
  } finally {
    loading.value = false
  }
}

async function openDetail(coupon: CouponTemplate) {
  try {
    detail.value = await getAdminCouponTemplateDetail(coupon.id)
    detailVisible.value = true
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '优惠券详情加载失败')
  }
}

function buildPayload(): CouponTemplatePayload {
  if (form.receiveTimeRange.length !== 2 || form.validTimeRange.length !== 2) {
    throw new Error('请选择完整的领取时间和使用有效期')
  }
  const [receiveStartTime, receiveEndTime] = form.receiveTimeRange as [string, string]
  const [validStartTime, validEndTime] = form.validTimeRange as [string, string]
  return {
    couponName: form.couponName.trim(),
    description: form.description.trim() || undefined,
    thresholdAmount: Math.round(form.thresholdYuan * 100),
    discountAmount: Math.round(form.discountYuan * 100),
    totalStock: form.totalStock,
    receiveStartTime,
    receiveEndTime,
    validStartTime,
    validEndTime
  }
}

async function submitForm() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }
  if (form.thresholdYuan > 0 && form.discountYuan > form.thresholdYuan) {
    ElMessage.warning('优惠金额不能大于使用门槛')
    return
  }

  saving.value = true
  try {
    const payload = buildPayload()
    if (editingId.value) {
      await updateAdminCouponTemplate(editingId.value, payload)
      ElMessage.success('优惠券已更新')
    } else {
      await createAdminCouponTemplate(payload)
      ElMessage.success('优惠券草稿已创建')
    }
    formVisible.value = false
    await loadCoupons()
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '优惠券保存失败')
  } finally {
    saving.value = false
  }
}

async function publishCoupon(coupon: CouponTemplate) {
  await runConfirmedAction(coupon, '发布', publishAdminCouponTemplate)
}

async function stopCoupon(coupon: CouponTemplate) {
  await runConfirmedAction(coupon, '停用', stopAdminCouponTemplate)
}

async function removeCoupon(coupon: CouponTemplate) {
  await runConfirmedAction(coupon, '删除', deleteAdminCouponTemplate)
}

async function runConfirmedAction(
  coupon: CouponTemplate,
  action: '发布' | '停用' | '删除',
  request: (id: string) => Promise<void>
) {
  try {
    await ElMessageBox.confirm(`确认${action}优惠券“${coupon.couponName}”吗？`, `${action}优惠券`, {
      type: action === '发布' ? 'success' : 'warning',
      confirmButtonText: action,
      cancelButtonText: '取消'
    })
    await request(coupon.id)
    ElMessage.success(`优惠券已${action}`)
    await loadCoupons()
  } catch (error) {
    if (error === 'cancel') {
      return
    }
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : `${action}优惠券失败`)
  }
}

function formatPrice(value?: number) {
  return ((value || 0) / 100).toFixed(2)
}

function thresholdText(value: number) {
  return value === 0 ? '无门槛' : `满 ￥${formatPrice(value)}`
}

function statusText(status: CouponTemplateStatus) {
  return { 0: '草稿', 1: '已发布', 2: '已停用' }[status]
}

function statusTagType(status: CouponTemplateStatus) {
  if (status === 1) {
    return 'success'
  }
  if (status === 0) {
    return 'warning'
  }
  return 'info'
}

onMounted(loadCoupons)
</script>

<style scoped>
.head-actions {
  display: flex;
  gap: 10px;
}

.field-unit {
  margin-left: 8px;
  color: #6c7893;
  font-size: 13px;
}

.detail-description {
  line-height: 1.7;
  white-space: pre-wrap;
  overflow-wrap: anywhere;
}
</style>
