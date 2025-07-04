<template>
    <div v-if="hasError" class="error-boundary">
      <div class="error-content">
        <div class="error-icon">⚠️</div>
        <h3>出现了一些问题</h3>
        <p>{{ errorMessage }}</p>
        <div class="error-details" v-if="showDetails">
          <p><strong>错误详情:</strong></p>
          <pre>{{ errorDetails }}</pre>
        </div>
        <div class="error-actions">
          <button @click="handleRetry" class="retry-btn" :disabled="isRetrying">
            {{ isRetrying ? '重试中...' : '重试' }}
          </button>
          <button @click="toggleDetails" class="details-btn">
            {{ showDetails ? '隐藏详情' : '显示详情' }}
          </button>
          <button @click="handleReset" class="reset-btn">重置</button>
          <button @click="handleReport" class="report-btn">报告问题</button>
        </div>
      </div>
    </div>
    <slot v-else />
  </template>
  
  <script setup>
  import { ref, onErrorCaptured, onMounted } from 'vue';
  
  const hasError = ref(false);
  const errorMessage = ref('');
  const errorDetails = ref('');
  const error = ref(null);
  const showDetails = ref(false);
  const isRetrying = ref(false);
  const retryCount = ref(0);
  const maxRetries = 3;
  
  // 错误类型映射
  const errorTypeMap = {
    'Network Error': '网络连接失败，请检查网络设置',
    'timeout': '请求超时，请稍后重试',
    'ECONNREFUSED': '无法连接到服务器，请检查服务器状态',
    'ENOTFOUND': '服务器地址不存在',
    'CORS': '跨域请求被拒绝',
    'Unexpected token': '数据格式错误',
    'JSON Parse error': '数据解析失败'
  };
  
  onErrorCaptured((err, instance, info) => {
    console.error('错误边界捕获到错误:', err, info);
    error.value = err;
    hasError.value = true;
    
    // 构建错误详情
    errorDetails.value = JSON.stringify({
      message: err.message,
      stack: err.stack,
      info: info,
      timestamp: new Date().toISOString(),
      url: window.location.href,
      userAgent: navigator.userAgent
    }, null, 2);
    
    // 根据错误类型设置友好的错误信息
    if (err.response?.status) {
      // HTTP状态码错误
      switch (err.response.status) {
        case 401:
          errorMessage.value = '登录已过期，请重新登录';
          // 自动跳转到登录页
          setTimeout(() => {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = '/login';
          }, 2000);
          break;
        case 403:
          errorMessage.value = '权限不足，无法执行此操作';
          break;
        case 404:
          errorMessage.value = '请求的资源不存在';
          break;
        case 422:
          errorMessage.value = '请求参数验证失败，请检查输入';
          break;
        case 429:
          errorMessage.value = '请求过于频繁，请稍后重试';
          break;
        case 500:
          errorMessage.value = '服务器内部错误，请稍后重试';
          break;
        case 502:
          errorMessage.value = '网关错误，请稍后重试';
          break;
        case 503:
          errorMessage.value = '服务暂时不可用，请稍后重试';
          break;
        default:
          errorMessage.value = `服务器错误 (${err.response.status})`;
      }
    } else if (err.message) {
      // 根据错误消息判断类型
      const errorType = Object.keys(errorTypeMap).find(type => 
        err.message.includes(type)
      );
      errorMessage.value = errorType ? errorTypeMap[errorType] : err.message;
    } else {
      errorMessage.value = '发生未知错误，请稍后重试';
    }
    
    // 记录错误到控制台
    console.group('错误边界捕获的错误');
    console.error('错误对象:', err);
    console.error('组件信息:', info);
    console.error('错误详情:', errorDetails.value);
    console.groupEnd();
    
    return false; // 阻止错误继续传播
  });
  
  const handleRetry = async () => {
    if (isRetrying.value || retryCount.value >= maxRetries) {
      return;
    }
    
    isRetrying.value = true;
    retryCount.value++;
    
    try {
      // 等待一段时间后重试
      await new Promise(resolve => setTimeout(resolve, 1000 * retryCount.value));
      
      // 触发父组件的重试逻辑
      emit('retry');
      
      // 如果重试成功，重置错误状态
      setTimeout(() => {
        if (!hasError.value) {
          retryCount.value = 0;
        }
      }, 1000);
      
    } catch (retryError) {
      console.error('重试失败:', retryError);
      if (retryCount.value >= maxRetries) {
        errorMessage.value = '重试次数已达上限，请检查网络连接或联系管理员';
      }
    } finally {
      isRetrying.value = false;
    }
  };
  
  const handleReset = () => {
    // 清除本地存储
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    
    // 重置状态
    hasError.value = false;
    errorMessage.value = '';
    errorDetails.value = '';
    error.value = null;
    showDetails.value = false;
    isRetrying.value = false;
    retryCount.value = 0;
    
    // 重新加载页面
    window.location.reload();
  };
  
  const toggleDetails = () => {
    showDetails.value = !showDetails.value;
  };
  
  const handleReport = () => {
    // 构建错误报告
    const report = {
      error: errorDetails.value,
      userAgent: navigator.userAgent,
      url: window.location.href,
      timestamp: new Date().toISOString(),
      retryCount: retryCount.value
    };
    
    // 这里可以发送错误报告到服务器或显示给用户
    console.log('错误报告:', report);
    
    // 复制错误报告到剪贴板
    navigator.clipboard.writeText(JSON.stringify(report, null, 2)).then(() => {
      alert('错误报告已复制到剪贴板，请联系管理员并提供此信息');
    }).catch(() => {
      alert('无法复制错误报告，请手动记录错误信息');
    });
  };
  
  // 监听全局错误
  onMounted(() => {
    window.addEventListener('unhandledrejection', (event) => {
      console.error('未处理的Promise拒绝:', event.reason);
      // 可以在这里处理未捕获的Promise错误
    });
    
    window.addEventListener('error', (event) => {
      console.error('全局错误:', event.error);
      // 可以在这里处理全局JavaScript错误
    });
  });
  
  const emit = defineEmits(['retry']);
  </script>
  
  <style scoped>
  .error-boundary {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 300px;
    padding: 20px;
    background-color: #f8f9fa;
    border-radius: 8px;
    margin: 20px;
  }
  
  .error-content {
    text-align: center;
    max-width: 500px;
    background: white;
    padding: 30px;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  }
  
  .error-icon {
    font-size: 48px;
    margin-bottom: 20px;
  }
  
  .error-content h3 {
    color: #e74c3c;
    margin-bottom: 15px;
    font-size: 20px;
  }
  
  .error-content p {
    color: #666;
    margin-bottom: 20px;
    line-height: 1.5;
  }
  
  .error-details {
    margin: 20px 0;
    text-align: left;
    background: #f8f9fa;
    padding: 15px;
    border-radius: 4px;
    border-left: 4px solid #e74c3c;
  }
  
  .error-details pre {
    font-size: 12px;
    color: #666;
    white-space: pre-wrap;
    word-break: break-all;
    margin: 10px 0 0 0;
  }
  
  .error-actions {
    display: flex;
    gap: 10px;
    justify-content: center;
    flex-wrap: wrap;
  }
  
  .retry-btn, .details-btn, .reset-btn, .report-btn {
    padding: 10px 16px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
    transition: background-color 0.2s;
  }
  
  .retry-btn {
    background-color: #3498db;
    color: white;
  }
  
  .retry-btn:hover:not(:disabled) {
    background-color: #2980b9;
  }
  
  .retry-btn:disabled {
    background-color: #bdc3c7;
    cursor: not-allowed;
  }
  
  .details-btn {
    background-color: #95a5a6;
    color: white;
  }
  
  .details-btn:hover {
    background-color: #7f8c8d;
  }
  
  .reset-btn {
    background-color: #e74c3c;
    color: white;
  }
  
  .reset-btn:hover {
    background-color: #c0392b;
  }
  
  .report-btn {
    background-color: #f39c12;
    color: white;
  }
  
  .report-btn:hover {
    background-color: #e67e22;
  }
  
  @media (max-width: 600px) {
    .error-actions {
      flex-direction: column;
    }
    
    .error-content {
      padding: 20px;
    }
  }
  </style> 