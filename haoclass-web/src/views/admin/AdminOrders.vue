<template>
  <section class="page-block">
    <div class="section-head">
      <h1>订单管理</h1>
      <el-button :loading="loading" @click="loadOrders">刷新</el-button>
    </div>

    <el-form class="admin-filter" :inline="true" @submit.prevent>
      <el-form-item label="订单号">
        <el-input v-model="query.orderNo" clearable placeholder="请输入订单号" style="width: 220px" />
      </el-form-item>
      <el-form-item label="用户ID">
        <el-input v-model="query.userId" clearable placeholder="请输入用户ID" style="width: 180px" />
      </el-form-item>
      <el-form-item label="课程ID">
        <el-input v-model="query.courseId" clearable placeholder="请输入课程ID" style="width: 180px" />
      </el-form-item>
      <el-form-item label="订单状态">
        <el-select v-model="query.status" clearable placeholder="全部" style="width: 130px">
          <el-option label="待支付" :value="0" />
          <el-option label="已支付" :value="1" />
          <el-option label="已取消" :value="2" />
          <el-option label="已退款" :value="3" />
          <el-option label="已关闭" :value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="支付方式">
        <el-select v-model="query.payType" clearable placeholder="全部" style="width: 130px">
          <el-option label="未支付" :value="0" />
          <el-option label="微信" :value="1" />
          <el-option label="支付宝" :value="2" />
          <el-option label="模拟支付" :value="3" />
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

    <el-table v-loading="loading" :data="orders" style="width: 100%">
      <el-table-column prop="orderNo" label="订单号" min-width="190" show-overflow-tooltip />
      <el-table-column prop="courseTitle" label="课程" min-width="180" show-overflow-tooltip />
      <el-table-column prop="userId" label="用户ID" min-width="150" show-overflow-tooltip />
      <el-table-column label="原价" width="110">
        <template #default="{ row }">￥{{ formatPrice(row.coursePrice) }}</template>
      </el-table-column>
      <el-table-column label="实付" width="110">
        <template #default="{ row }">￥{{ formatPrice(row.payAmount) }}</template>
      </el-table-column>
      <el-table-column label="优惠" width="110">
        <template #default="{ row }">-￥{{ formatPrice(row.discountAmount) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="支付方式" width="110">
        <template #default="{ row }">{{ payTypeText(row.payType) }}</template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column prop="payTime" label="支付时间" width="170" />
      <el-table-column label="操作" width="90" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openDetail(row)">详情</el-button>
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
        @size-change="loadOrders"
        @current-change="loadOrders"
      />
    </div>

    <el-dialog v-model="detailVisible" title="订单详情" width="640px">
      <el-descriptions v-if="detail" :column="2" border>
        <el-descriptions-item label="订单号" :span="2">{{ detail.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="课程">{{ detail.courseTitle }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ detail.userId }}</el-descriptions-item>
        <el-descriptions-item label="原价">￥{{ formatPrice(detail.coursePrice) }}</el-descriptions-item>
        <el-descriptions-item label="优惠金额">-￥{{ formatPrice(detail.discountAmount) }}</el-descriptions-item>
        <el-descriptions-item label="优惠券">{{ detail.couponName || '未使用优惠券' }}</el-descriptions-item>
        <el-descriptions-item label="实付">￥{{ formatPrice(detail.payAmount) }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ statusText(detail.status) }}</el-descriptions-item>
        <el-descriptions-item label="支付方式">{{ payTypeText(detail.payType) }}</el-descriptions-item>
        <el-descriptions-item label="第三方流水号" :span="2">
          {{ detail.thirdTradeNo || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="过期时间">{{ detail.expireTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ detail.payTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="取消时间">{{ detail.cancelTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="退款时间">{{ detail.refundTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detail.updateTime || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdminOrderDetail, listAdminOrders } from '@/api/order'
import type {
  AdminCourseOrderPageQuery,
  CourseOrder,
  CourseOrderPayType,
  CourseOrderStatus
} from '@/types/order'

type OrderQueryForm = Omit<
  AdminCourseOrderPageQuery,
  'userId' | 'courseId' | 'status' | 'payType' | 'createTimeStart' | 'createTimeEnd'
> & {
  userId?: string
  courseId?: string
  status?: CourseOrderStatus | ''
  payType?: CourseOrderPayType | ''
}

const loading = ref(false)
const orders = ref<CourseOrder[]>([])
const total = ref(0)
const detailVisible = ref(false)
const detail = ref<CourseOrder | null>(null)
const createTimeRange = ref<[string, string] | []>([])

const query = reactive<OrderQueryForm>({
  current: 1,
  size: 10,
  orderNo: '',
  userId: '',
  courseId: '',
  status: '',
  payType: ''
})

function buildQuery(): AdminCourseOrderPageQuery {
  const [createTimeStart, createTimeEnd] = createTimeRange.value

  return {
    current: query.current,
    size: query.size,
    orderNo: query.orderNo?.trim() || undefined,
    userId: query.userId?.trim() || undefined,
    courseId: query.courseId?.trim() || undefined,
    status: query.status === '' ? undefined : query.status,
    payType: query.payType === '' ? undefined : query.payType,
    createTimeStart,
    createTimeEnd
  }
}

async function loadOrders() {
  loading.value = true

  try {
    const page = await listAdminOrders(buildQuery())
    orders.value = page.records
    total.value = page.total
    query.current = page.curPage || query.current
    query.size = page.size || query.size
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '订单列表加载失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.current = 1
  loadOrders()
}

function handleReset() {
  query.current = 1
  query.size = 10
  query.orderNo = ''
  query.userId = ''
  query.courseId = ''
  query.status = ''
  query.payType = ''
  createTimeRange.value = []
  loadOrders()
}

async function openDetail(order: CourseOrder) {
  loading.value = true

  try {
    detail.value = await getAdminOrderDetail(order.id)
    detailVisible.value = true
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '订单详情加载失败')
  } finally {
    loading.value = false
  }
}

function formatPrice(price?: number) {
  return ((price || 0) / 100).toFixed(2)
}

function statusText(status: CourseOrderStatus) {
  const map: Record<CourseOrderStatus, string> = {
    0: '待支付',
    1: '已支付',
    2: '已取消',
    3: '已退款',
    4: '已关闭'
  }

  return map[status] || '未知'
}

function statusTagType(status: CourseOrderStatus) {
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

function payTypeText(payType: CourseOrderPayType) {
  const map: Record<CourseOrderPayType, string> = {
    0: '未支付',
    1: '微信',
    2: '支付宝',
    3: '模拟支付'
  }

  return map[payType] || '未知'
}

onMounted(loadOrders)
</script>
