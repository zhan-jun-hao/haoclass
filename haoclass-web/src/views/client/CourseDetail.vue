<template>
  <section v-if="loading" class="detail-page">
    <el-skeleton :rows="10" animated />
  </section>

  <section v-else-if="errorMessage" class="episode-section">
    <el-alert :title="errorMessage" type="error" show-icon :closable="false" />
    <el-button class="retry-button" type="primary" @click="loadCourse">重新加载</el-button>
  </section>

  <section v-else-if="!course" class="episode-section">
    <el-empty description="课程不存在" />
  </section>

  <section v-else class="detail-page">
    <img
      class="detail-cover"
      :src="resolveCourseCover(course)"
      :alt="course.title"
      @error="useFallbackCourseCover"
    />
    <div class="detail-info">
      <span class="tag">{{ course.categoryName }}</span>
      <h1>{{ course.title }}</h1>
      <p>{{ course.summary }}</p>
      <div class="price-line">
        <strong>￥{{ formatPrice(course.price) }}</strong>
        <span>{{ chargeText(course.chargeType) }}</span>
      </div>
      <div class="action-row">
        <el-button type="primary" :loading="ordering" @click="buyCourse">{{ primaryActionText }}</el-button>
        <router-link :to="`/client/courses/${course.id}/watch`">
          <el-button>试看第一集</el-button>
        </router-link>
      </div>
    </div>
  </section>

  <section v-if="course" class="episode-section">
    <h2>课程目录</h2>
    <el-table :data="course.episodes || []" style="width: 100%">
      <el-table-column prop="title" label="标题" />
      <el-table-column label="时长" width="120">
        <template #default="{ row }">{{ formatDuration(row.durationSeconds) }}</template>
      </el-table-column>
    </el-table>
  </section>

  <el-dialog v-model="checkoutVisible" title="确认订单" width="620px" destroy-on-close>
    <div v-if="course" class="checkout-summary">
      <div>
        <strong>{{ course.title }}</strong>
        <span>课程原价</span>
      </div>
      <strong>￥{{ formatPrice(course.price) }}</strong>
    </div>

    <div class="coupon-section">
      <div class="coupon-section-head">
        <strong>选择优惠券</strong>
        <span>{{ availableCoupons.length }} 张可用</span>
      </div>

      <el-skeleton v-if="couponLoading" :rows="3" animated />
      <el-radio-group v-else v-model="selectedCouponId" class="coupon-list">
        <el-radio value="" class="coupon-option">
          <span>不使用优惠券</span>
          <strong>原价购买</strong>
        </el-radio>
        <el-radio
          v-for="coupon in availableCoupons"
          :key="coupon.userCouponId"
          :value="coupon.userCouponId"
          class="coupon-option"
        >
          <span>
            {{ coupon.couponName }}
            <small>满￥{{ formatPrice(coupon.thresholdAmount) }}可用 · {{ coupon.validEndTime }}到期</small>
          </span>
          <strong>-￥{{ formatPrice(coupon.discountAmount) }}</strong>
        </el-radio>
      </el-radio-group>
    </div>

    <div class="checkout-total">
      <span>应付金额</span>
      <strong>￥{{ formatPrice(expectedPayAmount) }}</strong>
    </div>

    <template #footer>
      <el-button @click="checkoutVisible = false">取消</el-button>
      <el-button type="primary" :loading="ordering" @click="submitOrder">提交订单</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCourseDetail } from '@/api/course'
import { resolveCourseCover, useFallbackCourseCover } from '@/utils/courseCover'
import {
  createClientOrder,
  listClientOrderAvailableCoupons,
  mockPayClientOrder
} from '@/api/order'
import type { Course } from '@/types/course'
import type { CourseOrderAvailableCoupon } from '@/types/order'

const route = useRoute()
const router = useRouter()
const course = ref<Course | null>(null)
const loading = ref(false)
const ordering = ref(false)
const errorMessage = ref('')
const checkoutVisible = ref(false)
const couponLoading = ref(false)
const availableCoupons = ref<CourseOrderAvailableCoupon[]>([])
const selectedCouponId = ref('')

const primaryActionText = computed(() => {
  if (course.value?.chargeType === 0) {
    return '免费学习'
  }

  return '购买课程'
})

const selectedCoupon = computed(() =>
  availableCoupons.value.find((coupon) => coupon.userCouponId === selectedCouponId.value)
)

const expectedPayAmount = computed(() => selectedCoupon.value?.payAmount ?? course.value?.price ?? 0)

async function loadCourse() {
  loading.value = true
  errorMessage.value = ''

  try {
    course.value = await getCourseDetail(String(route.params.id))
  } catch (error) {
    console.error(error)
    course.value = null
    errorMessage.value = '课程详情加载失败，请确认后端接口 /api/main/client/courses/{id} 是否正常'
    ElMessage.error(errorMessage.value)
  } finally {
    loading.value = false
  }
}

async function buyCourse() {
  if (!course.value) {
    return
  }

  if (course.value.chargeType === 0) {
    router.push(`/client/courses/${course.value.id}/watch`)
    return
  }

  checkoutVisible.value = true
  selectedCouponId.value = ''
  await loadAvailableCoupons()
}

async function loadAvailableCoupons() {
  if (!course.value) {
    return
  }

  couponLoading.value = true
  try {
    availableCoupons.value = await listClientOrderAvailableCoupons(course.value.id)
  } catch (error) {
    console.error(error)
    availableCoupons.value = []
    ElMessage.error(error instanceof Error ? error.message : '可用优惠券加载失败')
  } finally {
    couponLoading.value = false
  }
}

async function submitOrder() {
  if (!course.value) {
    return
  }

  ordering.value = true

  try {
    const order = await createClientOrder({
      courseId: course.value.id,
      userCouponId: selectedCouponId.value || undefined
    })
    checkoutVisible.value = false

    if (order.status === 1) {
      ElMessage.success('课程已开通')
      router.push(`/client/courses/${course.value.id}/watch`)
      return
    }

    await ElMessageBox.confirm(`订单 ${order.orderNo} 已创建，是否立即模拟支付？`, '确认支付', {
      type: 'info',
      confirmButtonText: '模拟支付',
      cancelButtonText: '稍后支付'
    })

    ElMessage.info('已发起支付，正在等待订单履约完成')
    // mockPayClientOrder 内部会等待 main-service 把课程订单改成已支付，避免跳转太早导致播放鉴权失败。
    await mockPayClientOrder(order.id)
    ElMessage.success('支付成功，已开通课程')
    router.push(`/client/courses/${course.value.id}/watch`)
  } catch (error) {
    if (error === 'cancel') {
      return
    }

    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '下单失败')
  } finally {
    ordering.value = false
  }
}

function formatPrice(price: number) {
  return (price / 100).toFixed(2)
}

function formatDuration(seconds?: number) {
  const safeSeconds = seconds ?? 0
  const minutes = Math.floor(safeSeconds / 60)
  const remainSeconds = safeSeconds % 60
  return `${String(minutes).padStart(2, '0')}:${String(remainSeconds).padStart(2, '0')}`
}

function chargeText(chargeType: number) {
  if (chargeType === 0) {
    return '免费课程'
  }

  if (chargeType === 2) {
    return 'VIP免费'
  }

  return '付费课程'
}

onMounted(loadCourse)

watch(() => route.params.id, loadCourse)
</script>

<style scoped>
.checkout-summary,
.coupon-section-head,
.checkout-total {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.checkout-summary {
  padding-bottom: 18px;
  border-bottom: 1px solid #e4e7ed;
}

.checkout-summary div {
  display: grid;
  gap: 6px;
}

.checkout-summary span,
.coupon-section-head span,
.coupon-option small,
.checkout-total span {
  color: #909399;
}

.coupon-section {
  margin-top: 22px;
}

.coupon-list {
  display: grid;
  gap: 10px;
  margin-top: 14px;
}

.coupon-option {
  width: 100%;
  height: auto;
  min-height: 58px;
  margin-right: 0;
  padding: 10px 14px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
}

.coupon-option :deep(.el-radio__label) {
  display: flex;
  flex: 1;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.coupon-option span {
  display: grid;
  gap: 4px;
}

.coupon-option strong,
.checkout-total strong {
  color: #e5484d;
}

.coupon-option small {
  font-size: 12px;
}

.checkout-total {
  margin-top: 22px;
  padding-top: 18px;
  border-top: 1px solid #e4e7ed;
}

.checkout-total strong {
  font-size: 24px;
}
</style>
