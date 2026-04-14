<template>
  <div class="app">
    <header class="header">
      <div class="header-content">
        <div class="logo-section">
          <div class="logo">🤖</div>
          <div class="title-section">
            <h1>AI 代码评审平台</h1>
            <p class="subtitle">Code Review Assistant · Powered by DeepSeek</p>
          </div>
        </div>
        <div class="header-actions">
          <button @click="showConfig = !showConfig" class="btn-config" title="功能说明">
            💡
          </button>
        </div>
      </div>
    </header>

    <main class="main">
      <!-- 功能说明 -->
      <div v-if="showConfig" class="config-panel">
        <div class="config-card">
          <h3>💡 可选功能说明</h3>
          <p class="config-hint">以下功能需在 GitHub Actions 中配置环境变量后自动生效：</p>
          <div class="feature-list">
            <div class="feature-item">
              <span class="feature-icon">📦</span>
              <div class="feature-content">
                <strong>GitHub 评审日志</strong>
                <p>将每次评审结果自动推送到指定仓库存档</p>
                <code>GITHUB_REVIEW_LOG_URI</code>
              </div>
            </div>
            <div class="feature-item">
              <span class="feature-icon">💬</span>
              <div class="feature-content">
                <strong>微信通知</strong>
                <p>评审完成后自动推送结果到微信</p>
                <code>WEIXIN_APPID / WEIXIN_SECRET</code>
              </div>
            </div>
            <div class="feature-item">
              <span class="feature-icon">🤖</span>
              <div class="feature-content">
                <strong>自动评审触发</strong>
                <p>代码提交自动触发 AI 评审流程</p>
                <code>通过 GitHub Webhook 实现</code>
              </div>
            </div>
          </div>
          <button @click="showConfig = false" class="btn-close">收起</button>
        </div>
      </div>

      <div class="content-grid">
        <!-- 左侧：评审面板 -->
        <div class="review-section">
          <ReviewPanel @review-complete="handleReviewComplete" />
        </div>

        <!-- 右侧：历史记录 -->
        <div class="history-section">
          <div class="history-card">
            <div class="history-header">
              <h3>📋 评审历史</h3>
              <button @click="clearHistory" class="btn-clear" v-if="history.length > 0">清空</button>
            </div>
            <div v-if="history.length === 0" class="history-empty">
              <p>暂无评审记录</p>
              <p class="hint">完成评审后会显示在这里</p>
            </div>
            <div v-else class="history-list">
              <div 
                v-for="(item, index) in history" 
                :key="index"
                class="history-item"
                :class="{ active: selectedHistory === index }"
                @click="selectedHistory = index; showHistoryDetail(item)"
              >
                <div class="history-meta">
                  <span class="history-score" :class="getScoreClass(item.score)">
                    {{ item.score ? item.score + '分' : '--' }}
                  </span>
                  <span class="history-time">{{ formatTime(item.time) }}</span>
                </div>
                <div class="history-preview">{{ item.preview }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 历史详情弹窗 -->
      <div v-if="historyDetail" class="modal-overlay" @click.self="historyDetail = null">
        <div class="modal-content">
          <div class="modal-header">
            <h3>📋 评审详情</h3>
            <button @click="historyDetail = null" class="btn-close-modal">×</button>
          </div>
          <div class="modal-body" v-html="renderedHistoryDetail"></div>
          <div class="modal-footer">
            <button @click="copyToClipboard" class="btn-action">📋 复制结果</button>
            <button @click="downloadAsMd" class="btn-action">📥 导出 Markdown</button>
          </div>
        </div>
      </div>
    </main>

    <!-- Toast 提示 -->
    <transition name="toast">
      <div v-if="toast.show" class="toast" :class="toast.type">
        {{ toast.message }}
      </div>
    </transition>

    <footer class="footer">
      <p>基于 Spring Boot + Vue 3 构建 · 支持 Docker 部署 · GitHub Actions CI/CD</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import ReviewPanel from './components/ReviewPanel.vue'
import { marked } from 'marked'

const showConfig = ref(false)
const history = ref([])
const selectedHistory = ref(-1)
const historyDetail = ref(null)
const toast = ref({ show: false, message: '', type: 'success' })

// 监听 ReviewPanel 的结果，添加到历史
const handleReviewComplete = (result) => {
  if (result.status === 'success') {
    const preview = result.reviewContent 
      ? result.reviewContent.substring(0, 50).replace(/[#*`]/g, '') + '...'
      : '无内容'
    
    history.value.unshift({
      time: new Date(),
      score: result.score,
      content: result.reviewContent,
      preview: preview
    })
    
    // 只保留最近10条
    if (history.value.length > 10) {
      history.value = history.value.slice(0, 10)
    }
  }
}

const renderedHistoryDetail = computed(() => {
  if (!historyDetail.value?.content) return ''
  return marked(historyDetail.value.content)
})

const showHistoryDetail = (item) => {
  historyDetail.value = item
}

const clearHistory = () => {
  history.value = []
  showToast('历史记录已清空', 'info')
}

const formatTime = (date) => {
  const now = new Date()
  const diff = now - date
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  return date.toLocaleDateString('zh-CN')
}

const getScoreClass = (score) => {
  if (!score) return ''
  if (score >= 80) return 'score-high'
  if (score >= 60) return 'score-medium'
  return 'score-low'
}

const copyToClipboard = () => {
  if (!historyDetail.value?.content) return
  navigator.clipboard.writeText(historyDetail.value.content)
    .then(() => showToast('已复制到剪贴板', 'success'))
    .catch(() => showToast('复制失败', 'error'))
}

const downloadAsMd = () => {
  if (!historyDetail.value?.content) return
  const blob = new Blob([historyDetail.value.content], { type: 'text/markdown' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `code-review-${Date.now()}.md`
  a.click()
  URL.revokeObjectURL(url)
  showToast('已下载 Markdown 文件', 'success')
}

const showToast = (message, type = 'success') => {
  toast.value = { show: true, message, type }
  setTimeout(() => {
    toast.value.show = false
  }, 2000)
}
</script>

<style scoped>
.app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 1rem 2rem;
  box-shadow: 0 2px 10px rgba(0,0,0,0.15);
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.logo {
  font-size: 2.5rem;
}

.title-section h1 {
  font-size: 1.5rem;
  margin-bottom: 0.25rem;
}

.subtitle {
  font-size: 0.85rem;
  opacity: 0.85;
}

.header-actions {
  display: flex;
  gap: 0.5rem;
}

.btn-config {
  background: rgba(255,255,255,0.2);
  border: none;
  border-radius: 8px;
  padding: 0.5rem 0.75rem;
  font-size: 1.2rem;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-config:hover {
  background: rgba(255,255,255,0.3);
}

.main {
  flex: 1;
  max-width: 1400px;
  width: 100%;
  margin: 1.5rem auto;
  padding: 0 1.5rem;
}

.config-panel {
  margin-bottom: 1.5rem;
}

.config-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.config-card h3 {
  margin-bottom: 1rem;
  color: #333;
}

.config-hint {
  font-size: 0.85rem;
  color: #888;
  margin: 0.5rem 0 1rem;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.feature-item {
  display: flex;
  gap: 1rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
}

.feature-icon {
  font-size: 1.5rem;
}

.feature-content strong {
  display: block;
  margin-bottom: 0.25rem;
  color: #333;
}

.feature-content p {
  font-size: 0.85rem;
  color: #666;
  margin-bottom: 0.5rem;
}

.feature-content code {
  font-size: 0.8rem;
  background: #e9ecef;
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  color: #667eea;
}

.btn-close {
  background: #f5f5f5;
  border: none;
  border-radius: 6px;
  padding: 0.5rem 1rem;
  cursor: pointer;
  margin-top: 1rem;
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: 1.5rem;
}

@media (max-width: 1024px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
}

.history-section {
  position: sticky;
  top: 1rem;
}

.history-card {
  background: white;
  border-radius: 12px;
  padding: 1.25rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.history-header h3 {
  color: #333;
  font-size: 1rem;
}

.btn-clear {
  background: none;
  border: none;
  color: #999;
  font-size: 0.85rem;
  cursor: pointer;
}

.btn-clear:hover {
  color: #666;
}

.history-empty {
  text-align: center;
  padding: 2rem;
  color: #999;
}

.history-empty .hint {
  font-size: 0.85rem;
  margin-top: 0.5rem;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  max-height: 500px;
  overflow-y: auto;
}

.history-item {
  padding: 0.75rem;
  border-radius: 8px;
  background: #f8f9fa;
  cursor: pointer;
  transition: all 0.2s;
}

.history-item:hover,
.history-item.active {
  background: #e9ecef;
}

.history-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
}

.history-score {
  font-weight: bold;
  font-size: 0.85rem;
  padding: 0.15rem 0.5rem;
  border-radius: 4px;
}

.history-score.score-high {
  background: #d4edda;
  color: #155724;
}

.history-score.score-medium {
  background: #fff3cd;
  color: #856404;
}

.history-score.score-low {
  background: #f8d7da;
  color: #721c24;
}

.history-time {
  font-size: 0.75rem;
  color: #999;
}

.history-preview {
  font-size: 0.85rem;
  color: #666;
  line-height: 1.4;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
}

.modal-content {
  background: white;
  border-radius: 12px;
  max-width: 800px;
  width: 100%;
  max-height: 85vh;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
}

.btn-close-modal {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #999;
}

.modal-body {
  flex: 1;
  overflow-y: auto;
  padding: 1.5rem;
  line-height: 1.8;
}

.modal-footer {
  display: flex;
  gap: 1rem;
  padding: 1rem 1.5rem;
  border-top: 1px solid #eee;
}

.btn-action {
  flex: 1;
  padding: 0.75rem;
  background: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-action:hover {
  background: #eee;
}

.toast {
  position: fixed;
  bottom: 2rem;
  left: 50%;
  transform: translateX(-50%);
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  color: white;
  font-size: 0.95rem;
  z-index: 1001;
}

.toast.success {
  background: #28a745;
}

.toast.error {
  background: #dc3545;
}

.toast.info {
  background: #667eea;
}

.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateX(-50%) translateY(20px);
}

.footer {
  background: #f8f9fa;
  padding: 1rem;
  text-align: center;
  color: #888;
  font-size: 0.85rem;
  margin-top: auto;
}
</style>
