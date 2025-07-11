import axios from 'axios'
import { ElMessage } from 'element-plus'
import store from '@/store'
import router from '@/router'
import { useUserStore } from '@/store/user'

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 优先从 store 取，没有就从 localStorage 取
    let token = ''
    if (store && store.state && store.state.token) {
      token = store.state.token
    }
    if (!token && typeof localStorage !== 'undefined') {
      token = localStorage.getItem('token')
    }
    if (token) {
      const trimmedToken = token.trim();
      config.headers.Authorization = `Bearer ${trimmedToken}`;
      // 打印即将发送的 Authorization 头
      console.log('发送请求 Authorization:', config.headers.Authorization);
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    return response
  },
  error => {
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 401:
          ElMessage.error('登录已过期，请重新登录')
          // 在需要登出的地方使用
          const userStore = useUserStore()
          userStore.logout && userStore.logout()
          router.push('/login')
          break
        case 403:
          ElMessage.error('没有权限访问')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(data.message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }
    if (error.response && error.response.status === 401) {
      ElMessage.error('登录状态失效，请重新登录');
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error)
  }
)

// API模块
const auth = {
  login: (credentials) => api.post('/login', credentials),
  register: (userData) => api.post('/register', userData),
  getCaptcha: () => '/auth/captcha?' + Date.now()
}

export const user = {
  getCurrentUser: () => api.get('/user/current'),
  getUsers: (params) => api.get('/admin/users', { params }),
  createUser: (userData) => api.post('/admin/users', userData),
  updateUser: (id, userData) => api.put(`/admin/users/${id}`, userData),
  deleteUser: (id) => api.delete(`/admin/users/${id}`),
  updateCurrentUser: (userData) => api.put('/user/current', userData),
  exportUsers: (params) => api.get('/admin/users/export', { 
    params, 
    responseType: 'blob' 
  }),
  getUserHistory: (params) => api.get('/admin/users/history', { params }),
  restoreUserHistory: (historyId) => api.post(`/admin/users/history/${historyId}/restore`)
}

const enterprise = {
  getEnterprises: (params) => api.get('/enterprise/list', { params }),
  createEnterprise: (enterpriseData) => api.post('/enterprise/create', enterpriseData),
  updateEnterprise: (id, enterpriseData) => api.put(`/enterprise/${id}`, enterpriseData),
  deleteEnterprise: (id) => api.delete(`/enterprise/${id}`),
  exportEnterprises: (params) => api.get('/enterprise/export', { 
    params, 
    responseType: 'blob' 
  }),
  syncEnterpriseInfo: (enterpriseName) => api.get('/login/syncEnterpriseInfo', { params: { enterpriseName } }),
  syncEnterpriseInfoById: (enterpriseId) => {
    console.log('API层收到参数 enterpriseId:', enterpriseId)
    // axios会自动将params拼接到URL
    return api.get('/login/syncEnterpriseInfoById', { params: { enterpriseId } })
  }
}

export function askAI(question) {
  return fetch('/api/ai/chat', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ message: question }),
  })
    .then(res => res.json())
    .then(res => {
      // 兼容原有 answer 字段
      if (res && res.code === 200 && res.data && res.data.response) {
        return { answer: res.data.response };
      } else {
        return { answer: res?.message || 'AI无回复' };
      }
    });
}

export function getAIWelcome(userInfo) {
  return fetch('/api/ai/welcome', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(userInfo)
  }).then(res => res.json());
}

export function getAIReportAnalysis(reportData) {
  return fetch('/api/ai/report-analysis', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(reportData)
  }).then(res => res.json());
}

export default {
  auth,
  user,
  enterprise
} 