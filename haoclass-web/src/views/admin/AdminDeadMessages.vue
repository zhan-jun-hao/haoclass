<template>
  <section class="page-block">
    <div class="section-head">
      <h1>异常消息管理</h1>
      <el-button :loading="loading" @click="loadMessages">刷新</el-button>
    </div>

    <el-form class="admin-filter" :inline="true" @submit.prevent>
      <el-form-item label="业务类型">
        <el-input v-model="query.bizType" clearable placeholder="请输入业务类型" style="width: 180px" />
      </el-form-item>
      <el-form-item label="业务ID">
        <el-input v-model="query.bizId" clearable placeholder="请输入业务ID" style="width: 180px" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" clearable placeholder="全部" style="width: 130px">
          <el-option label="待处理" :value="0" />
          <el-option label="已重投" :value="1" />
          <el-option label="已忽略" :value="2" />
          <el-option label="重投失败" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="来源队列">
        <el-input v-model="query.sourceQueue" clearable placeholder="请输入来源队列" style="width: 240px" />
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

    <el-table v-loading="loading" :data="messages" style="width: 100%">
      <el-table-column prop="bizType" label="业务类型" width="120" show-overflow-tooltip />
      <el-table-column prop="bizId" label="业务ID" min-width="150" show-overflow-tooltip />
      <el-table-column prop="sourceQueue" label="来源队列" min-width="220" show-overflow-tooltip />
      <el-table-column prop="deadReason" label="死信原因" min-width="180" show-overflow-tooltip />
      <el-table-column prop="lastError" label="最近错误" min-width="220" show-overflow-tooltip />
      <el-table-column label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="retryCount" label="重试次数" width="100" />
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="230" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openDetail(row)">详情</el-button>
          <el-button
            v-if="canOperate(row.status)"
            size="small"
            type="primary"
            :loading="actionLoadingKey === `retry-${row.id}`"
            @click="retryMessage(row)"
          >
            重试
          </el-button>
          <el-button
            v-if="canOperate(row.status)"
            size="small"
            type="warning"
            :loading="actionLoadingKey === `ignore-${row.id}`"
            @click="ignoreMessage(row)"
          >
            忽略
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
        @size-change="loadMessages"
        @current-change="loadMessages"
      />
    </div>

    <el-dialog v-model="detailVisible" title="异常消息详情" width="860px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="业务类型">{{ detail.bizType }}</el-descriptions-item>
          <el-descriptions-item label="业务ID">{{ detail.bizId }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ statusText(detail.status) }}</el-descriptions-item>
          <el-descriptions-item label="重试次数">{{ detail.retryCount }}</el-descriptions-item>
          <el-descriptions-item label="来源队列" :span="2">{{ detail.sourceQueue || '-' }}</el-descriptions-item>
          <el-descriptions-item label="来源交换机">{{ detail.sourceExchange || '-' }}</el-descriptions-item>
          <el-descriptions-item label="来源路由键">{{ detail.sourceRoutingKey || '-' }}</el-descriptions-item>
          <el-descriptions-item label="死信队列">{{ detail.deadQueue || '-' }}</el-descriptions-item>
          <el-descriptions-item label="死信交换机">{{ detail.deadExchange || '-' }}</el-descriptions-item>
          <el-descriptions-item label="死信路由键">{{ detail.deadRoutingKey || '-' }}</el-descriptions-item>
          <el-descriptions-item label="死信原因">{{ detail.deadReason || '-' }}</el-descriptions-item>
          <el-descriptions-item label="最近错误" :span="2">{{ detail.lastError || '-' }}</el-descriptions-item>
          <el-descriptions-item label="最后重试">{{ detail.lastRetryTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ detail.createTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ detail.updateTime || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="detail-section">
          <h3>消息体</h3>
          <pre class="json-block">{{ formatJson(detail.messageBody) }}</pre>
        </div>

        <div class="detail-section">
          <h3>消息头</h3>
          <pre class="json-block">{{ formatJson(detail.headers) }}</pre>
        </div>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAdminMqDeadMessageDetail,
  ignoreAdminMqDeadMessage,
  listAdminMqDeadMessages,
  retryAdminMqDeadMessage
} from '@/api/mqDeadMessage'
import type {
  MqDeadMessageBasic,
  MqDeadMessageDetail,
  MqDeadMessagePageQuery,
  MqDeadMessageStatus
} from '@/types/mq'

type DeadMessageQueryForm = Omit<
  MqDeadMessagePageQuery,
  'status' | 'createTimeStart' | 'createTimeEnd'
> & {
  status?: MqDeadMessageStatus | ''
}

const loading = ref(false)
const messages = ref<MqDeadMessageBasic[]>([])
const total = ref(0)
const detailVisible = ref(false)
const detail = ref<MqDeadMessageDetail | null>(null)
const createTimeRange = ref<[string, string] | []>([])
const actionLoadingKey = ref('')

const query = reactive<DeadMessageQueryForm>({
  current: 1,
  size: 10,
  bizType: '',
  bizId: '',
  status: '',
  sourceQueue: ''
})

function buildQuery(): MqDeadMessagePageQuery {
  const [createTimeStart, createTimeEnd] = createTimeRange.value

  // 组装查询参数：空字符串不要传给后端，否则后端可能会把它当成真实查询条件。
  return {
    current: query.current,
    size: query.size,
    bizType: query.bizType?.trim() || undefined,
    bizId: query.bizId?.trim() || undefined,
    status: query.status === '' ? undefined : query.status,
    sourceQueue: query.sourceQueue?.trim() || undefined,
    createTimeStart,
    createTimeEnd
  }
}

async function loadMessages() {
  loading.value = true

  try {
    const page = await listAdminMqDeadMessages(buildQuery())
    messages.value = page.records
    total.value = page.total
    query.current = page.curPage || query.current
    query.size = page.size || query.size
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '异常消息列表加载失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.current = 1
  loadMessages()
}

function handleReset() {
  query.current = 1
  query.size = 10
  query.bizType = ''
  query.bizId = ''
  query.status = ''
  query.sourceQueue = ''
  createTimeRange.value = []
  loadMessages()
}

async function openDetail(message: MqDeadMessageBasic) {
  loading.value = true

  try {
    detail.value = await getAdminMqDeadMessageDetail(message.id)
    detailVisible.value = true
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '异常消息详情加载失败')
  } finally {
    loading.value = false
  }
}

async function retryMessage(message: MqDeadMessageBasic) {
  try {
    await ElMessageBox.confirm(
      '确认重新投递这条消息吗？请先确认业务问题已经修复。',
      '重试异常消息',
      {
        type: 'warning',
        confirmButtonText: '重试',
        cancelButtonText: '取消'
      }
    )

    actionLoadingKey.value = `retry-${message.id}`
    // 人工重试不是直接改业务数据，而是让后端把死信消息重新投递到原业务队列。
    await retryAdminMqDeadMessage(message.id)
    ElMessage.success('已提交重试处理')
    await loadMessages()
  } catch (error) {
    if (error === 'cancel') {
      return
    }
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '异常消息重试失败')
  } finally {
    actionLoadingKey.value = ''
  }
}

async function ignoreMessage(message: MqDeadMessageBasic) {
  try {
    await ElMessageBox.confirm(
      '确认忽略这条消息吗？忽略后表示人工确认不再处理。',
      '忽略异常消息',
      {
        type: 'warning',
        confirmButtonText: '忽略',
        cancelButtonText: '取消'
      }
    )

    actionLoadingKey.value = `ignore-${message.id}`
    await ignoreAdminMqDeadMessage(message.id)
    ElMessage.success('异常消息已忽略')
    await loadMessages()
  } catch (error) {
    if (error === 'cancel') {
      return
    }
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '忽略异常消息失败')
  } finally {
    actionLoadingKey.value = ''
  }
}

function canOperate(status: MqDeadMessageStatus) {
  return status === 0 || status === 3
}

function statusText(status: MqDeadMessageStatus) {
  const map: Record<MqDeadMessageStatus, string> = {
    0: '待处理',
    1: '已重投',
    2: '已忽略',
    3: '重投失败'
  }

  return map[status] || '未知'
}

function statusTagType(status: MqDeadMessageStatus) {
  if (status === 0) {
    return 'warning'
  }

  if (status === 1) {
    return 'success'
  }

  if (status === 3) {
    return 'danger'
  }

  return 'info'
}

function formatJson(value?: string) {
  if (!value) {
    return '-'
  }

  try {
    // 详情里的消息体是后端保存的原始 JSON，格式化一下方便排查问题。
    return JSON.stringify(JSON.parse(value), null, 2)
  } catch {
    return value
  }
}

onMounted(loadMessages)
</script>

<style scoped>
.detail-section {
  margin-top: 18px;
}

.detail-section h3 {
  margin: 0 0 8px;
  color: #1f2a44;
  font-size: 15px;
  font-weight: 700;
}

.json-block {
  max-height: 300px;
  margin: 0;
  padding: 14px;
  overflow: auto;
  color: #1f2a44;
  background: #f7f9fc;
  border: 1px solid #e5eaf3;
  border-radius: 8px;
  font-size: 13px;
  line-height: 1.6;
  white-space: pre-wrap;
  overflow-wrap: anywhere;
}
</style>
