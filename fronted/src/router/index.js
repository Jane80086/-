import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'
import EnterpriseLayout from '@/layouts/EnterpriseLayout.vue'
import CourseSearch from '@/views/enterprise/CourseSearch.vue'
import TestPage from '@/views/enterprise/TestPage.vue'
import { newsRoutes } from './news-index.js'
import { registerRoutes } from './register_index.js'

const routes = [
  {
    path: '/',
    redirect: () => {
      const userStore = useUserStore()
      const role = userStore.user?.role
      if (role === 'admin') return '/admin/dashboard'
      if (role === 'enterprise') return '/enterprise/home'
      return '/user/home'
    }
  },
  {
    path: '/login',
    component: () => import('@/views/RegisterAndLogin/Login.vue')
  },
  {
    path: '/test',
    component: () => import('@/views/TestPage.vue')
  },
  {
    path: '/debug',
    component: () => import('@/views/DebugPage.vue')
  },
  {
    path: '/simple-test',
    component: () => import('@/views/SimpleTest.vue')
  },
  {
    path: '/register',
    component: () => import('@/views/RegisterAndLogin/Register.vue')
  },
  {
    path: '/user',
    component: () => import('@/layouts/UserLayout.vue'),
    redirect: '/user/home',
    children: [
      { path: 'home', component: () => import('@/views/user/Home.vue') },
      { path: 'news', component: () => import('@/views/user/NewsList.vue') },
      { path: 'news/:id', component: () => import('@/views/user/NewsDetail.vue') },
      { path: 'course', component: () => import('@/views/user/CourseList.vue') },
      { path: 'course/:id', component: () => import('@/views/user/CourseDetail.vue') },
      { path: 'course/:id/play', component: () => import('@/views/user/CoursePlay.vue') },
      { path: 'my-courses', component: () => import('@/views/user/MyCourses.vue') },
      { path: 'meeting', component: () => import('@/views/Meeting-HomeView.vue') },
      { path: 'meeting/:id', component: () => import('@/views/MeetingDetailView.vue') },
      { path: 'meeting/stats', component: () => import('@/views/ReviewView.vue') },
      { path: 'profile', component: () => import('@/views/user/Profile.vue') },
    ]
  },
  {
    path: '/enterprise',
    component: EnterpriseLayout,
    redirect: '/enterprise/home',
    children: [
      { path: 'home', component: () => import('@/views/enterprise/Home.vue') },
      { path: 'test', component: TestPage },
      { path: 'courses', component: CourseSearch },
      { path: 'my-courses', component: () => import('@/views/enterprise/MyCourses.vue') },
      { path: 'course/:id', component: () => import('@/views/enterprise/CourseDetail.vue') },
      { path: 'course/:id/edit', component: () => import('@/views/enterprise/CourseEdit.vue') },
      { path: 'course/:id/audit', component: () => import('@/views/enterprise/CourseAudit.vue') },
      { path: 'course-list', component: () => import('@/views/enterprise/CourseList.vue') },
      { path: 'course-manage', component: () => import('@/views/enterprise/CourseManage.vue') },
      { path: 'course/:id/play', component: () => import('@/views/user/CoursePlay.vue') },
      // 新闻相关路由
      { path: 'news', component: () => import('@/views/enterprise/NewsList.vue') },
      { path: 'news/publish', component: () => import('@/views/enterprise/NewsPublish.vue') },
      { path: 'news/my', component: () => import('@/views/enterprise/MyNews.vue') },
      { path: 'news/:id', component: () => import('@/views/enterprise/NewsDetail.vue') },
      { path: 'news/:id/edit', component: () => import('@/views/enterprise/NewsEdit.vue') },
      // 会议相关路由
      { path: 'meeting', component: () => import('@/views/Meeting-HomeView.vue') },
      { path: 'meeting/:id', component: () => import('@/views/MeetingDetailView.vue') },
      { path: 'meeting-stats', component: () => import('@/views/ReviewView.vue') },
      // 设置路由
      { path: 'settings', component: () => import('@/views/enterprise/Settings.vue') },
      // 其他路由
      { path: 'dashboard', component: () => import('@/views/enterprise/Dashboard.vue') },
      { path: 'employees', component: () => import('@/views/enterprise/Employees.vue') },
    ]
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    children: [
      { path: 'dashboard', component: () => import('@/views/admin/Dashboard.vue') },
      { path: 'course-search', component: () => import('@/views/admin/CourseSearch.vue') },
      { path: 'my-courses', component: () => import('@/views/admin/MyCourses.vue') },
      { path: 'courses', component: () => import('@/views/admin/CourseAudit.vue') },
      { path: 'course/:id', component: () => import('@/views/admin/CourseDetail.vue') },
      { path: 'course/:id/play', component: () => import('@/views/admin/CoursePlay.vue') },
      { path: 'users', component: () => import('@/views/admin/UserManagement.vue') },
      { path: 'roles', component: () => import('@/views/admin/RoleManagement.vue') },
      { path: 'profile', component: () => import('@/views/admin/Profile.vue') },
      { path: 'settings', component: () => import('@/views/admin/Settings.vue') },
      { path: 'news', component: () => import('@/views/admin/NewsManagement.vue') },
      { path: 'news/publish', component: () => import('@/views/admin/NewsPublish.vue') },
      { path: 'news/:id', component: () => import('@/views/admin/NewsDetail.vue') },
      { path: 'news/:id/edit', component: () => import('@/views/admin/NewsEdit.vue') },
      { path: 'news/audit', component: () => import('@/views/admin/NewsAudit.vue') },
      { path: 'news/stats', component: () => import('@/views/admin/NewsStatistics.vue') },
      { path: 'meeting', component: () => import('@/views/Meeting-HomeView.vue') },
      { path: 'meeting/:id', component: () => import('@/views/MeetingDetailView.vue') },
      { path: 'meeting-stats', component: () => import('@/views/ReviewView.vue') },
      { path: 'enterprise', component: () => import('@/views/admin/EnterpriseManagement.vue') },
      { path: 'ai-chat', component: () => import('@/views/admin/AIChat.vue') }
    ]
  },
  // 添加其他路由配置
  ...newsRoutes,
  ...registerRoutes
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  console.log('=== 路由守卫 ===')
  console.log('目标路径:', to.path)
  console.log('来源路径:', from.path)

  const userStore = useUserStore()
  const role = userStore.user?.role
  console.log('用户角色:', role)

  if (to.path === '/') {
    if (role === 'admin') return next('/admin/dashboard')
    if (role === 'enterprise') return next('/enterprise/home')
    return next('/user/home')
  }

  console.log('路由守卫通过，继续导航')
  next()
})

export default router