<template>
  <div class="dashboard">
    <el-card>
      <template #header>
        <span>管理员仪表盘</span>
      </template>
      <div class="debug-info">
        <h3>调试信息</h3>
        <p><strong>当前角色:</strong> {{ userStore.user.role }}</p>
        <p><strong>用户信息:</strong> {{ JSON.stringify(userStore.user, null, 2) }}</p>
        <p><strong>当前路径:</strong> {{ $route.path }}</p>
      </div>
      <div class="dashboard-content">
        <h2>欢迎来到管理员仪表盘</h2>
        <p>这里是管理员专属的仪表盘页面</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.debug-info {
  background: #f5f5f5;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.debug-info h3 {
  margin-top: 0;
  color: #333;
}

.debug-info p {
  margin: 8px 0;
  font-family: monospace;
  font-size: 14px;
}

.dashboard-content {
  text-align: center;
}

.dashboard-content h2 {
  color: #6D8BA6;
  margin-bottom: 10px;
}
</style>