// 注册登录相关路由配置
export const registerRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/register/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/enterprise-register',
    name: 'EnterpriseRegister',
    component: () => import('@/views/register/EnterpriseRegister.vue'),
    meta: { requiresAuth: false }
  }
] 