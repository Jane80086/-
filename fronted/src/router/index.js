import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'
import AdminLayout from '@/layouts/AdminLayout.vue'
import EnterpriseLayout from '@/layouts/EnterpriseLayout.vue'
import UserLayout from '@/layouts/UserLayout.vue'
import Dashboard from '../components/Dashboard.vue'
import UserDashboard from '../components/UserDashboard.vue'
import TestConnection from '../components/TestConnection.vue'
import Home from '../views/Home.vue'
import CourseDetail from '../views/CourseDetail.vue'
import CourseEdit from '../views/CourseEdit.vue'
import CourseCreate from '../views/CourseCreate.vue'
import CoursePlayer from '../views/CoursePlayer.vue'
import AIRecommend from '../views/AIRecommend.vue'
import MyCourses from '../views/MyCourses.vue'
import CourseCreateNormal from '../views/CourseCreateNormal.vue'
import CourseCreateEnterprise from '../views/CourseCreateEnterprise.vue'
import CourseManageAdmin from '../views/CourseManageAdmin.vue'
import Placeholder from '../views/Placeholder.vue'

const routes = [
  { path: '/', redirect: (to) => {
      const userStore = useUserStore()
      const role = userStore.user.role
      if (role === 'admin') return '/admin/dashboard'
      if (role === 'enterprise') return '/enterprise/home'
      return '/user/home'
    }
  },
  
  // 重定向旧路径到新的角色基础路径
  { path: '/news', redirect: (to) => {
      const userStore = useUserStore()
      const role = userStore.user.role
      if (role === 'admin') return '/admin/news'
      if (role === 'enterprise') return '/enterprise/news'
      return '/user/news'
    }
  },
  { path: '/news/:id', redirect: (to) => {
      const userStore = useUserStore()
      const role = userStore.user.role
      if (role === 'admin') return `/admin/news/${to.params.id}`
      if (role === 'enterprise') return `/enterprise/news/${to.params.id}`
      return `/user/news/${to.params.id}`
    }
  },
  { path: '/course', redirect: (to) => {
      const userStore = useUserStore()
      const role = userStore.user.role
      if (role === 'admin') return '/admin/course'
      if (role === 'enterprise') return '/enterprise/course'
      return '/user/course'
    }
  },
  { path: '/course/:id', redirect: (to) => {
      const userStore = useUserStore()
      const role = userStore.user.role
      if (role === 'admin') return `/admin/course/${to.params.id}`
      if (role === 'enterprise') return `/enterprise/course/${to.params.id}`
      return `/user/course/${to.params.id}`
    }
  },
  { path: '/meeting', redirect: (to) => {
      const userStore = useUserStore()
      const role = userStore.user.role
      if (role === 'admin') return '/admin/meeting'
      if (role === 'enterprise') return '/enterprise/meeting'
      return '/user/meeting'
    }
  },
  { path: '/meeting/:id', redirect: (to) => {
      const userStore = useUserStore()
      const role = userStore.user.role
      if (role === 'admin') return `/admin/meeting/${to.params.id}`
      if (role === 'enterprise') return `/enterprise/meeting/${to.params.id}`
      return `/user/meeting/${to.params.id}`
    }
  },
  
  // 用户主路由 - 使用 UserLayout
  {
    path: '/user',
    component: UserLayout,
    redirect: '/user/home',
    children: [
      { path: 'home', component: () => import('@/views/user/Home.vue') },
      { path: 'news', component: () => import('@/views/user/NewsList.vue') },
      { path: 'news/:id', component: () => import('@/views/user/NewsDetail.vue') },
      { path: 'course', component: () => import('@/views/user/CourseList.vue') },
      { path: 'course/:id', component: () => import('@/views/user/CourseDetail.vue') },
      { path: 'meeting', component: () => import('@/views/user/MeetingDetailView.vue') },
      { path: 'meeting/:id', component: () => import('@/views/user/MeetingDetailView.vue') },
      { path: 'meeting/stats', component: () => import('@/views/user/ReviewView.vue') },
      { path: 'profile', component: () => import('@/views/user/Profile.vue') },
      // 其他user页面可继续添加
    ]
  },
  // 企业主路由 - 使用 EnterpriseLayout
  {
    path: '/enterprise',
    component: EnterpriseLayout,
    redirect: '/enterprise/home',
    children: [
      { path: 'home', component: () => import('@/views/enterprise/Home.vue') },
      { path: 'dashboard', component: () => import('@/views/enterprise/Dashboard.vue') },
      { path: 'employees', component: () => import('@/views/enterprise/Employees.vue') },
      { path: 'news', component: () => import('@/views/enterprise/NewsList.vue') },
      { path: 'news/:id', component: () => import('@/views/enterprise/NewsDetail.vue') },
      { path: 'course', component: () => import('@/views/enterprise/CourseList.vue') },
      { path: 'course/:id', component: () => import('@/views/enterprise/CourseDetail.vue') },
      { path: 'my-courses', component: () => import('@/views/enterprise/MyCourses.vue') },
      { path: 'meeting', component: () => import('@/views/enterprise/MeetingDetailView.vue') },
      { path: 'meeting/:id', component: () => import('@/views/enterprise/MeetingDetailView.vue') },
      { path: 'meeting-stats', component: () => import('@/views/enterprise/ReviewView.vue') },
      { path: 'settings', component: () => import('@/views/enterprise/Settings.vue') },
      // 其他enterprise页面可继续添加
    ]
  },
  // 管理员主路由 - 使用 AdminLayout
  {
    path: '/admin',
    component: AdminLayout,
    redirect: '/admin/dashboard',
    children: [
      { path: 'dashboard', component: () => import('@/views/admin/Dashboard.vue') },
      { path: 'users', component: () => import('@/views/admin/UserManage.vue') },
      { path: 'roles', component: () => import('@/views/admin/RoleManage.vue') },
      { path: 'news', component: () => import('@/views/admin/NewsAudit.vue') },
      { path: 'news/:id', component: () => import('@/views/admin/NewsDetail.vue') },
      { path: 'courses', component: () => import('@/views/admin/CourseAudit.vue') },
      { path: 'course', component: () => import('@/views/admin/CourseList.vue') },
      { path: 'course/:id', component: () => import('@/views/admin/CourseDetail.vue') },
      { path: 'meetings', component: () => import('@/views/admin/MeetingAudit.vue') },
      { path: 'meeting', component: () => import('@/views/admin/MeetingDetailView.vue') },
      { path: 'meeting/:id', component: () => import('@/views/admin/MeetingDetailView.vue') },
      { path: 'meeting-stats', component: () => import('@/views/admin/ReviewView.vue') },
      { path: 'settings', component: () => import('@/views/admin/Settings.vue') },
      // 其他admin页面可继续添加
    ]
  },
  
  // 通用路由
  { path: '/dashboard', name: 'Dashboard', component: Dashboard },
  { path: '/user-dashboard', name: 'UserDashboard', component: UserDashboard },
  { path: '/test', name: 'TestConnection', component: TestConnection },
  { path: '/home', name: 'Home', component: Home },
  { path: '/course/:id/play', name: 'CoursePlayer', component: CoursePlayer },
  { path: '/course/:id/edit', name: 'CourseEdit', component: CourseEdit },
  { path: '/create', name: 'CourseCreate', component: CourseCreate },
  { path: '/ai-recommend', name: 'AIRecommend', component: AIRecommend },
  { path: '/my-courses', name: 'MyCourses', component: MyCourses },
  { path: '/create-normal', name: 'CourseCreateNormal', component: CourseCreateNormal },
  { path: '/create-enterprise', name: 'CourseCreateEnterprise', component: CourseCreateEnterprise },
  { path: '/manage-admin', name: 'CourseManageAdmin', component: CourseManageAdmin },
  
  // 普通用户功能路由
  { path: '/learning-progress', name: 'LearningProgress', component: () => import('../views/LearningProgress.vue') },
  { path: '/favorites', name: 'Favorites', component: () => import('../views/Favorites.vue') },
  { path: '/help', name: 'Help', component: () => import('../views/Help.vue') },
  
  // 企业用户功能路由
  { path: '/enterprise/training', component: Placeholder },
  { path: '/enterprise/courses', component: Placeholder },
  { path: '/enterprise/create-course', component: Placeholder },
  { path: '/enterprise/analytics', component: Placeholder },
  { path: '/enterprise/reports', component: Placeholder },
  { path: '/enterprise/performance', component: Placeholder },
  { path: '/enterprise/ai-insights', component: Placeholder },
  { path: '/enterprise/billing', component: Placeholder },
  { path: '/enterprise/support', component: Placeholder },
  
  // 管理员功能路由
  { path: '/admin/user-analytics', name: 'AdminUserAnalytics', component: Placeholder },
  { path: '/admin/permissions', name: 'AdminPermissions', component: Placeholder },
  { path: '/admin/course-analytics', name: 'AdminCourseAnalytics', component: Placeholder },
  { path: '/admin/categories', name: 'AdminCategories', component: Placeholder },
  { path: '/admin/system-health', name: 'AdminSystemHealth', component: Placeholder },
  { path: '/admin/performance', name: 'AdminPerformance', component: Placeholder },
  { path: '/admin/logs', name: 'AdminLogs', component: Placeholder },
  { path: '/admin/ai-config', name: 'AdminAIConfig', component: Placeholder },
  { path: '/admin/backup', name: 'AdminBackup', component: Placeholder },
  
  // 通用功能路由
  { path: '/course/search', name: 'CourseSearch', component: Placeholder },
  { path: '/personalized', name: 'Personalized', component: Placeholder },
  { path: '/trending', name: 'Trending', component: Placeholder },
  { path: '/login', component: () => import('@/views/RegisterAndLogin/Login.vue') },
  { path: '/register', component: () => import('@/views/RegisterAndLogin/Register.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫，开发时允许直接访问三种首页
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const role = userStore.user.role
  console.log('=== 路由守卫调试信息 ===')
  console.log('当前角色:', role)
  console.log('目标路径:', to.path)
  console.log('来源路径:', from.path)
  console.log('用户信息:', userStore.user)
  console.log('=======================')
  
  // 只在访问 / 时做角色跳转，其他页面允许直接访问
  if (to.path === '/') {
    if (role === 'admin') return next('/admin/dashboard')
    if (role === 'enterprise') return next('/enterprise/home')
    return next('/user/home')
  }
  next()
})

export default router 