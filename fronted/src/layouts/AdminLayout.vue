<template>
  <el-container class="admin-layout">
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
      <div class="logo">测盟汇 - 管理员</div>
      <el-input v-model="search" placeholder="搜索菜单" class="menu-search" clearable />
      <el-menu
        :default-active="$route.path"
        class="menu"
        background-color="#F7F9FA"
        text-color="#2D3A4B"
        active-text-color="#6D8BA6"
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
        <div class="header-title">测盟汇 - 管理员控制台</div>
        <div class="header-user">
          <el-avatar :src="user.avatar" size="medium" class="avatar" />
          <el-tag class="role-tag" effect="plain" color="#6D8BA6">超级管理员</el-tag>
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
import { HomeFilled, Document, Collection, Calendar, UserFilled, DataAnalysis, MoreFilled } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

onMounted(() => {
  console.log('=== AdminLayout 组件挂载 ===')
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
  if (path.includes('/dashboard') || path.includes('/home') || path.includes('/users') || path.includes('/roles')) {
    return 'home'
  } else if (path.includes('/news') || path.includes('/courses')) {
    return 'content'
  } else if (path.includes('/meeting') || path.includes('/meetings')) {
    return 'meeting'
  } else if (path.includes('/settings')) {
    return 'more'
  }
  return 'home'
}

const activeSecondary = ref(getActiveSecondary(route.path))

// 监听路由变化，更新 activeSecondary
watch(() => route.path, (newPath) => {
  console.log('AdminLayout 路由变化:', newPath)
  activeSecondary.value = getActiveSecondary(newPath)
  console.log('更新 activeSecondary:', activeSecondary.value)
})

// 管理员专用菜单
const adminMenu = {
  home: [
    { label: '仪表盘', path: '/admin/dashboard', icon: DataAnalysis },
    { label: '用户管理', path: '/admin/users', icon: UserFilled },
    { label: '角色权限', path: '/admin/roles', icon: UserFilled },
  ],
  content: [
    { label: '动态审核', path: '/admin/news', icon: Document },
    { label: '课程审核', path: '/admin/courses', icon: Collection },
  ],
  meeting: [
    { label: '会议审核', path: '/admin/meetings', icon: Calendar },
    { label: '会议统计', path: '/admin/meeting-stats', icon: DataAnalysis },
  ],
  more: [
    { label: '更多设置', path: '/admin/settings', icon: MoreFilled },
  ]
}

const search = ref('')
const filteredPrimaryMenu = computed(() => {
  console.log('=== AdminLayout filteredPrimaryMenu 计算 ===')
  console.log('当前 activeSecondary:', activeSecondary.value)
  
  let menu = adminMenu[activeSecondary.value] || []
  console.log('菜单内容:', menu)
  
  if (!search.value) return menu
  return menu.filter(item => item.label.includes(search.value))
})

const handleSecondaryMenuClick = (key) => {
  console.log('AdminLayout 点击了二级菜单:', key)
  activeSecondary.value = key
  console.log('当前激活的二级菜单:', activeSecondary.value)
}

const handleMenuSelect = (index) => {
  console.log('AdminLayout 菜单项被选中:', index)
  console.log('目标路径:', index)
  
  // 确保路径是管理员路径
  if (!index.startsWith('/admin/')) {
    console.error('管理员尝试访问非管理员路径:', index)
    return
  }
  
  // 执行路由跳转
  router.push(index)
}
</script>

<style scoped>
.admin-layout {
  background: #F7F9FA;
  font-family: 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Arial', sans-serif;
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
  color: #6D8BA6;
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
}
.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 64px;
  font-size: 18px;
  font-weight: bold;
  color: #6D8BA6;
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
  background: #6D8BA6 !important;
  color: #fff !important;
  font-weight: bold;
  box-shadow: 0 2px 8px #6d8ba655;
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
}
.header-title {
  font-size: 20px;
  font-weight: bold;
  color: #6D8BA6;
  letter-spacing: 2px;
}
.header-user {
  display: flex;
  align-items: center;
  gap: 12px;
}
.avatar {
  border: 2px solid #6D8BA6;
}
.role-tag {
  font-weight: bold;
}
.el-dropdown-link {
  color: #2D3A4B;
  font-weight: 500;
  cursor: pointer;
}
.main-content {
  background: #F7F9FA;
  padding: 20px;
}
</style> 