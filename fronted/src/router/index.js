import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'
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
      // 动态根据角色跳转
      const userStore = useUserStore()
      const role = userStore.user.role
      if (role === 'admin') return '/admin/dashboard'
      if (role === 'enterprise') return '/enterprise/home'
      return '/user/home'
    }
  },
  { path: '/admin/dashboard', component: () => import('@/views/admin/Dashboard.vue') },
  { path: '/admin/users', component: () => import('@/views/admin/UserManage.vue') },
  { path: '/admin/roles', component: () => import('@/views/admin/RoleManage.vue') },
  { path: '/admin/news', component: () => import('@/views/admin/NewsAudit.vue') },
  { path: '/admin/courses', component: () => import('@/views/admin/CourseAudit.vue') },
  { path: '/admin/meetings', component: () => import('@/views/admin/MeetingAudit.vue') },
  { path: '/', name: 'Dashboard', component: Dashboard },
  { path: '/dashboard', name: 'Dashboard', component: Dashboard },
  { path: '/user-dashboard', name: 'UserDashboard', component: UserDashboard },
  { path: '/test', name: 'TestConnection', component: TestConnection },
  { path: '/home', name: 'Home', component: Home },
  { path: '/course/:id', name: 'CourseDetail', component: CourseDetail },
  { path: '/course/:id/play', name: 'CoursePlayer', component: CoursePlayer },
  { path: '/course/:id/edit', name: 'CourseEdit', component: CourseEdit },
  { path: '/create', name: 'CourseCreate', component: CourseCreate },
  { path: '/ai-recommend', name: 'AIRecommend', component: AIRecommend },
  { path: '/my-courses', name: 'MyCourses', component: MyCourses },
  { path: '/create-normal', name: 'CourseCreateNormal', component: CourseCreateNormal },
  { path: '/create-enterprise', name: 'CourseCreateEnterprise', component: CourseCreateEnterprise },
  { path: '/manage-admin', name: 'CourseManageAdmin', component: CourseManageAdmin },
  
  // 普通用户路由
  { path: '/learning-progress', name: 'LearningProgress', component: () => import('../views/LearningProgress.vue') },
  { path: '/favorites', name: 'Favorites', component: () => import('../views/Favorites.vue') },
  { path: '/profile', name: 'Profile', component: () => import('../views/Profile.vue') },
  { path: '/settings', name: 'Settings', component: () => import('../views/Settings.vue') },
  { path: '/help', name: 'Help', component: () => import('../views/Help.vue') },
  
  // 企业用户路由优先
  {
    path: '/enterprise',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      { path: '', redirect: '/enterprise/home' },
      { path: 'home', component: () => import('@/views/enterprise/Home.vue') },
      { path: 'dashboard', component: () => import('../views/enterprise/Dashboard.vue') },
      { path: 'employees', component: () => import('../views/enterprise/Employees.vue') },
      { path: 'training', component: Placeholder },
      { path: 'courses', component: Placeholder },
      { path: 'create-course', component: Placeholder },
      { path: 'analytics', component: Placeholder },
      { path: 'reports', component: Placeholder },
      { path: 'performance', component: Placeholder },
      { path: 'ai-insights', component: Placeholder },
      { path: 'settings', component: Placeholder },
      { path: 'billing', component: Placeholder },
      { path: 'support', component: Placeholder },
    ]
  },
  
  // 普通用户和通用页面
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      { path: '', redirect: '/news' },
      { path: 'news', component: () => import('@/views/news/NewsList.vue') },
      { path: 'news/:id', component: () => import('@/views/news/NewsDetail.vue') },
      { path: 'course', component: () => import('@/views/course/CourseList.vue') },
      { path: 'course/:id', component: () => import('@/views/course/CourseDetail.vue') },
      { path: 'meeting', component: () => import('@/views/meeting/MeetingList.vue') },
      { path: 'meeting/:id', component: () => import('@/views/meeting/MeetingDetail.vue') },
      { path: 'profile', component: () => import('@/views/profile/Profile.vue') },
      { path: 'user/home', component: () => import('@/views/user/Home.vue') },
    ]
  },
  
  // 管理员路由
  { path: '/admin/users', name: 'AdminUsers', component: () => import('../views/admin/Users.vue') },
  { path: '/admin/user-analytics', name: 'AdminUserAnalytics', component: Placeholder },
  { path: '/admin/permissions', name: 'AdminPermissions', component: Placeholder },
  { path: '/admin/courses', name: 'AdminCourses', component: () => import('../views/admin/Courses.vue') },
  { path: '/admin/course-analytics', name: 'AdminCourseAnalytics', component: Placeholder },
  { path: '/admin/categories', name: 'AdminCategories', component: Placeholder },
  { path: '/admin/system-health', name: 'AdminSystemHealth', component: Placeholder },
  { path: '/admin/performance', name: 'AdminPerformance', component: Placeholder },
  { path: '/admin/logs', name: 'AdminLogs', component: Placeholder },
  { path: '/admin/settings', name: 'AdminSettings', component: Placeholder },
  { path: '/admin/ai-config', name: 'AdminAIConfig', component: Placeholder },
  { path: '/admin/backup', name: 'AdminBackup', component: Placeholder },
  
  // 通用路由
  { path: '/course/search', name: 'CourseSearch', component: Placeholder },
  { path: '/personalized', name: 'Personalized', component: Placeholder },
  { path: '/trending', name: 'Trending', component: Placeholder },
  { path: '/login', component: () => import('@/views/auth/Login.vue') },
  { path: '/register', component: () => import('@/views/auth/Register.vue') },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    children: [
      { path: 'dashboard', component: () => import('@/views/admin/Dashboard.vue') },
      { path: 'users', component: () => import('@/views/admin/UserManage.vue') },
      { path: 'roles', component: () => import('@/views/admin/RoleManage.vue') },
      { path: 'news', component: () => import('@/views/admin/NewsAudit.vue') },
      { path: 'courses', component: () => import('@/views/admin/CourseAudit.vue') },
      { path: 'meetings', component: () => import('@/views/admin/MeetingAudit.vue') },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫，开发时允许直接访问三种首页
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const role = userStore.user.role
  // 只在访问 / 时做角色跳转，其他页面允许直接访问
  if (to.path === '/') {
    if (role === 'admin') return next('/admin/dashboard')
    if (role === 'enterprise') return next('/enterprise/home')
    return next('/user/home')
  }
  next()
})

export default router 