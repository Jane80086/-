<template>
  <div v-if="error" class="error-handler">
    <el-alert
      title="页面错误"
      type="error"
      :description="error.message"
      show-icon
      :closable="false"
    />
    <div class="error-actions">
      <el-button @click="reload" type="primary">重新加载</el-button>
      <el-button @click="goHome" type="default">返回首页</el-button>
    </div>
  </div>
  <slot v-else />
</template>

<script setup>
import { ref, onErrorCaptured } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const error = ref(null)

onErrorCaptured((err, instance, info) => {
  console.error('ErrorHandler 捕获到错误:', err)
  console.error('错误信息:', info)
  error.value = err
  return false
})

const reload = () => {
  window.location.reload()
}

const goHome = () => {
  router.push('/')
}
</script>

<style scoped>
.error-handler {
  padding: 20px;
  text-align: center;
}
.error-actions {
  margin: 20px 0;
}
.error-actions .el-button {
  margin: 0 10px;
}
</style> 