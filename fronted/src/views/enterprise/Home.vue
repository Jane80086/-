<template>
  <div class="home">
    <el-card>
      <template #header>
        <div class="header-content">
          <span>企业用户首页</span>
          <div class="user-info">
            <span class="welcome-text">欢迎，{{ userStore.user.nickname || userStore.user.phone || '企业用户' }}</span>
          </div>
        </div>
      </template>
      
      <!-- 删除调试信息区域 -->
      
      <div class="home-content">
        <h2>欢迎来到企业用户首页</h2>
        <p>这里是企业用户专属的首页，您可以在这里管理课程、发布新闻等。</p>
        
        <div class="quick-actions">
          <el-row :gutter="20">
            <el-col :span="6">
              <el-card class="action-card" @click="$router.push('/enterprise/courses')">
                <div class="action-icon">📚</div>
                <h3>课程管理</h3>
                <p>管理企业课程</p>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="action-card" @click="$router.push('/enterprise/news')">
                <div class="action-icon">📰</div>
                <h3>新闻管理</h3>
                <p>发布和管理新闻</p>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="action-card" @click="$router.push('/enterprise/meeting')">
                <div class="action-icon">🤝</div>
                <h3>会议管理</h3>
                <p>管理企业会议</p>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="action-card" @click="$router.push('/enterprise/settings')">
                <div class="action-icon">⚙️</div>
                <h3>系统设置</h3>
                <p>企业信息设置</p>
              </el-card>
            </el-col>
          </el-row>
        </div>
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

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.welcome-text {
  color: #666;
  font-size: 14px;
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

.home-content p {
  color: #666;
  margin-bottom: 30px;
}

.quick-actions {
  margin-top: 30px;
}

.action-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  height: 150px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.action-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.action-icon {
  font-size: 40px;
  margin-bottom: 10px;
}

.action-card h3 {
  color: #333;
  margin: 10px 0;
}

.action-card p {
  color: #666;
  font-size: 14px;
  margin: 0;
}
</style> 