<template>
  <section class="episode-section">
    <div class="section-head">
      <h1>我的订单</h1>
      <el-button :loading="loading" @click="loadOrders">刷新</el-button>
    </div>

    <el-form class="admin-filter" :inline="true" @submit.prevent>
      <el-form-item label="订单状态">
        <el-select v-model="query.status" clearable placeholder="全部" style="width: 130px">
          <el-option label="待支付" :value="0" />
          <el-option label="已支付" :value="1" />
          <el-option label="已取消" :value="2" />
          <el-option label="已退款" :value="3" />
          <el-option label="已关闭" :value="4" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="orders" style="width: 100%">
      <el-table-column prop="orderNo" label="订单号" min-width="190" show-overflow-tooltip />
      <el-table-column prop="courseTitle" label="课程" min-width="180" show-overflow-tooltip />
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
      <el-table-column prop="expireTime" label="过期时间" width="170" />
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="370" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openDetail(row)">详情</el-button>
          <el-button
            v-if="row.status === 0"
            size="small"
            type="primary"
            :loading="payingId === row.id"
            @click="handleMockPay(row)"
          >
            模拟支付
          </el-button>
          <el-button
            v-if="row.status === 0"
            size="small"
            type="danger"
            plain
            :loading="cancellingId === row.id"
            @click="handleCancel(row)"
          >
            取消
          </el-button>
          <el-button v-if="row.status === 1" size="small" type="success" @click="goStudy(row)">
            去学习
          </el-button>
          <el-button
            v-if="row.status === 1"
            size="small"
            type="danger"
            plain
            :loading="refundingId === row.id"
            @click="handleRefund(row)"
          >
            申请退款
          </el-button>
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

    <el-dialog v-model="detailVisible" title="订单详情" width="620px">
      <el-descriptions v-if="detail" :column="2" border>
        <el-descriptions-item label="订单号" :span="2">{{ detail.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="课程">{{ detail.courseTitle }}</el-descriptions-item>
        <el-descriptions-item label="原价">￥{{ formatPrice(detail.coursePrice) }}</el-descriptions-item>
        <el-descriptions-item label="优惠金额">-￥{{ formatPrice(detail.discountAmount) }}</el-descriptions-item>
        <el-descriptions-item label="优惠券">{{ detail.couponName || '未使用优惠券' }}</el-descriptions-item>
        <el-descriptions-item label="实付">￥{{ formatPrice(detail.payAmount) }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ statusText(detail.status) }}</el-descriptions-item>
        <el-descriptions-item label="支付方式">{{ payTypeText(detail.payType) }}</el-descriptions-item>
        <el-descriptions-item label="过期时间">{{ detail.expireTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ detail.payTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="退款时间">{{ detail.refundTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detail.updateTime || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  cancelClientOrder,
  getClientOrderDetail,
  listClientOrders,
  mockPayClientOrder,
  mockRefundClientOrder
} from '@/api/order'
import type {
  ClientCourseOrderPageQuery,
  CourseOrder,
  CourseOrderPayType,
  CourseOrderStatus
} from '@/types/order'

type ClientOrderQueryForm = Omit<ClientCourseOrderPageQuery, 'status'> & {
  status?: CourseOrderStatus | ''
}

const router = useRouter()
const loading = ref(false)
const orders = ref<CourseOrder[]>([])
const total = ref(0)
const detailVisible = ref(false)
const detail = ref<CourseOrder | null>(null)
const payingId = ref('')
const cancellingId = ref('')
const refundingId = ref('')

const query = reactive<ClientOrderQueryForm>({
  current: 1,
  size: 10,
  status: ''
})

function buildQuery(): ClientCourseOrderPageQuery {
  return {
    current: query.current,
    size: query.size,
    status: query.status === '' ? undefined : query.status
  }
}

async function loadOrders() {
  loading.value = true

  try {
    const page = await listClientOrders(buildQuery())
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
  query.status = ''
  loadOrders()
}

async function openDetail(order: CourseOrder) {
  loading.value = true

  try {
    detail.value = await getClientOrderDetail(order.id)
    detailVisible.value = true
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '订单详情加载失败')
  } finally {
    loading.value = false
  }
}

async function handleMockPay(order: CourseOrder) {
  try {
    await ElMessageBox.confirm(`确认模拟支付订单 ${order.orderNo} 吗？`, '模拟支付', {
      type: 'info',
      confirmButtonText: '确认支付',
      cancelButtonText: '取消'
    })

    payingId.value = order.id
    ElMessage.info('已发起支付，正在等待订单履约完成')
    // 这里不是单纯创建支付单，而是完整等待：创建支付单 -> 模拟支付成功 -> MQ履约 -> 订单已支付。
    await mockPayClientOrder(order.id)
    ElMessage.success('支付成功，课程已开通')
    await loadOrders()
  } catch (error) {
    if (error === 'cancel') {
      return
    }

    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '支付失败')
  } finally {
    payingId.value = ''
  }
}

async function handleCancel(order: CourseOrder) {
  try {
    await ElMessageBox.confirm(`确认取消订单 ${order.orderNo} 吗？`, '取消订单', {
      type: 'warning',
      confirmButtonText: '确认取消',
      cancelButtonText: '暂不取消'
    })

    cancellingId.value = order.id
    await cancelClientOrder(order.id)
    ElMessage.success('订单已取消，优惠券已释放')
    await loadOrders()
  } catch (error) {
    if (error === 'cancel') {
      return
    }

    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '取消订单失败')
  } finally {
    cancellingId.value = ''
  }
}

async function handleRefund(order: CourseOrder) {
  try {
    await ElMessageBox.confirm(
      `确认申请退款订单 ${order.orderNo} 吗？退款成功后课程权益会被回收。`,
      '申请退款',
      {
        type: 'warning',
        confirmButtonText: '确认退款',
        cancelButtonText: '暂不退款'
      }
    )

    refundingId.value = order.id
    ElMessage.info('已提交退款，正在等待退款履约完成')
    // 这里会等待：创建退款单 -> 退款成功消息发送 -> main-service 回收权益 -> 订单已退款。
    await mockRefundClientOrder(order.id)
    ElMessage.success('退款成功，课程权益已回收')
    await loadOrders()
  } catch (error) {
    if (error === 'cancel') {
      return
    }

    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '退款失败')
  } finally {
    refundingId.value = ''
  }
}

function goStudy(order: CourseOrder) {
  router.push(`/client/courses/${order.courseId}/watch`)
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
