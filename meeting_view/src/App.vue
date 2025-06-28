<script setup>
import { RouterView } from 'vue-router';
import { onErrorCaptured, ref } from 'vue';
import ErrorBoundary from './components/ErrorBoundary.vue';
import LoadingSpinner from './components/LoadingSpinner.vue';

// 全局错误状态
const hasGlobalError = ref(false);
const globalErrorMessage = ref('');

// 全局错误捕获
onErrorCaptured((error, instance, info) => {
  console.error('全局错误捕获:', error, info);
  hasGlobalError.value = true;
  globalErrorMessage.value = error.message || '发生未知错误';
  
  // 可以在这里添加错误上报逻辑
  return false; // 阻止错误继续传播
});

// 处理全局错误恢复
const handleGlobalErrorRecovery = () => {
  hasGlobalError.value = false;
  globalErrorMessage.value = '';
  window.location.reload();
};
</script>
 
<template>
  <div id="app">
    <!-- 全局错误处理 -->
    <div v-if="hasGlobalError" class="global-error">
      <div class="error-content">
        <h2>系统出现错误</h2>
        <p>{{ globalErrorMessage }}</p>
        <button @click="handleGlobalErrorRecovery" class="recovery-btn">
          重新加载页面
        </button>
      </div>
    </div>
    
    <!-- 主要内容 -->
    <main v-else>
      <ErrorBoundary @retry="handleGlobalErrorRecovery">
        <RouterView />
      </ErrorBoundary>
    </main>
  </div>
</template>
 
<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  min-height: 100vh;
}

main {
  padding: 20px;
}

a {
  color: #2c3e50;
  text-decoration: none;
}

a:hover {
  text-decoration: underline;
}

/* 全局错误样式 */
.global-error {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: #f8f9fa;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 10000;
}

.error-content {
  text-align: center;
  max-width: 500px;
  padding: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.error-content h2 {
  color: #e74c3c;
  margin-bottom: 20px;
}

.error-content p {
  color: #666;
  margin-bottom: 30px;
  line-height: 1.6;
}

.recovery-btn {
  background-color: #3498db;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s;
}

.recovery-btn:hover {
  background-color: #2980b9;
}

/* 响应式设计 */
@media (max-width: 768px) {
  main {
    padding: 10px;
  }
  
  .error-content {
    margin: 20px;
    padding: 20px;
  }
}
</style>