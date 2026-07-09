<template>
  <section class="page-block">
    <div class="section-head">
      <h1>退款管理</h1>
      <el-button :loading="loading" @click="loadRefunds">刷新</el-button>
    </div>

    <el-form class="admin-filter" :inline="true" @submit.prevent>
      <el-form-item label="退款单号">
        <el-input v-model="query.refundNo" clearable placeholder="请输入退款单号" style="width: 220px" />
      </el-form-item>
      <el-form-item label="支付单号">
        <el-input v-model="query.paymentNo" clearable placeholder="请输入支付单号" style="width: 220px" />
      </el-form-item>
      <el-form-item label="业务订单号">
        <el-input v-model="query.bizOrderNo" clearable placeholder="请输入业务订单号" style="width: 220px" />
      </el-form-item>
      <el-form-item label="用户ID">
        <el-input v-model="query.userId" clearable placeholder="请输入用户ID" style="width: 180px" />
      </el-form-item>
      <el-form-item label="退款状态">
        <el-select v-model="query.status" clearable placeholder="全部" style="width: 130px">
          <el-option label="待退款" :value="0" />
          <el-option label="退款中" :value="1" />
          <el-option label="退款成功" :value="2" />
          <el-option label="退款失败" :value="3" />
          <el-option label="已关闭" :value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="支付渠道">
        <el-select v-model="query.payChannel" clearable placeholder="全部" style="width: 130px">
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

    <el-table v-loading="loading" :data="refunds" style="width: 100%">
      <el-table-column prop="refundNo" label="退款单号" min-width="200" show-overflow-tooltip />
      <el-table-column prop="paymentNo" label="支付单号" min-width="200" show-overflow-tooltip />
      <el-table-column prop="bizOrderNo" label="业务订单号" min-width="190" show-overflow-tooltip />
      <el-table-column prop="userId" label="用户ID" min-width="150" show-overflow-tooltip />
      <el-table-column label="原支付" width="110">
        <template #default="{ row }">￥{{ formatPrice(row.payAmount) }}</template>
      </el-table-column>
      <el-table-column label="退款金额" width="110">
        <template #default="{ row }">￥{{ formatPrice(row.refundAmount) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="refundStatusTagType(row.status)">{{ refundStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="支付渠道" width="110">
        <template #default="{ row }">{{ payChannelText(row.payChannel) }}</template>
      </el-table-column>
      <el-table-column prop="applyTime" label="申请时间" width="170" />
      <el-table-column prop="refundTime" label="退款时间" width="170" />
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
        @size-change="loadRefunds"
        @current-change="loadRefunds"
      />
    </div>

    <el-dialog v-model="detailVisible" title="退款详情" width="760px">
      <el-descriptions v-if="detail" :column="2" border>
        <el-descriptions-item label="退款单号" :span="2">{{ detail.refundNo }}</el-descriptions-item>
        <el-descriptions-item label="支付单号" :span="2">{{ detail.paymentNo }}</el-descriptions-item>
        <el-descriptions-item label="业务类型">{{ bizTypeText(detail.bizType) }}</el-descriptions-item>
        <el-descriptions-item label="业务订单号">{{ detail.bizOrderNo }}</el-descriptions-item>
        <el-descriptions-item label="业务订单ID">{{ detail.bizOrderId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ detail.userId }}</el-descriptions-item>
        <el-descriptions-item label="原支付金额">￥{{ formatPrice(detail.payAmount) }}</el-descriptions-item>
        <el-descriptions-item label="退款金额">￥{{ formatPrice(detail.refundAmount) }}</el-descriptions-item>
        <el-descriptions-item label="货币">{{ detail.currency || 'CNY' }}</el-descriptions-item>
        <el-descriptions-item label="支付渠道">{{ payChannelText(detail.payChannel) }}</el-descriptions-item>
        <el-descriptions-item label="退款状态">{{ refundStatusText(detail.status) }}</el-descriptions-item>
        <el-descriptions-item label="原支付流水" :span="2">{{ detail.thirdTradeNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="退款流水" :span="2">{{ detail.thirdRefundNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="退款原因" :span="2">{{ detail.refundReason || '-' }}</el-descriptions-item>
        <el-descriptions-item label="失败原因" :span="2">{{ detail.failureReason || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ detail.applyTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="退款时间">{{ detail.refundTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="关闭时间">{{ detail.closeTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detail.updateTime || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdminRefundOrderDetail, listAdminRefundOrders } from '@/api/refund'
import type {
  AdminPaymentRefundOrderPageQuery,
  PaymentBizType,
  PaymentChannel,
  PaymentRefundOrder,
  PaymentRefundStatus
} from '@/types/refund'

type RefundQueryForm = Omit<
  AdminPaymentRefundOrderPageQuery,
  'bizType' | 'payChannel' | 'status' | 'createTimeStart' | 'createTimeEnd'
> & {
  bizType?: PaymentBizType | ''
  payChannel?: PaymentChannel | ''
  status?: PaymentRefundStatus | ''
}

const loading = ref(false)
const refunds = ref<PaymentRefundOrder[]>([])
const total = ref(0)
const detailVisible = ref(false)
const detail = ref<PaymentRefundOrder | null>(null)
const createTimeRange = ref<[string, string] | []>([])

const query = reactive<RefundQueryForm>({
  current: 1,
  size: 10,
  refundNo: '',
  paymentNo: '',
  bizOrderNo: '',
  userId: '',
  payChannel: '',
  status: ''
})

function buildQuery(): AdminPaymentRefundOrderPageQuery {
  const [createTimeStart, createTimeEnd] = createTimeRange.value

  // 组装查询参数：空字符串不传给后端，避免被当成精确查询条件。
  return {
    current: query.current,
    size: query.size,
    refundNo: query.refundNo?.trim() || undefined,
    paymentNo: query.paymentNo?.trim() || undefined,
    bizOrderNo: query.bizOrderNo?.trim() || undefined,
    userId: query.userId?.trim() || undefined,
    payChannel: query.payChannel === '' ? undefined : query.payChannel,
    status: query.status === '' ? undefined : query.status,
    createTimeStart,
    createTimeEnd
  }
}

async function loadRefunds() {
  loading.value = true

  try {
    const page = await listAdminRefundOrders(buildQuery())
    refunds.value = page.records
    total.value = page.total
    query.current = page.curPage || query.current
    query.size = page.size || query.size
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '退款列表加载失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.current = 1
  loadRefunds()
}

function handleReset() {
  query.current = 1
  query.size = 10
  query.refundNo = ''
  query.paymentNo = ''
  query.bizOrderNo = ''
  query.userId = ''
  query.payChannel = ''
  query.status = ''
  createTimeRange.value = []
  loadRefunds()
}

async function openDetail(refund: PaymentRefundOrder) {
  loading.value = true

  try {
    detail.value = await getAdminRefundOrderDetail(refund.refundNo)
    detailVisible.value = true
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '退款详情加载失败')
  } finally {
    loading.value = false
  }
}

function formatPrice(price?: number) {
  return ((price || 0) / 100).toFixed(2)
}

function bizTypeText(bizType?: PaymentBizType) {
  return bizType === 1 ? '课程订单' : '未知'
}

function payChannelText(payChannel?: PaymentChannel) {
  const map: Record<PaymentChannel, string> = {
    1: '微信',
    2: '支付宝',
    3: '模拟支付'
  }

  return payChannel ? map[payChannel] : '未知'
}

function refundStatusText(status: PaymentRefundStatus) {
  const map: Record<PaymentRefundStatus, string> = {
    0: '待退款',
    1: '退款中',
    2: '退款成功',
    3: '退款失败',
    4: '已关闭'
  }

  return map[status] || '未知'
}

function refundStatusTagType(status: PaymentRefundStatus) {
  if (status === 2) {
    return 'success'
  }
  if (status === 0 || status === 1) {
    return 'warning'
  }
  if (status === 3) {
    return 'danger'
  }
  return 'info'
}

onMounted(loadRefunds)
</script>
