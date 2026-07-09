<template>
  <section class="page-block ai-knowledge-page">
    <div class="section-head">
      <div>
        <h1>AI知识库</h1>
        <p>录入客服知识后，C端AI客服会基于这些资料回答用户问题。</p>
      </div>
      <el-button @click="fillExample">填入示例</el-button>
    </div>

    <el-alert
      class="page-alert"
      type="info"
      :closable="false"
      show-icon
      title="第一版只做文本入库，后续再扩展文件上传、知识列表和版本管理。"
    />

    <el-form ref="formRef" :model="form" :rules="rules" label-width="96px" @submit.prevent>
      <el-form-item label="知识标题" prop="title">
        <el-input v-model="form.title" maxlength="80" show-word-limit placeholder="例如：退款规则" />
      </el-form-item>

      <el-form-item label="知识来源" prop="source">
        <el-input
          v-model="form.source"
          maxlength="80"
          show-word-limit
          placeholder="例如：产品设计书、后台人工录入"
        />
      </el-form-item>

      <el-form-item label="知识正文" prop="content">
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="14"
          maxlength="8000"
          show-word-limit
          placeholder="请输入客服可参考的规则、说明、常见问答等内容"
        />
      </el-form-item>

      <div class="knowledge-actions">
        <el-button @click="resetForm">清空</el-button>
        <el-button type="primary" :loading="submitting" @click="submitKnowledge">提交入库</el-button>
      </div>
    </el-form>

    <el-result
      v-if="lastSegmentCount !== null"
      icon="success"
      title="知识入库成功"
      :sub-title="`本次生成 ${lastSegmentCount} 个知识片段，可以去C端AI客服提问验证。`"
    >
      <template #extra>
        <router-link to="/client/ai-chat">
          <el-button type="primary">去提问验证</el-button>
        </router-link>
      </template>
    </el-result>
  </section>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { ingestAdminAiKnowledge } from '@/api/ai'
import type { AdminAiKnowledgeIngestPayload } from '@/types/ai'

const formRef = ref<FormInstance>()
const submitting = ref(false)
const lastSegmentCount = ref<number | null>(null)

const form = reactive<AdminAiKnowledgeIngestPayload>({
  title: '',
  source: '',
  content: ''
})

const rules: FormRules = {
  title: [{ required: true, message: '请输入知识标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入知识正文', trigger: 'blur' }]
}

function fillExample() {
  form.title = '退款规则'
  form.source = '产品设计书'
  form.content =
    '用户支付成功后可以申请退款。退款成功后，课程订单会变成已退款，课程权益会被回收，课程销量会减少。退款完成后，用户不能继续观看该课程。'
}

function resetForm() {
  form.title = ''
  form.source = ''
  form.content = ''
  lastSegmentCount.value = null
  formRef.value?.clearValidate()
}

async function submitKnowledge() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }

  submitting.value = true
  try {
    const result = await ingestAdminAiKnowledge({
      title: form.title.trim(),
      source: form.source?.trim() || undefined,
      content: form.content.trim()
    })
    lastSegmentCount.value = result.segmentCount
    ElMessage.success('知识入库成功')
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : '知识入库失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.ai-knowledge-page .section-head h1 {
  margin: 0 0 6px;
}

.ai-knowledge-page .section-head p {
  margin: 0;
  color: #6b7280;
}

.knowledge-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
