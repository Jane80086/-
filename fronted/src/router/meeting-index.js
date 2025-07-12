import { createRouter, createWebHistory } from 'vue-router';
import { useUserStore } from '@/store/user';
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

// 路由守卫
router.beforeEach((to, from, next) => {
  try {
    const userStore = useUserStore();
    const isAuthenticated = userStore.isAuthenticated;
    
    // 记录路由跳转日志
    console.log(`Meeting路由跳转: ${from.path} -> ${to.path}, 认证状态: ${isAuthenticated}`);
    
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
    console.error('Meeting路由守卫错误:', error);
    // 发生错误时清除可能损坏的认证信息
    const userStore = useUserStore();
    userStore.logout();
    next('/login');
  }
});

// 路由错误处理
router.onError((error) => {
  console.error('Meeting路由错误:', error);
  // 可以在这里添加错误上报逻辑
});

export const meetingRoutes = routes;
export default router; 