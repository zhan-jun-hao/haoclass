<template>
  <section class="episode-section coupon-page">
    <div class="section-head">
      <div>
        <h1>优惠券</h1>
        <p>领取优惠券，并查看自己的优惠券状态</p>
      </div>
      <el-button :loading="refreshing" @click="refreshCurrentTab">刷新</el-button>
    </div>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="领券中心" name="center">
        <el-skeleton v-if="templateLoading" :rows="8" animated />

        <el-empty v-else-if="templates.length === 0" description="暂无可领取优惠券" />

        <div v-else class="coupon-grid">
          <article v-for="coupon in templates" :key="coupon.id" class="coupon-item">
            <div class="coupon-value">
              <span>￥</span>
              <strong>{{ formatPriceNumber(coupon.discountAmount) }}</strong>
            </div>

            <div class="coupon-info">
              <h2>{{ coupon.couponName }}</h2>
              <p>{{ thresholdText(coupon.thresholdAmount) }}</p>
              <time>有效期至 {{ coupon.validEndTime }}</time>
            </div>

            <el-button
              type="primary"
              :disabled="coupon.received"
              :loading="receivingId === coupon.id"
              @click="receiveCoupon(coupon)"
            >
              {{ coupon.received ? '已领取' : '立即领取' }}
            </el-button>
          </article>
        </div>
      </el-tab-pane>

      <el-tab-pane label="我的优惠券" name="mine">
        <el-form class="coupon-filter" :inline="true" @submit.prevent>
          <el-form-item label="优惠券状态">
            <el-select v-model="query.status" clearable placeholder="全部" style="width: 140px">
              <el-option label="未使用" :value="0" />
              <el-option label="已锁定" :value="1" />
              <el-option label="已使用" :value="2" />
              <el-option label="已过期" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>

        <el-table v-loading="myCouponLoading" :data="myCoupons" style="width: 100%">
          <el-table-column prop="couponName" label="优惠券" min-width="180" />
          <el-table-column label="优惠金额" width="120">
            <template #default="{ row }">￥{{ formatPrice(row.discountAmount) }}</template>
          </el-table-column>
          <el-table-column label="使用门槛" width="140">
            <template #default="{ row }">{{ thresholdText(row.thresholdAmount) }}</template>
          </el-table-column>
          <el-table-column label="状态" width="110">
            <template #default="{ row }">
              <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="validStartTime" label="生效时间" width="170" />
          <el-table-column prop="validEndTime" label="失效时间" width="170" />
          <el-table-column prop="createTime" label="领取时间" width="170" />
        </el-table>

        <div v-if="total > 0" class="coupon-pagination">
          <el-pagination
            v-model:current-page="query.current"
            v-model:page-size="query.size"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handlePageSizeChange"
            @current-change="loadMyCoupons"
          />
        </div>
      </el-tab-pane>
    </el-tabs>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  listClientCouponTemplates,
  listClientMyCoupons,
  receiveClientCouponTemplate
} from '@/api/coupon'
import type {
  ClientCouponTemplate,
  ClientMyCoupon,
  ClientMyCouponPageQuery,
  UserCouponStatus
} from '@/types/coupon'

type MyCouponQueryForm = Omit<ClientMyCouponPageQuery, 'status'> & {
  status?: UserCouponStatus | ''
}

const activeTab = ref('center')
const templateLoading = ref(false)
const myCouponLoading = ref(false)
const receivingId = ref('')
const templates = ref<ClientCouponTemplate[]>([])
const myCoupons = ref<ClientMyCoupon[]>([])
const total = ref(0)

const query = reactive<MyCouponQueryForm>({
  current: 1,
  size: 10,
  status: ''
})

const refreshing = computed(() =>
  activeTab.value === 'center' ? templateLoading.value : myCouponLoading.value
)

function buildMyCouponQuery(): ClientMyCouponPageQuery {
  return {
    current: query.current,
    size: query.size,
    status: query.status === '' ? undefined : query.status
  }
}

async function loadTemplates() {
  templateLoading.value = true
  try {
    templates.value = await listClientCouponTemplates()
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '可领取优惠券加载失败')
  } finally {
    templateLoading.value = false
  }
}

async function loadMyCoupons() {
  myCouponLoading.value = true
  try {
    const page = await listClientMyCoupons(buildMyCouponQuery())
    myCoupons.value = page.records
    total.value = page.total
    query.current = page.curPage || query.current
    query.size = page.size || query.size
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '我的优惠券加载失败')
  } finally {
    myCouponLoading.value = false
  }
}

async function receiveCoupon(coupon: ClientCouponTemplate) {
  if (coupon.received) {
    return
  }

  receivingId.value = coupon.id
  try {
    await receiveClientCouponTemplate(coupon.id)
    coupon.received = true
    ElMessage.success('优惠券领取成功')
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '优惠券领取失败')
  } finally {
    receivingId.value = ''
  }
}

function handleTabChange(tabName: string | number) {
  if (tabName === 'mine') {
    loadMyCoupons()
  }
}

function refreshCurrentTab() {
  if (activeTab.value === 'mine') {
    loadMyCoupons()
    return
  }
  loadTemplates()
}

function handleSearch() {
  query.current = 1
  loadMyCoupons()
}

function handleReset() {
  query.current = 1
  query.size = 10
  query.status = ''
  loadMyCoupons()
}

function handlePageSizeChange() {
  query.current = 1
  loadMyCoupons()
}

function formatPrice(price?: number) {
  return ((price || 0) / 100).toFixed(2)
}

function formatPriceNumber(price?: number) {
  const value = (price || 0) / 100
  return Number.isInteger(value) ? value.toFixed(0) : value.toFixed(2)
}

function thresholdText(thresholdAmount?: number) {
  return thresholdAmount ? `满￥${formatPrice(thresholdAmount)}可用` : '无门槛'
}

function statusText(status: UserCouponStatus) {
  const map: Record<UserCouponStatus, string> = {
    0: '未使用',
    1: '已锁定',
    2: '已使用',
    3: '已过期'
  }
  return map[status] || '未知'
}

function statusTagType(status: UserCouponStatus) {
  if (status === 0) {
    return 'success'
  }
  if (status === 1) {
    return 'warning'
  }
  return 'info'
}

onMounted(loadTemplates)
</script>

<style scoped>
.coupon-page .section-head h1 {
  margin: 0 0 6px;
}

.coupon-page .section-head p {
  margin: 0;
  color: #6b7280;
}

.coupon-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
  padding: 8px 0;
}

.coupon-item {
  display: grid;
  grid-template-columns: 110px minmax(0, 1fr) auto;
  gap: 18px;
  align-items: center;
  padding: 18px;
  border: 1px solid #e5e7eb;
  border-left: 4px solid #e5484d;
  border-radius: 8px;
  background: #fff;
}

.coupon-value {
  color: #e5484d;
  white-space: nowrap;
}

.coupon-value span {
  font-size: 16px;
}

.coupon-value strong {
  font-size: 34px;
}

.coupon-info {
  min-width: 0;
}

.coupon-info h2 {
  overflow: hidden;
  margin: 0 0 8px;
  color: #1f2937;
  font-size: 17px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.coupon-info p,
.coupon-info time {
  display: block;
  margin: 0;
  color: #6b7280;
  font-size: 13px;
}

.coupon-info time {
  margin-top: 6px;
}

.coupon-filter {
  margin-bottom: 16px;
  padding: 14px 16px 0;
  border: 1px solid #e5e7eb;
  background: #f8fafc;
}

.coupon-pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

@media (max-width: 900px) {
  .coupon-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 620px) {
  .coupon-item {
    grid-template-columns: 90px minmax(0, 1fr);
  }

  .coupon-item :deep(.el-button) {
    grid-column: 1 / -1;
    width: 100%;
  }

  .coupon-filter :deep(.el-form-item),
  .coupon-filter :deep(.el-select) {
    width: 100% !important;
  }

  .coupon-pagination {
    justify-content: center;
    overflow-x: auto;
  }
}
</style>
