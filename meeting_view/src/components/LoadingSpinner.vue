<template>
  <div v-if="show" class="loading-container" :class="{ overlay: overlay }">
    <div class="loading-content">
      <div class="spinner" :class="{ 'spinner-large': large }">
        <div class="spinner-ring"></div>
        <div class="spinner-ring"></div>
        <div class="spinner-ring"></div>
      </div>
      <div class="loading-text">
        <p v-if="message">{{ message }}</p>
        <p v-else-if="isTimeout" class="timeout-message">
          加载时间较长，请耐心等待...
        </p>
        <p v-else>加载中...</p>
        <div v-if="showProgress && progress > 0" class="progress-bar">
          <div class="progress-fill" :style="{ width: progress + '%' }"></div>
        </div>
      </div>
      <div v-if="isTimeout && allowCancel" class="loading-actions">
        <button @click="handleCancel" class="cancel-btn">取消</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted } from 'vue';

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  message: {
    type: String,
    default: ''
  },
  overlay: {
    type: Boolean,
    default: true
  },
  large: {
    type: Boolean,
    default: false
  },
  timeout: {
    type: Number,
    default: 30000 // 30秒超时
  },
  allowCancel: {
    type: Boolean,
    default: true
  },
  showProgress: {
    type: Boolean,
    default: false
  },
  progress: {
    type: Number,
    default: 0
  }
});

const emit = defineEmits(['cancel', 'timeout']);

const isTimeout = ref(false);
const timeoutId = ref(null);

// 监听show属性变化
watch(() => props.show, (newVal) => {
  if (newVal) {
    startTimeout();
  } else {
    clearTimeout();
    isTimeout.value = false;
  }
});

const startTimeout = () => {
  if (props.timeout > 0) {
    timeoutId.value = setTimeout(() => {
      isTimeout.value = true;
      emit('timeout');
    }, props.timeout);
  }
};

const clearTimeout = () => {
  if (timeoutId.value) {
    clearTimeout(timeoutId.value);
    timeoutId.value = null;
  }
};

const handleCancel = () => {
  clearTimeout();
  emit('cancel');
};

// 组件卸载时清理定时器
onUnmounted(() => {
  clearTimeout();
});
</script>

<style scoped>
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.loading-container.overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 9999;
  backdrop-filter: blur(2px);
}

.loading-content {
  text-align: center;
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  min-width: 200px;
}

.spinner {
  display: inline-block;
  position: relative;
  width: 40px;
  height: 40px;
  margin-bottom: 20px;
}

.spinner-large {
  width: 60px;
  height: 60px;
}

.spinner-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  border: 3px solid transparent;
  border-top-color: #3498db;
  border-radius: 50%;
  animation: spin 1.2s linear infinite;
}

.spinner-ring:nth-child(2) {
  border-top-color: #e74c3c;
  animation-delay: -0.4s;
}

.spinner-ring:nth-child(3) {
  border-top-color: #f39c12;
  animation-delay: -0.8s;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  margin-bottom: 15px;
}

.loading-text p {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 14px;
}

.timeout-message {
  color: #e74c3c !important;
  font-weight: 500;
}

.progress-bar {
  width: 100%;
  height: 4px;
  background-color: #ecf0f1;
  border-radius: 2px;
  overflow: hidden;
  margin-top: 10px;
}

.progress-fill {
  height: 100%;
  background-color: #3498db;
  border-radius: 2px;
  transition: width 0.3s ease;
}

.loading-actions {
  margin-top: 15px;
}

.cancel-btn {
  padding: 8px 16px;
  background-color: #95a5a6;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: background-color 0.2s;
}

.cancel-btn:hover {
  background-color: #7f8c8d;
}

/* 响应式设计 */
@media (max-width: 600px) {
  .loading-content {
    padding: 20px;
    min-width: 150px;
  }
  
  .spinner {
    width: 30px;
    height: 30px;
  }
  
  .spinner-large {
    width: 40px;
    height: 40px;
  }
}

/* 暗色主题支持 */
@media (prefers-color-scheme: dark) {
  .loading-content {
    background: #2c3e50;
    color: white;
  }
  
  .loading-text p {
    color: #ecf0f1;
  }
  
  .progress-bar {
    background-color: #34495e;
  }
}
</style> 