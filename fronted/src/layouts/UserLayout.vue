<template>
  <el-container class="user-layout">
    <!-- Secondary sidebar -->
    <aside class="secondary-sidebar">
      <div
        v-for="item in secondaryMenu"
        :key="item.key"
        :class="['icon-btn', { active: item.key === activeSecondary }]"
        @click="handleSecondaryMenuClick(item.key)"
      >
        <el-icon :size="28"><component :is="item.icon" /></el-icon>
      </div>
    </aside>
    <!-- Primary sidebar -->
    <el-aside width="220px" class="primary-sidebar">
      <div class="logo">测盟汇 - 个人版</div>
      <el-input v-model="search" placeholder="搜索菜单" class="menu-search" clearable />
      <el-menu
        :default-active="$route.path"
        class="menu"
        background-color="#F7F9FA"
        text-color="#2D3A4B"
        active-text-color="#B7AFA3"
        @select="handleMenuSelect"
      >
        <el-menu-item
          v-for="item in filteredPrimaryMenu"
          :key="item.path"
          :index="item.path"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          {{ item.label }}
          <el-badge v-if="item.badge" :value="item.badge" class="menu-badge" />
        </el-menu-item>
      </el-menu>
    </el-aside>
    <!-- 内容区 -->
    <el-container>
      <el-header class="header">
        <div class="header-title">测盟汇 - 个人中心</div>
        <div class="header-user">
          <el-avatar :src="user.avatar" size="medium" class="avatar" />
          <el-tag class="role-tag" effect="plain" color="#B7AFA3">普通用户</el-tag>
          <el-dropdown>
            <span class="el-dropdown-link">{{ user.nickname }}</span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人中心</el-dropdown-item>
                <el-dropdown-item>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { HomeFilled, Document, Collection, Calendar, UserFilled, DataAnalysis, MoreFilled, Reading } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

onMounted(() => {
  console.log('=== UserLayout 组件挂载 ===')
  console.log('用户信息:', userStore.user)
  console.log('用户角色:', userStore.user.role)
  console.log('当前路由:', route.path)
})

const user = computed(() => userStore.user)

// Secondary sidebar menu
const secondaryMenu = [
  { key: 'home', icon: HomeFilled, label: '首页' },
  { key: 'content', icon: Document, label: '内容' },
  { key: 'meeting', icon: Calendar, label: '会议管理' },
  { key: 'more', icon: MoreFilled, label: '更多' },
]

// 根据当前路由设置 activeSecondary
const getActiveSecondary = (path) => {
  if (path.includes('/home') || path.includes('/news')) {
    return 'home'
  } else if (path.includes('/course') || path.includes('/my-courses')) {
    return 'content'
  } else if (path.includes('/meeting')) {
    return 'meeting'
  } else if (path.includes('/profile')) {
    return 'more'
  }
  return 'home'
}

const activeSecondary = ref(getActiveSecondary(route.path))

// 监听路由变化，更新 activeSecondary
watch(() => route.path, (newPath) => {
  console.log('UserLayout 路由变化:', newPath)
  activeSecondary.value = getActiveSecondary(newPath)
  console.log('更新 activeSecondary:', activeSecondary.value)
})

// 普通用户专用菜单
const userMenu = {
  home: [
    { label: '首页', path: '/user/home', icon: HomeFilled },
    { label: '行业动态', path: '/user/news', icon: Document },
  ],
  content: [
    { label: '课程搜索', path: '/user/course', icon: Collection },
    { label: '我的课程', path: '/user/my-courses', icon: Reading },
  ],
  meeting: [
    { label: '会议管理', path: '/user/meeting', icon: Calendar },
    { label: '会议统计', path: '/user/meeting/stats', icon: DataAnalysis },
  ],
  more: [
    { label: '个人设置', path: '/user/profile', icon: MoreFilled },
  ]
}

const search = ref('')
const filteredPrimaryMenu = computed(() => {
  console.log('=== UserLayout filteredPrimaryMenu 计算 ===')
  console.log('当前 activeSecondary:', activeSecondary.value)
  
  let menu = userMenu[activeSecondary.value] || []
  console.log('菜单内容:', menu)
  
  if (!search.value) return menu
  return menu.filter(item => item.label.includes(search.value))
})

const handleSecondaryMenuClick = (key) => {
  console.log('UserLayout 点击了二级菜单:', key)
  activeSecondary.value = key
  console.log('当前激活的二级菜单:', activeSecondary.value)
}

const handleMenuSelect = (index) => {
  console.log('UserLayout 菜单项被选中:', index)
  console.log('目标路径:', index)
  
  // 确保路径是普通用户路径
  if (!index.startsWith('/user/')) {
    console.error('普通用户尝试访问非用户路径:', index)
    return
  }
  
  // 执行路由跳转
  try {
    router.push(index).catch(err => {
      console.error('路由跳转失败:', err)
      ElMessage.error('页面跳转失败，请稍后重试')
    })
  } catch (error) {
    console.error('路由跳转异常:', error)
    ElMessage.error('页面跳转异常，请稍后重试')
  }
}
</script>

<style scoped>
.user-layout {
  background: #F7F9FA;
  font-family: 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Arial', sans-serif;
  display: flex !important;
  flex-direction: row !important;
  min-width: 0 !important;
}
.secondary-sidebar {
  width: 64px;
  background: #fff;
  border-right: 1px solid #E3E8EE;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 16px;
  box-shadow: 2px 0 8px #E3E8EE22;
  z-index: 2;
  flex: 0 0 64px !important;
  min-width: 64px !important;
  max-width: 64px !important;
}
.icon-btn {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
  cursor: pointer;
  transition: background 0.2s;
  color: #A3BCE2;
}
.icon-btn.active, .icon-btn:hover {
  background: #F0F4F8;
  color: #B7AFA3;
}
.primary-sidebar {
  background: #F7F9FA;
  color: #2D3A4B;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  border-right: 1px solid #E3E8EE;
  min-height: 100vh;
  z-index: 1;
  width: 220px !important;
  min-width: 220px !important;
  max-width: 220px !important;
  box-sizing: border-box;
  flex: 0 0 220px !important;
}
.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 64px;
  font-size: 18px;
  font-weight: bold;
  color: #B7AFA3;
  letter-spacing: 2px;
  margin-bottom: 8px;
}
.menu-search {
  margin: 0 16px 12px 16px;
  border-radius: 8px;
}
.menu {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 16px;
}
.el-menu-item {
  border-radius: 8px !important;
  margin: 6px 8px;
  transition: background 0.2s, color 0.2s, font-weight 0.2s;
  font-size: 16px;
  height: 44px;
  display: flex;
  align-items: center;
}
.el-menu-item.is-active {
  background: #B7AFA3 !important;
  color: #fff !important;
  font-weight: bold;
  box-shadow: 0 2px 8px #b7afa355;
}
.el-menu-item:hover {
  background: #E3E8EE !important;
  color: #2D3A4B !important;
}
.menu-badge {
  margin-left: 8px;
}
.header {
  background: #fff;
  box-shadow: 0 2px 12px #e0e0e0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 64px;
  padding: 0 32px;
  border-bottom-left-radius: 16px;
  border-bottom-right-radius: 16px;
  border-top: 1px solid #F3F2F0;
  min-width: 0 !important;
}
.header-title {
  font-size: 20px;
  font-weight: bold;
  color: #B7AFA3;
  letter-spacing: 2px;
}
.header-user {
  display: flex;
  align-items: center;
  gap: 12px;
}
.avatar {
  border: 2px solid #B7AFA3;
}
.role-tag {
  font-weight: bold;
}
.el-dropdown-link {
  color: #2D3A4B;
  font-weight: 500;
  cursor: pointer;
}
.el-container {
  min-width: 0 !important;
}
.el-main {
  min-width: 0 !important;
}
.main-content {
  background: #F7F9FA;
  padding: 20px;
  width: 100%;
  min-width: 0 !important;
  box-sizing: border-box;
}
</style> 
}
.header-user {
  display: flex;
  align-items: center;
  gap: 12px;
}
.avatar {
  border: 2px solid #B7AFA3;
}
.role-tag {
  font-weight: bold;
}
.el-dropdown-link {
  color: #2D3A4B;
  font-weight: 500;
  cursor: pointer;
}
.el-container {
  min-width: 0 !important;
}
.el-main {
  min-width: 0 !important;
}
.main-content {
  background: #F7F9FA;
  padding: 20px;
  width: 100%;
  min-width: 0 !important;
  box-sizing: border-box;
}
</style> 