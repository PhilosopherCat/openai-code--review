<template>
  <div class="review-panel">
    <!-- 输入区域 -->
    <div class="input-section">
      <h2>📝 提交代码评审</h2>
      <textarea
        v-model="diffContent"
        placeholder="请粘贴 git diff 内容..."
        class="diff-input"
      ></textarea>
      <div class="actions">
        <button @click="submitReview" :disabled="loading || !diffContent.trim()" class="btn-primary">
          {{ loading ? '评审中...' : '🚀 开始评审' }}
        </button>
        <button @click="clearAll" class="btn-secondary">清空</button>
      </div>
    </div>

    <!-- 状态显示 -->
    <div v-if="taskId" class="status-section">
      <div class="status-card" :class="statusClass">
        <span class="status-icon">{{ statusIcon }}</span>
        <span class="status-text">{{ statusText }}</span>
        <span v-if="taskId" class="task-id">任务ID: {{ taskId }}</span>
      </div>
    </div>

    <!-- 结果显示 -->
    <div v-if="reviewResult" class="result-section">
      <div class="result-header">
        <h3>📋 评审结果</h3>
        <div v-if="reviewResult.score" class="score-badge" :class="scoreClass">
          评分: {{ reviewResult.score }}/100
        </div>
      </div>
      <div class="result-content" v-html="renderedContent"></div>
    </div>

    <!-- 错误显示 -->
    <div v-if="errorMessage" class="error-section">
      <div class="error-card">
        <span class="error-icon">❌</span>
        <span>{{ errorMessage }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import axios from 'axios'
import { marked } from 'marked'

const diffContent = ref('')
const taskId = ref('')
const loading = ref(false)
const reviewResult = ref(null)
const errorMessage = ref('')
let pollTimer = null

// 提交评审
const submitReview = async () => {
  if (!diffContent.value.trim()) return
  
  loading.value = true
  errorMessage.value = ''
  reviewResult.value = null
  taskId.value = ''

  try {
    const response = await axios.post('/api/review', {
      diffContent: diffContent.value
    })
    taskId.value = response.data.taskId
    pollResult()
  } catch (error) {
    loading.value = false
    errorMessage.value = error.response?.data?.errorMessage || error.message || '提交失败'
  }
}

// 轮询查询结果
const pollResult = () => {
  pollTimer = setInterval(async () => {
    try {
      const response = await axios.get(`/api/review/${taskId.value}`)
      reviewResult.value = response.data
      
      if (response.data.status === 'success' || response.data.status === 'failed') {
        loading.value = false
        clearInterval(pollTimer)
      }
    } catch (error) {
      loading.value = false
      errorMessage.value = '查询结果失败'
      clearInterval(pollTimer)
    }
  }, 2000)
}

// 监听失败状态
watch(() => reviewResult.value?.status, (newStatus) => {
  if (newStatus === 'failed') {
    errorMessage.value = reviewResult.value.errorMessage || '评审失败'
  }
})

// 状态样式
const statusClass = computed(() => {
  const status = reviewResult.value?.status || 'pending'
  return {
    'status-pending': status === 'pending' || status === 'processing',
    'status-success': status === 'success',
    'status-failed': status === 'failed'
  }
})

const statusIcon = computed(() => {
  const status = reviewResult.value?.status || 'pending'
  return {
    'pending': '⏳',
    'processing': '⏳',
    'success': '✅',
    'failed': '❌'
  }[status]
})

const statusText = computed(() => {
  const status = reviewResult.value?.status || 'pending'
  return {
    'pending': '等待处理...',
    'processing': 'AI 评审中...',
    'success': '评审完成',
    'failed': '评审失败'
  }[status]
})

// 评分样式
const scoreClass = computed(() => {
  const score = reviewResult.value?.score
  if (!score) return ''
  if (score >= 80) return 'score-high'
  if (score >= 60) return 'score-medium'
  return 'score-low'
})

// 渲染 Markdown
const renderedContent = computed(() => {
  if (!reviewResult.value?.reviewContent) return ''
  return marked(reviewResult.value.reviewContent)
})

// 清空
const clearAll = () => {
  diffContent.value = ''
  taskId.value = ''
  reviewResult.value = null
  errorMessage.value = ''
  if (pollTimer) clearInterval(pollTimer)
}
</script>

<style scoped>
.review-panel {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.input-section {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.input-section h2 {
  margin-bottom: 1rem;
  color: #333;
}

.diff-input {
  width: 100%;
  height: 200px;
  padding: 1rem;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-family: 'Monaco', 'Menlo', monospace;
  font-size: 13px;
  resize: vertical;
  transition: border-color 0.2s;
}

.diff-input:focus {
  outline: none;
  border-color: #667eea;
}

.actions {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

.btn-primary {
  padding: 0.75rem 2rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: opacity 0.2s, transform 0.2s;
}

.btn-primary:hover:not(:disabled) {
  opacity: 0.9;
  transform: translateY(-1px);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  padding: 0.75rem 1.5rem;
  background: #f5f5f5;
  color: #666;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
}

.btn-secondary:hover {
  background: #eee;
}

.status-section {
  animation: fadeIn 0.3s ease;
}

.status-card {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem 1.5rem;
  border-radius: 8px;
  font-weight: 500;
}

.status-pending {
  background: #fff3cd;
  color: #856404;
}

.status-processing {
  background: #cce5ff;
  color: #004085;
}

.status-success {
  background: #d4edda;
  color: #155724;
}

.status-failed {
  background: #f8d7da;
  color: #721c24;
}

.task-id {
  margin-left: auto;
  font-size: 0.85rem;
  color: #666;
}

.result-section {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  animation: fadeIn 0.5s ease;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.result-header h3 {
  color: #333;
}

.score-badge {
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-weight: bold;
}

.score-high {
  background: #d4edda;
  color: #155724;
}

.score-medium {
  background: #fff3cd;
  color: #856404;
}

.score-low {
  background: #f8d7da;
  color: #721c24;
}

.result-content {
  line-height: 1.8;
  color: #333;
}

.result-content :deep(h1) {
  font-size: 1.5rem;
  margin: 1rem 0;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid #667eea;
}

.result-content :deep(h2) {
  font-size: 1.25rem;
  margin: 1rem 0;
  color: #555;
}

.result-content :deep(h3) {
  font-size: 1.1rem;
  margin: 0.8rem 0;
}

.result-content :deep(code) {
  background: #f4f4f4;
  padding: 0.2rem 0.4rem;
  border-radius: 4px;
  font-family: 'Monaco', 'Menlo', monospace;
  font-size: 0.9em;
}

.result-content :deep(pre) {
  background: #1e1e1e;
  color: #d4d4d4;
  padding: 1rem;
  border-radius: 8px;
  overflow-x: auto;
  margin: 1rem 0;
}

.result-content :deep(pre code) {
  background: none;
  padding: 0;
  color: inherit;
}

.error-section {
  animation: fadeIn 0.3s ease;
}

.error-card {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem 1.5rem;
  background: #f8d7da;
  color: #721c24;
  border-radius: 8px;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
