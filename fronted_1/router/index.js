import { createRouter, createWebHistory } from 'vue-router'
import store from '@/store'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/enterprise-register',
    component: () => import('@/views/EnterpriseRegister.vue')
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/Home.vue')
      },
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('@/views/UserManagement.vue')
      },
      {
        path: 'enterprises',
        name: 'EnterpriseManagement',
        component: () => import('@/views/EnterpriseManagement.vue')
      }
    ]
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { requiresAuth: true, title: '个人信息' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 每次跳转前自动同步 token
  const token = localStorage.getItem('token')
  if (token && !store.state.token) {
    store.commit('SET_TOKEN', token)
    console.log('路由守卫 - 从localStorage同步token:', token)
  }
  
  const isAuthenticated = store.getters.isAuthenticated
  const currentToken = store.state.token
  const currentUser = store.state.user
  
  console.log('路由守卫 - 详细信息:')
  console.log('  要去的页面:', to.path)
  console.log('  从哪个页面来:', from.path)
  console.log('  当前token:', currentToken)
  console.log('  当前user:', currentUser)
  console.log('  isAuthenticated:', isAuthenticated)
  console.log('  localStorage中的token:', localStorage.getItem('token'))
  
  if (to.meta.requiresAuth && !isAuthenticated) {
    console.log('路由守卫 - 用户未登录，重定向到登录页面')
    next('/login')
  } else if ((to.path === '/login' || to.path === '/register') && isAuthenticated) {
    console.log('路由守卫 - 用户已登录，重定向到仪表盘')
    next('/dashboard')
  } else {
    console.log('路由守卫 - 路由守卫通过，继续访问')
    next()
  }
})

export default router 