// 注册登录相关路由配置
export const registerRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/RegisterAndLogin/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/role-selection',
    name: 'RoleSelection',
    component: () => import('@/views/RegisterAndLogin/RoleSelection.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/RegisterAndLogin/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register/normal',
    name: 'NormalRegister',
    component: () => import('@/views/RegisterAndLogin/NormalRegister.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register/enterprise',
    name: 'EnterpriseUserRegister',
    component: () => import('@/views/RegisterAndLogin/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register/admin',
    name: 'AdminRegister',
    component: () => import('@/views/RegisterAndLogin/AdminRegister.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/enterprise-register',
    name: 'EnterpriseRegister',
    component: () => import('@/views/RegisterAndLogin/EnterpriseRegister.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/RegisterAndLogin/Dashboard.vue'),
    meta: { requiresAuth: true }
  }
] 