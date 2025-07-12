<template>
  <el-container class="main-layout">
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
      <div class="logo">测盟汇</div>
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
        <div class="header-title">测盟汇</div>
        <div class="header-user">
          <el-avatar :src="user.avatar" size="medium" class="avatar" />
          <el-tag class="role-tag" effect="plain" :color="roleColor">{{ roleName }}</el-tag>
          <el-dropdown @command="handleDropdownCommand">
            <span class="el-dropdown-link">
              {{ user.nickname }}<template v-if="user.realName">（{{ user.realName }}）</template>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { HomeFilled, Document, Collection, Calendar, UserFilled, DataAnalysis, MoreFilled } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 确保用户状态正确初始化
onMounted(() => {
  console.log('=== MainLayout 组件挂载 ===')
  console.log('用户信息:', userStore.user)
  console.log('用户角色:', userStore.user.role)
  console.log('当前路由:', route.path)
  
  // 如果用户信息为空，尝试从 localStorage 恢复
  if (!userStore.user.role) {
    console.log('用户角色为空，尝试从 localStorage 恢复')
    userStore.initUser()
    console.log('恢复后的用户信息:', userStore.user)
  }
})

const user = computed(() => userStore.user)
const role = computed(() => user.value.role)
const roleName = computed(() => role.value === 'admin' ? '超级管理员' : role.value === 'enterprise' ? '企业用户' : '普通用户')
const roleColor = computed(() =>
  role.value === 'admin' ? '#6D8BA6' : role.value === 'enterprise' ? '#A3BCE2' : '#B7AFA3'
)

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
  } else if (path.includes('/news') || path.includes('/courses') || path.includes('/my-courses')) {
    return 'content'
  } else if (path.includes('/meeting') || path.includes('/meetings')) {
    return 'meeting'
  } else if (path.includes('/settings') || path.includes('/profile')) {
    return 'more'
  }
  return 'home'
}

const activeSecondary = ref(getActiveSecondary(route.path))

// 监听路由变化，更新 activeSecondary
watch(() => route.path, (newPath) => {
  console.log('路由变化:', newPath)
  activeSecondary.value = getActiveSecondary(newPath)
  console.log('更新 activeSecondary:', activeSecondary.value)
})

// Primary sidebar menu分组
const primaryMenu = computed(() => {
  console.log('=== primaryMenu 计算开始 ===')
  console.log('当前角色:', role.value)
  console.log('角色类型:', typeof role.value)
  console.log('角色值:', role.value)
  
  let result
  if (role.value === 'admin') {
    console.log('进入 admin 分支')
    result = {
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
        { label: '会议管理', path: '/admin/meeting', icon: Calendar },
        { label: '会议统计', path: '/admin/meeting-stats', icon: DataAnalysis },
      ],
      more: [
        { label: '更多设置', path: '/admin/settings', icon: MoreFilled },
      ]
    }
  } else if (role.value === 'enterprise') {
    console.log('进入 enterprise 分支')
    result = {
      home: [
        { label: '企业首页', path: '/enterprise/home', icon: HomeFilled },
        { label: '我的课程', path: '/enterprise/my-courses', icon: Collection },
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
  } else {
    console.log('进入 user 分支')
    result = {
      home: [
        { label: '首页', path: '/user/home', icon: HomeFilled },
        { label: '行业动态', path: '/user/news', icon: Document },
      ],
      content: [
        { label: '课程管理', path: '/user/course', icon: Collection },
      ],
      meeting: [
        { label: '会议管理', path: '/user/meeting', icon: Calendar },
        { label: '会议统计', path: '/user/meeting/stats', icon: DataAnalysis },
      ],
      more: [
        { label: '个人设置', path: '/user/profile', icon: MoreFilled },
      ]
    }
  }
  
  console.log('返回的菜单结构:', result)
  console.log('=== primaryMenu 计算结束 ===')
  return result
})

const search = ref('')
const filteredPrimaryMenu = computed(() => {
  console.log('=== filteredPrimaryMenu 计算开始 ===')
  console.log('当前角色:', role.value)
  console.log('当前 activeSecondary:', activeSecondary.value)
  console.log('primaryMenu.value:', primaryMenu.value)
  console.log('primaryMenu.value[activeSecondary.value]:', primaryMenu.value[activeSecondary.value])
  
  let menu = primaryMenu.value[activeSecondary.value] || []
  console.log('计算菜单项 - 当前二级菜单:', activeSecondary.value)
  console.log('计算菜单项 - 当前角色:', role.value)
  console.log('计算菜单项 - 菜单内容:', menu)
  console.log('=== filteredPrimaryMenu 计算结束 ===')
  
  if (!search.value) return menu
  return menu.filter(item => item.label.includes(search.value))
})

const handleSecondaryMenuClick = (key) => {
  console.log('点击了二级菜单:', key)
  activeSecondary.value = key
  console.log('当前激活的二级菜单:', activeSecondary.value)
  console.log('当前角色:', role.value)
  console.log('当前菜单项:', filteredPrimaryMenu.value)
}

const handleMenuSelect = (index) => {
  console.log('菜单项被选中:', index)
  console.log('当前角色:', role.value)
  console.log('目标路径:', index)
  
  // 确保路径与当前角色匹配
  if (role.value === 'admin' && !index.startsWith('/admin/')) {
    console.error('管理员尝试访问非管理员路径:', index)
    return
  }
  if (role.value === 'enterprise' && !index.startsWith('/enterprise/')) {
    console.error('企业用户尝试访问非企业路径:', index)
    return
  }
  if (role.value === 'user' && !index.startsWith('/user/')) {
    console.error('普通用户尝试访问非用户路径:', index)
    return
  }
  
  // 执行路由跳转
  router.push(index)
}

// 处理下拉菜单命令
const handleDropdownCommand = (command) => {
  console.log('下拉菜单命令:', command)
  
  if (command === 'profile') {
    handleProfile()
  } else if (command === 'logout') {
    handleLogout()
  }
}

// 处理个人中心点击
const handleProfile = () => {
  console.log('点击个人中心')
  // 根据用户角色跳转到对应的个人中心页面
  if (role.value === 'admin') {
    router.push('/admin/profile')
  } else if (role.value === 'enterprise') {
    router.push('/enterprise/profile')
  } else {
    router.push('/user/profile')
  }
}

// 处理退出登录
const handleLogout = async () => {
  try {
    console.log('开始退出登录')
    
    // 显示确认对话框
    await ElMessageBox.confirm(
      '确定要退出登录吗？',
      '退出登录',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    // 调用store中的退出登录方法
    await userStore.logout()
    
    // 显示成功消息
    ElMessage.success('退出登录成功')
    
    // 跳转到登录页面
    router.push('/login')
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('退出登录失败:', error)
      ElMessage.error('退出登录失败，请重试')
    }
  }
}
</script>

<style scoped>
.main-layout {
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
  font-size: 22px;
  font-weight: bold;
  color: #6D8BA6;
  letter-spacing: 3px;
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
  color: #2D3A4B !important;
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
.menu-divider {
  margin: 10px 0;
  border-color: #E3E8EE;
}
.sidebar-footer {
  text-align: center;
  padding: 16px 0 8px 0;
  font-size: 12px;
  color: #888;
  letter-spacing: 1px;
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
  font-size: 22px;
  font-weight: bold;
  color: #2D3A4B;
  letter-spacing: 2px;
}
.header-user {
  display: flex;
  align-items: center;
  gap: 16px;
}
.avatar {
  border: 2px solid #A3BCE2;
  box-shadow: 0 2px 8px #a3bce255;
}
.role-tag {
  font-size: 14px;
  border-radius: 8px;
  padding: 2px 12px;
  color: #2D3A4B;
  border: none;
  background: #F7F9FA;
  font-weight: 500;
}

.el-dropdown-link {
  color: #2D3A4B;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
}

.el-dropdown-link:hover {
  color: #6D8BA6;
}
.main-content {
  padding: 32px;
  min-height: 600px;
  background: #F7F9FA;
  border-radius: 18px;
  box-shadow: 0 2px 12px #e0e0e0;
  margin: 24px;
  transition: box-shadow 0.2s;
}
</style> 