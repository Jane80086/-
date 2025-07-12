import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '../views/Meeting-HomeView.vue'; 
import MeetingDetailView from '../views/MeetingDetailView.vue';
import ReviewView from '../views/ReviewView.vue';
 
const routes = [
  {
    path: '/meeting',
    name: 'meeting-home',
    component: HomeView,
    meta: { requiresAuth: true }
  },
  {
    path: '/meeting/:id',
    name: 'meeting-detail',
    component: MeetingDetailView,
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/review',
    name: 'review',
    component: ReviewView,
    meta: { requiresAuth: true }
  },
  {
    path: '/meeting-stats',
    name: 'meeting-stats',
    component: ReviewView,
    meta: { requiresAuth: true }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    redirect: '/meeting'
  }
];
 
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL), 
  routes 
});

// 验证token是否有效
const isValidToken = (token) => {
  if (!token) return false;
  
  try {
    // 简单的token格式验证
    if (typeof token !== 'string' || token.trim() === '') {
      return false;
    }
    
    // 检查token是否过期（这里可以添加JWT解码逻辑）
    // 暂时只做基本格式验证
    return token.length > 10;
  } catch (error) {
    console.error('Token验证失败:', error);
    return false;
  }
};

// 路由守卫
router.beforeEach((to, from, next) => {
  try {
    const token = localStorage.getItem('token');
    const isAuthenticated = isValidToken(token);
    
    // 记录路由跳转日志
    console.log(`路由跳转: ${from.path} -> ${to.path}, 认证状态: ${isAuthenticated}`);
    
    if (to.meta.requiresAuth && !isAuthenticated) {
      console.log('需要认证但未登录，重定向到登录页');
      next('/login');
    } else if (to.name === 'login' && isAuthenticated) {
      console.log('已登录用户访问登录页，重定向到首页');
      next('/meeting');
    } else {
      next();
    }
  } catch (error) {
    console.error('路由守卫错误:', error);
    // 发生错误时清除可能损坏的认证信息
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    next('/login');
  }
});

// 路由错误处理
router.onError((error) => {
  console.error('路由错误:', error);
  // 可以在这里添加错误上报逻辑
});

export const meetingRoutes = routes;
export default router; 