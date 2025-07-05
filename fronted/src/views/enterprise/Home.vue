<template>
  <div class="home">
    <el-card>
      <template #header>
        <span>企业用户首页</span>
      </template>
      <div class="debug-info">
        <h3>调试信息</h3>
        <p><strong>当前角色:</strong> {{ userStore.user.role }}</p>
        <p><strong>用户信息:</strong> {{ JSON.stringify(userStore.user, null, 2) }}</p>
        <p><strong>当前路径:</strong> {{ $route.path }}</p>
      </div>
      <div class="home-content">
        <h2>欢迎来到企业用户首页</h2>
        <p>这里是企业用户专属的首页</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
</script>

<style scoped>
.home {
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

.home-content {
  text-align: center;
}

.home-content h2 {
  color: #A3BCE2;
  margin-bottom: 10px;
}
</style> 