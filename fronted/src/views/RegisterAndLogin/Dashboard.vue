<template>
  <el-container class="dashboard-container">
    <!-- 侧边栏 -->
    <el-aside width="250px" class="sidebar">
      <div class="logo">
        <h2>测盟汇系统</h2>
      </div>
      
      <el-menu
        :default-active="$route.path"
        class="sidebar-menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><House /></el-icon>
          <span>首页</span>
        </el-menu-item>
        
        <el-menu-item index="/dashboard/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        
        <el-menu-item index="/dashboard/enterprises">
          <el-icon><OfficeBuilding /></el-icon>
          <span>企业管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <!-- 主内容区 -->
    <el-container>
      <!-- 顶部导航 -->
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="$route.name !== 'Home'">{{ $route.meta.title || $route.name }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userAvatar">
                {{ currentUser?.account?.charAt(0)?.toUpperCase() }}
              </el-avatar>
              <span class="account">{{ currentUser?.account }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="settings">系统设置</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <!-- 内容区 -->
      <el-main class="main-content">
        <div class="ai-analysis-bar" v-if="analysisText">
          <el-icon style="color:#67c23a;"><i class="el-icon-data-analysis"></i></el-icon>
          <span class="ai-analysis-text">{{ analysisText }}</span>
        </div>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessageBox, ElMessage } from 'element-plus'
import { getAIReportAnalysis } from '../api/index.js'

const router = useRouter()
const store = useStore()

const currentUser = computed(() => store.getters.currentUser)
const userAvatar = computed(() => currentUser.value?.avatar || '')
const analysisText = ref('')

// 示例报表数据（实际应从后端获取）
const reportData = {
  企业本月注册数: 12,
  企业上月注册数: 10,
  用户本月活跃数: 50,
  用户上月活跃数: 40
}

onMounted(async () => {
  try {
    const res = await getAIReportAnalysis(reportData)
    analysisText.value = res.analysis || ''
  } catch (e) {
    analysisText.value = ''
  }
})

const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      // 跳转到系统设置页面
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        // 使用Vuex store的logout方法
        await store.dispatch('logout')
        
        ElMessage.success('退出登录成功')
        router.push('/login')
      } catch (error) {
        if (error !== 'cancel') {
          console.error('退出登录失败:', error)
          ElMessage.error('退出登录失败，请重试')
        }
      }
      break
  }
}
</script>

<style scoped>
.dashboard-container {
  height: 100vh;
}

.sidebar {
  background-color: #304156;
  color: #bfcbd9;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b2f3a;
  color: #fff;
}

.logo h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.sidebar-menu {
  border: none;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f5f5;
}

.account {
  margin: 0 8px;
  color: #333;
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
}

.ai-analysis-bar {
  background: #e8f5e9;
  border-left: 4px solid #67c23a;
  padding: 12px 18px;
  margin-bottom: 18px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  color: #388e3c;
  box-shadow: 0 2px 8px rgba(103, 194, 58, 0.08);
}
.ai-analysis-text {
  font-weight: 500;
}
</style>