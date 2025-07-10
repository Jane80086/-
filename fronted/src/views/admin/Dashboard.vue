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
      
      <!-- 路由测试按钮 -->
      <div class="route-test">
        <h3>路由测试</h3>
        <el-button @click="testRoute('/admin/course-search')" type="primary">测试课程搜索</el-button>
        <el-button @click="testRoute('/admin/my-courses')" type="success">测试我的课程</el-button>
        <el-button @click="testRoute('/admin/courses')" type="warning">测试课程审核</el-button>
        <el-button @click="testRoute('/admin/users')" type="info">测试用户管理</el-button>
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
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const router = useRouter()

const testRoute = (path) => {
  console.log('测试路由跳转到:', path)
  try {
    router.push(path)
    ElMessage.success(`正在跳转到: ${path}`)
  } catch (error) {
    console.error('路由跳转失败:', error)
    ElMessage.error(`路由跳转失败: ${error.message}`)
  }
}
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

.route-test {
  background: #e8f4fd;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.route-test h3 {
  margin-top: 0;
  color: #409eff;
}

.route-test .el-button {
  margin-right: 10px;
  margin-bottom: 10px;
}

.dashboard-content {
  text-align: center;
}

.dashboard-content h2 {
  color: #6D8BA6;
  margin-bottom: 10px;
}
</style>