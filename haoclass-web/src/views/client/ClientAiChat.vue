<template>
  <section class="episode-section ai-chat-page">
    <div class="section-head">
      <div>
        <h1>AI客服</h1>
        <p>可以咨询课程、订单、支付、退款、优惠券等问题。</p>
      </div>
      <el-button :disabled="sending" @click="clearChat">新会话</el-button>
    </div>

    <div class="chat-shell">
      <div ref="messageListRef" class="message-list">
        <article
          v-for="message in messages"
          :key="message.id"
          class="chat-message"
          :class="`chat-message--${message.role}`"
        >
          <div class="message-role">{{ message.role === 'user' ? '我' : 'AI客服' }}</div>
          <div class="message-content">{{ message.content }}</div>

          <div v-if="message.references?.length" class="reference-list">
            <h3>参考资料</h3>
            <el-collapse>
              <el-collapse-item
                v-for="(reference, index) in message.references"
                :key="`${message.id}-${index}`"
                :title="`片段 ${index + 1} · 相似度 ${formatScore(reference.score)}`"
              >
                <p>{{ reference.text }}</p>
              </el-collapse-item>
            </el-collapse>
          </div>
        </article>

        <el-empty v-if="messages.length === 0" description="录入知识后，可以在这里测试AI客服回答效果" />
      </div>

      <div class="question-panel">
        <div class="quick-questions">
          <el-button
            v-for="question in quickQuestions"
            :key="question"
            size="small"
            :disabled="sending"
            @click="useQuickQuestion(question)"
          >
            {{ question }}
          </el-button>
        </div>

        <el-input
          v-model="question"
          type="textarea"
          :rows="4"
          maxlength="500"
          show-word-limit
          resize="none"
          placeholder="请输入你的问题，例如：退款成功后我还能继续看课程吗？"
          @keydown.enter.exact.prevent="sendQuestion"
        />

        <div class="chat-actions">
          <span v-if="conversationId" class="conversation-id">会话ID：{{ conversationId }}</span>
          <span v-else class="conversation-id">首次提问后会生成会话ID</span>
          <el-button type="primary" :loading="sending" @click="sendQuestion">发送</el-button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { nextTick, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { askClientAi } from '@/api/ai'
import type { ClientAiReference } from '@/types/ai'

type MessageRole = 'user' | 'assistant'

interface ChatMessage {
  id: number
  role: MessageRole
  content: string
  references?: ClientAiReference[]
}

let messageId = 0

const question = ref('')
const sending = ref(false)
const conversationId = ref('')
const messageListRef = ref<HTMLElement>()
const messages = ref<ChatMessage[]>([])

const quickQuestions = [
  '退款成功后还能继续看课程吗？',
  '优惠券下单时怎么使用？',
  '支付成功后为什么课程还没开通？'
]

function useQuickQuestion(value: string) {
  question.value = value
}

function clearChat() {
  question.value = ''
  conversationId.value = ''
  messages.value = []
}

async function sendQuestion() {
  const currentQuestion = question.value.trim()
  if (!currentQuestion) {
    ElMessage.warning('请输入问题')
    return
  }

  messages.value.push({
    id: ++messageId,
    role: 'user',
    content: currentQuestion
  })
  question.value = ''
  await scrollToBottom()

  sending.value = true
  try {
    const result = await askClientAi({
      conversationId: conversationId.value || undefined,
      question: currentQuestion,
      topK: 5
    })

    conversationId.value = result.conversationId
    messages.value.push({
      id: ++messageId,
      role: 'assistant',
      content: result.answer || '暂时没有生成回答',
      references: result.references || []
    })
    await scrollToBottom()
  } catch (error) {
    console.error(error)
    ElMessage.error(error instanceof Error ? error.message : 'AI客服请求失败')
  } finally {
    sending.value = false
  }
}

async function scrollToBottom() {
  await nextTick()
  const el = messageListRef.value
  if (el) {
    el.scrollTop = el.scrollHeight
  }
}

function formatScore(score?: number) {
  if (score === undefined || score === null) {
    return '-'
  }
  return score.toFixed(3)
}
</script>

<style scoped>
.ai-chat-page .section-head h1 {
  margin: 0 0 6px;
}

.ai-chat-page .section-head p {
  margin: 0;
  color: #6b7280;
}

.chat-shell {
  display: grid;
  grid-template-rows: minmax(360px, 58vh) auto;
  gap: 18px;
}

.message-list {
  min-height: 360px;
  overflow-y: auto;
  padding: 18px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #f8fafc;
}

.chat-message {
  max-width: 780px;
  margin-bottom: 16px;
}

.chat-message--user {
  margin-left: auto;
}

.message-role {
  margin-bottom: 6px;
  color: #6b7280;
  font-size: 13px;
}

.chat-message--user .message-role {
  text-align: right;
}

.message-content {
  padding: 14px 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #fff;
  line-height: 1.8;
  white-space: pre-wrap;
  overflow-wrap: anywhere;
}

.chat-message--user .message-content {
  color: #fff;
  background: #1e4fd7;
  border-color: #1e4fd7;
}

.reference-list {
  margin-top: 10px;
}

.reference-list h3 {
  margin: 0 0 8px;
  color: #4b5563;
  font-size: 14px;
}

.reference-list p {
  margin: 0;
  color: #4b5563;
  line-height: 1.8;
  white-space: pre-wrap;
}

.question-panel {
  padding: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #fff;
}

.quick-questions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.chat-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 12px;
}

.conversation-id {
  min-width: 0;
  overflow: hidden;
  color: #6b7280;
  font-size: 13px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

@media (max-width: 700px) {
  .chat-actions {
    align-items: stretch;
    flex-direction: column;
  }

  .chat-actions :deep(.el-button) {
    width: 100%;
  }
}
</style>
