<template>
  <div class="user-dashboard">
    <!-- 用户信息头部 -->
    <el-card class="user-header">
      <div class="user-info">
        <el-avatar :src="userInfo.avatar" :size="80" />
        <div class="user-details">
          <h2>{{ userInfo.name || userInfo.username }}</h2>
          <p class="user-type">{{ getUserTypeText(userInfo.userType) }}</p>
          <p class="user-email">{{ userInfo.email }}</p>
        </div>
        <div class="user-stats">
          <div class="stat-item">
            <div class="stat-number">{{ userStats.courseCount || 0 }}</div>
            <div class="stat-label">我的课程</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ userStats.learningHours || 0 }}</div>
            <div class="stat-label">学习时长</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ userStats.completionRate || 0 }}%</div>
            <div class="stat-label">完成率</div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 功能模块区域 -->
    <el-row :gutter="20" class="dashboard-content">
      <!-- 普通用户功能 -->
      <template v-if="userInfo.userType === 'NORMAL'">
        <el-col :span="8">
          <el-card class="feature-card">
            <template #header>
              <div class="card-header">
                <el-icon><VideoPlay /></el-icon>
                <span>我的学习</span>
              </div>
            </template>
            <div class="feature-content">
              <el-button type="primary" @click="navigateTo('/my-courses')" style="width: 100%; margin-bottom: 10px;">
                我的课程
              </el-button>
              <el-button @click="navigateTo('/learning-progress')" style="width: 100%; margin-bottom: 10px;">
                学习进度
              </el-button>
              <el-button @click="navigateTo('/favorites')" style="width: 100%;">
                我的收藏
              </el-button>
            </div>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card class="feature-card">
            <template #header>
              <div class="card-header">
                <el-icon><Star /></el-icon>
                <span>AI推荐</span>
              </div>
            </template>
            <div class="feature-content">
              <el-button type="success" @click="navigateTo('/ai-recommend')" style="width: 100%; margin-bottom: 10px;">
                智能推荐
              </el-button>
              <el-button @click="navigateTo('/personalized')" style="width: 100%; margin-bottom: 10px;">
                个性化课程
              </el-button>
              <el-button @click="navigateTo('/trending')" style="width: 100%;">
                热门趋势
              </el-button>
            </div>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card class="feature-card">
            <template #header>
              <div class="card-header">
                <el-icon><User /></el-icon>
                <span>个人中心</span>
              </div>
            </template>
            <div class="feature-content">
              <el-button @click="navigateTo('/profile')" style="width: 100%; margin-bottom: 10px;">
                个人资料
              </el-button>
              <el-button @click="navigateTo('/settings')" style="width: 100%; margin-bottom: 10px;">
                设置
              </el-button>
              <el-button @click="navigateTo('/help')" style="width: 100%;">
                帮助中心
              </el-button>
            </div>
          </el-card>
        </el-col>
      </template>

      <!-- 企业用户功能 -->
      <template v-if="userInfo.userType === 'ENTERPRISE'">
        <el-col :span="6">
          <el-card class="feature-card">
            <template #header>
              <div class="card-header">
                <el-icon><OfficeBuilding /></el-icon>
                <span>企业管理</span>
              </div>
            </template>
            <div class="feature-content">
              <el-button type="primary" @click="navigateTo('/enterprise/dashboard')" style="width: 100%; margin-bottom: 10px;">
                企业仪表盘
              </el-button>
              <el-button @click="navigateTo('/enterprise/employees')" style="width: 100%; margin-bottom: 10px;">
                员工管理
              </el-button>
              <el-button @click="navigateTo('/enterprise/training')" style="width: 100%;">
                培训计划
              </el-button>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card class="feature-card">
            <template #header>
              <div class="card-header">
                <el-icon><VideoPlay /></el-icon>
                <span>课程管理</span>
              </div>
            </template>
            <div class="feature-content">
              <el-button type="success" @click="navigateTo('/enterprise/courses')" style="width: 100%; margin-bottom: 10px;">
                企业课程
              </el-button>
              <el-button @click="navigateTo('/enterprise/create-course')" style="width: 100%; margin-bottom: 10px;">
                创建课程
              </el-button>
              <el-button @click="navigateTo('/enterprise/analytics')" style="width: 100%;">
                学习分析
              </el-button>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card class="feature-card">
            <template #header>
              <div class="card-header">
                <el-icon><TrendCharts /></el-icon>
                <span>数据分析</span>
              </div>
            </template>
            <div class="feature-content">
              <el-button @click="navigateTo('/enterprise/reports')" style="width: 100%; margin-bottom: 10px;">
                学习报告
              </el-button>
              <el-button @click="navigateTo('/enterprise/performance')" style="width: 100%; margin-bottom: 10px;">
                绩效分析
              </el-button>
              <el-button @click="navigateTo('/enterprise/ai-insights')" style="width: 100%;">
                AI洞察
              </el-button>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card class="feature-card">
            <template #header>
              <div class="card-header">
                <el-icon><Setting /></el-icon>
                <span>企业设置</span>
              </div>
            </template>
            <div class="feature-content">
              <el-button @click="navigateTo('/enterprise/settings')" style="width: 100%; margin-bottom: 10px;">
                企业配置
              </el-button>
              <el-button @click="navigateTo('/enterprise/billing')" style="width: 100%; margin-bottom: 10px;">
                账单管理
              </el-button>
              <el-button @click="navigateTo('/enterprise/support')" style="width: 100%;">
                企业支持
              </el-button>
            </div>
          </el-card>
        </el-col>
      </template>

      <!-- 超级管理员功能 -->
      <template v-if="userInfo.userType === 'ADMIN'">
        <el-col :span="6">
          <el-card class="feature-card admin-card">
            <template #header>
              <div class="card-header">
                <el-icon><UserFilled /></el-icon>
                <span>用户管理</span>
              </div>
            </template>
            <div class="feature-content">
              <el-button type="primary" @click="navigateTo('/admin/users')" style="width: 100%; margin-bottom: 10px;">
                用户列表
              </el-button>
              <el-button @click="navigateTo('/admin/user-analytics')" style="width: 100%; margin-bottom: 10px;">
                用户分析
              </el-button>
              <el-button @click="navigateTo('/admin/permissions')" style="width: 100%;">
                权限管理
              </el-button>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card class="feature-card admin-card">
            <template #header>
              <div class="card-header">
                <el-icon><VideoPlay /></el-icon>
                <span>课程管理</span>
              </div>
            </template>
            <div class="feature-content">
              <el-button type="success" @click="navigateTo('/admin/courses')" style="width: 100%; margin-bottom: 10px;">
                课程审核
              </el-button>
              <el-button @click="navigateTo('/admin/course-analytics')" style="width: 100%; margin-bottom: 10px;">
                课程分析
              </el-button>
              <el-button @click="navigateTo('/admin/categories')" style="width: 100%;">
                分类管理
              </el-button>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card class="feature-card admin-card">
            <template #header>
              <div class="card-header">
                <el-icon><TrendCharts /></el-icon>
                <span>系统监控</span>
              </div>
            </template>
            <div class="feature-content">
              <el-button @click="navigateTo('/admin/system-health')" style="width: 100%; margin-bottom: 10px;">
                系统健康
              </el-button>
              <el-button @click="navigateTo('/admin/performance')" style="width: 100%; margin-bottom: 10px;">
                性能监控
              </el-button>
              <el-button @click="navigateTo('/admin/logs')" style="width: 100%;">
                系统日志
              </el-button>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card class="feature-card admin-card">
            <template #header>
              <div class="card-header">
                <el-icon><Setting /></el-icon>
                <span>系统设置</span>
              </div>
            </template>
            <div class="feature-content">
              <el-button @click="navigateTo('/admin/settings')" style="width: 100%; margin-bottom: 10px;">
                系统配置
              </el-button>
              <el-button @click="navigateTo('/admin/ai-config')" style="width: 100%; margin-bottom: 10px;">
                AI配置
              </el-button>
              <el-button @click="navigateTo('/admin/backup')" style="width: 100%;">
                数据备份
              </el-button>
            </div>
          </el-card>
        </el-col>
      </template>
    </el-row>

    <!-- 快速操作区域 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>快速操作</span>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="navigateTo('/course/search')" style="margin-right: 10px;">
              <el-icon><Search /></el-icon>
              搜索课程
            </el-button>
            <el-button type="success" @click="navigateTo('/course/create')" style="margin-right: 10px;">
              <el-icon><Plus /></el-icon>
              创建课程
            </el-button>
            <el-button @click="navigateTo('/test')" style="margin-right: 10px;">
              <el-icon><Connection /></el-icon>
              连接测试
            </el-button>
            <el-button @click="refreshData" :loading="loading">
              <el-icon><Refresh /></el-icon>
              刷新数据
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  VideoPlay, 
  Star, 
  User, 
  OfficeBuilding, 
  TrendCharts, 
  Setting,
  UserFilled,
  Search,
  Plus,
  Connection,
  Refresh
} from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)

// 用户信息（从props或store获取）
const userInfo = ref({
  id: 1,
  username: 'admin',
  name: '管理员',
  email: 'admin@example.com',
  avatar: 'https://randomuser.me/api/portraits/men/32.jpg',
  userType: 'ADMIN' // NORMAL, ENTERPRISE, ADMIN
})

// 用户统计数据
const userStats = ref({
  courseCount: 12,
  learningHours: 156,
  completionRate: 85
})

// 获取用户类型文本
const getUserTypeText = (userType) => {
  const typeMap = {
    'NORMAL': '普通用户',
    'ENTERPRISE': '企业用户',
    'ADMIN': '超级管理员'
  }
  return typeMap[userType] || '未知用户'
}

// 导航到指定页面
const navigateTo = (path) => {
  router.push(path)
}

// 刷新数据
const refreshData = async () => {
  loading.value = true
  try {
    // 这里可以添加刷新数据的逻辑
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('数据刷新成功')
  } catch (error) {
    ElMessage.error('数据刷新失败')
  } finally {
    loading.value = false
  }
}

// 组件挂载时获取用户数据
onMounted(() => {
  // 这里可以从API获取用户信息
})
</script>

<style scoped>
.user-dashboard {
  padding: 20px;
}

.user-header {
  margin-bottom: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-details {
  flex: 1;
}

.user-details h2 {
  margin: 0 0 8px 0;
  color: #333;
}

.user-type {
  margin: 0 0 4px 0;
  color: #409eff;
  font-weight: 500;
}

.user-email {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.user-stats {
  display: flex;
  gap: 30px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}

.dashboard-content {
  margin-bottom: 20px;
}

.feature-card {
  height: 200px;
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.admin-card {
  border: 2px solid #f56c6c;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.feature-content {
  display: flex;
  flex-direction: column;
  height: 120px;
  justify-content: center;
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
</style> 