<template>
  <div class="app">
    <header class="header">
      <h1>🤖 AI 代码评审工具</h1>
      <p class="subtitle">基于 DeepSeek AI 的智能代码评审服务</p>
    </header>
    <main class="main">
      <ReviewPanel />
    </main>
  </div>
</template>

<script setup>
import ReviewPanel from './components/ReviewPanel.vue'
</script>

<style scoped>
.app {
  min-height: 100vh;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 2rem;
  text-align: center;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.header h1 {
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

.subtitle {
  opacity: 0.9;
  font-size: 1rem;
}

.main {
  max-width: 1200px;
  margin: 2rem auto;
  padding: 0 1rem;
}
</style>
