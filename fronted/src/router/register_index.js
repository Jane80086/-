// 注册登录相关路由配置
export const registerRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/RegisterAndLogin/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/RegisterAndLogin/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/enterprise-register',
    name: 'EnterpriseRegister',
    component: () => import('@/views/RegisterAndLogin/EnterpriseRegister.vue'),
    meta: { requiresAuth: false }
  }
] 