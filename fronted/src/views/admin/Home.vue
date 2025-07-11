<template>
  <div class="admin-home">
    <el-card class="welcome-card">
      <template #header>
        <div class="card-header">
          <h2>欢迎来到管理员控制台</h2>
        </div>
      </template>
      <div class="welcome-content">
        <p>您已成功登录管理员账户</p>
        <p>正在跳转到仪表盘...</p>
        <el-button type="primary" @click="goToDashboard">立即前往仪表盘</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const goToDashboard = () => {
  router.push('/admin/dashboard')
}

onMounted(() => {
  // 自动跳转到仪表盘
  setTimeout(() => {
    router.push('/admin/dashboard')
  }, 2000)
})
</script>

<style scoped>
.admin-home {
  padding: 20px;
  min-height: 100vh;
  background: #f5f5f5;
}

.welcome-card {
  max-width: 600px;
  margin: 100px auto;
  text-align: center;
}

.card-header h2 {
  margin: 0;
  color: #409eff;
}

.welcome-content {
  padding: 20px 0;
}

.welcome-content p {
  margin: 10px 0;
  font-size: 16px;
  color: #666;
}

.welcome-content .el-button {
  margin-top: 20px;
}
</style> 