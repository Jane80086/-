<template>
  <div class="home-container">
    <el-row :gutter="20">
      <!-- 欢迎卡片 -->
      <el-col :span="24">
        <el-card class="welcome-card">
          <div class="welcome-content">
            <div class="welcome-text">
              <h2>欢迎回来，{{ currentUser?.username }}！</h2>
              <p>今天是 {{ currentDate }}，祝您工作愉快！</p>
            </div>
            <div class="welcome-icon">
              <el-icon size="60" color="#409EFF"><Sunny /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 统计卡片 -->
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon user-icon">
              <el-icon size="40"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.userCount }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon enterprise-icon">
              <el-icon size="40"><OfficeBuilding /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.enterpriseCount }}</div>
              <div class="stat-label">企业总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon active-icon">
              <el-icon size="40"><DataLine /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.activeCount }}</div>
              <div class="stat-label">活跃用户</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon sync-icon">
              <el-icon size="40"><Refresh /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.syncCount }}</div>
              <div class="stat-label">同步次数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 快速操作 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>快速操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/dashboard/users')">
              <el-icon><User /></el-icon>
              用户管理
            </el-button>
            <el-button type="success" @click="$router.push('/dashboard/enterprises')">
              <el-icon><OfficeBuilding /></el-icon>
              企业管理
            </el-button>
            <el-button type="warning">
              <el-icon><Refresh /></el-icon>
              同步数据
            </el-button>
            <el-button type="info">
              <el-icon><Setting /></el-icon>
              系统设置
            </el-button>
          </div>
        </el-card>
      </el-col>
      
      <!-- 最近活动 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最近活动</span>
            </div>
          </template>
          <div class="recent-activities">
            <div v-for="activity in recentActivities" :key="activity.id" class="activity-item">
              <div class="activity-icon">
                <el-icon :color="activity.color"><component :is="activity.icon" /></el-icon>
              </div>
              <div class="activity-content">
                <div class="activity-title">{{ activity.title }}</div>
                <div class="activity-time">{{ activity.time }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'

export default {
  name: 'Home',
  setup() {
    const store = useStore()
    
    const currentUser = computed(() => store.getters.currentUser)
    const currentDate = computed(() => {
      return new Date().toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        weekday: 'long'
      })
    })
    
    const stats = ref({
      userCount: 0,
      enterpriseCount: 0,
      activeCount: 0,
      syncCount: 0
    })
    
    const recentActivities = ref([
      {
        id: 1,
        title: '新用户注册',
        time: '2分钟前',
        icon: 'User',
        color: '#409EFF'
      },
      {
        id: 2,
        title: '企业信息同步',
        time: '5分钟前',
        icon: 'Refresh',
        color: '#67C23A'
      },
      {
        id: 3,
        title: '系统更新',
        time: '10分钟前',
        icon: 'Setting',
        color: '#E6A23C'
      }
    ])
    
    const loadStats = async () => {
      // 这里可以调用API获取统计数据
      stats.value = {
        userCount: 1250,
        enterpriseCount: 89,
        activeCount: 456,
        syncCount: 1234
      }
    }
    
    onMounted(() => {
      loadStats()
    })
    
    return {
      currentUser,
      currentDate,
      stats,
      recentActivities
    }
  }
}
</script>

<style scoped>
.home-container {
  padding: 20px;
}

.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-text h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
}

.welcome-text p {
  margin: 0;
  opacity: 0.9;
}

.stat-card {
  text-align: center;
}

.stat-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
}

.stat-icon {
  padding: 15px;
  border-radius: 50%;
  color: white;
}

.user-icon {
  background-color: #409EFF;
}

.enterprise-icon {
  background-color: #67C23A;
}

.active-icon {
  background-color: #E6A23C;
}

.sync-icon {
  background-color: #F56C6C;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 5px;
}

.card-header {
  font-weight: bold;
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.quick-actions .el-button {
  flex: 1;
  min-width: 120px;
}

.activity-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-icon {
  margin-right: 15px;
}

.activity-title {
  font-weight: 500;
  color: #333;
}

.activity-time {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}
</style> 