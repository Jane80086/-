<template>
  <el-container class="enterprise-layout">
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
    <el-aside width="220px !important" class="primary-sidebar">
      <div class="logo">测盟汇 - 企业版</div>
      <el-input v-model="search" placeholder="搜索菜单" class="menu-search" clearable />
      <el-menu
        :default-active="$route.path"
        class="menu"
        background-color="#F7F9FA"
        text-color="#2D3A4B"
        active-text-color="#A3BCE2"
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
        <div class="header-title">测盟汇 - 企业控制台</div>
        <div class="header-user">
          <el-avatar :src="user.avatar" size="medium" class="avatar" />
          <el-tag class="role-tag" effect="plain" color="#A3BCE2">企业用户</el-tag>
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
  console.log('=== EnterpriseLayout 组件挂载 ===')
  console.log('用户信息:', userStore.user)
  console.log('用户角色:', userStore.user.role)
  console.log('当前路由:', route.path)
})

const user = computed(() => userStore.user)

// Secondary sidebar menu
const secondaryMenu = [
  { key: 'content', icon: Document, label: '行业动态' },
  { key: 'home', icon: HomeFilled, label: '课程管理' },
  { key: 'meeting', icon: Calendar, label: '会议管理' },
  { key: 'more', icon: MoreFilled, label: '更多' },
]

// 根据当前路由设置 activeSecondary
const getActiveSecondary = (path) => {
  if (path.includes('/news')) {
    return 'content'
  } else if (
    path.includes('/home') ||
    path.includes('/my-courses') ||
    path.includes('/courses') ||
    path.includes('/course-manage')
  ) {
    return 'home'
  } else if (path.includes('/meeting')) {
    return 'meeting'
  } else if (path.includes('/settings')) {
    return 'more'
  }
  return 'content'
}

const activeSecondary = ref(getActiveSecondary(route.path))

// 监听路由变化，更新 activeSecondary
watch(() => route.path, (newPath) => {
  console.log('EnterpriseLayout 路由变化:', newPath)
  activeSecondary.value = getActiveSecondary(newPath)
  console.log('更新 activeSecondary:', activeSecondary.value)
})

// 企业用户专用菜单
const enterpriseMenu = {
  home: [
    { label: '课程搜索', path: '/enterprise/courses', icon: Collection },
    { label: '我的课程', path: '/enterprise/my-courses', icon: HomeFilled },
  ],
  content: [
    { label: '我的动态', path: '/enterprise/news', icon: Document },
  ],
  meeting: [
    { label: '我的会议', path: '/enterprise/meeting', icon: Calendar },
    { label: '会议统计', path: '/enterprise/meeting-stats', icon: DataAnalysis },
  ],
  more: [
    { label: '企业设置', path: '/enterprise/settings', icon: MoreFilled },
  ]
}

const search = ref('')
const filteredPrimaryMenu = computed(() => {
  console.log('=== EnterpriseLayout filteredPrimaryMenu 计算 ===')
  console.log('当前 activeSecondary:', activeSecondary.value)
  
  let menu = enterpriseMenu[activeSecondary.value] || []
  console.log('菜单内容:', menu)
  
  if (!search.value) return menu
  return menu.filter(item => item.label.includes(search.value))
})

const handleSecondaryMenuClick = (key) => {
  console.log('EnterpriseLayout 点击了二级菜单:', key)
  activeSecondary.value = key
  console.log('当前激活的二级菜单:', activeSecondary.value)
}

const handleMenuSelect = (index) => {
  console.log('EnterpriseLayout 菜单项被选中:', index)
  console.log('目标路径:', index)
  
  // 确保路径是企业用户路径
  if (!index.startsWith('/enterprise/')) {
    console.error('企业用户尝试访问非企业路径:', index)
    return
  }
  
  // 执行路由跳转
  router.push(index)
}
</script>

<style scoped>
.enterprise-layout {
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
  color: #A3BCE2;
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
  color: #A3BCE2;
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
  background: #A3BCE2 !important;
  color: #fff !important;
  font-weight: bold;
  box-shadow: 0 2px 8px #a3bce255;
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
  color: #A3BCE2;
  letter-spacing: 2px;
}
.header-user {
  display: flex;
  align-items: center;
  gap: 12px;
}
.avatar {
  border: 2px solid #A3BCE2;
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
.avatar {
  border: 2px solid #A3BCE2;
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