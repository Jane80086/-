import axios from 'axios'
import { ElMessage } from 'element-plus'
import enterpriseApi from './enterprise'

// 创建axios实例
const request = axios.create({
  baseURL: '/',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    // 兼容文件下载（blob）响应
    if (response.config && response.config.responseType === 'blob') {
      return response;
    }
    const { data } = response;
    // 后端成功状态码是200
    if (data.code === 200) {
      return data;
    } else {
      ElMessage.error(data.msg || '请求失败');
      return Promise.reject(new Error(data.msg || '请求失败'));
    }
  },
  error => {
    if (error.response) {
      const { status, data } = error.response;
      switch (status) {
        case 401:
          ElMessage.error('登录状态失效，请重新登录');
          localStorage.removeItem('token');
          window.location.href = '/login';
          break;
        case 403:
          ElMessage.error('拒绝访问');
          break;
        case 404:
          ElMessage.error('请求的资源不存在');
          break;
        case 500:
          ElMessage.error('服务器内部错误');
          break;
        default:
          ElMessage.error(data?.msg || '网络错误');
      }
    } else {
      ElMessage.error('网络连接失败');
    }
    return Promise.reject(error);
  }
)

// API方法
export const courseAPI = {
  // 获取课程列表
  getCourseList() {
    return request.get('/course/list')
  },
  
  // 获取课程详情
  getCourseDetail(id) {
    return request.get(`/course/${id}`)
  },
  
  // 创建课程
  createCourse(courseData) {
    return request.post('/course/create', courseData)
  },
  
  // 更新课程
  updateCourse(id, courseData) {
    return request.put(`/course/${id}`, courseData)
  },
  
  // 删除课程
  deleteCourse(id) {
    return request.delete(`/course/${id}`)
  },
  
  // 搜索课程
  searchCourses(keyword) {
    return request.get('/course/search', { params: { keyword } })
  },
  
  // 获取热门趋势
  getHotTrends() {
    return request.get('/course/trends')
  }
}

export const userAPI = {
  // 用户登录
  login(credentials) {
    return request.post('/api/user/login', credentials)
  },
  
  // 用户注册
  register(userData) {
    return request.post('/api/user/register', userData)
  },
  
  // 获取用户信息
  getUserInfo() {
    return request.get('/api/user/info')
  },
  
  // 更新用户信息
  updateUserInfo(userData) {
    return request.put('/api/user/info', userData)
  },
  // 获取用户列表
  getUsers(params) {
    return request.get('/api/admin/users', { params })
  },
  // 导出用户数据
  exportUsers(params) {
    return request.get('/api/admin/users/export', { params, responseType: 'blob' })
  },
  // 更新用户信息（根据ID）
  updateUser(id, userData) {
    return request.put(`/api/admin/users/${id}`, userData)
  },
  // 删除用户
  deleteUser(id) {
    return request.delete(`/api/admin/users/${id}`)
  },
  // 获取用户历史记录
  getUserHistory(params) {
    return request.get('/api/admin/users/history', { params })
  }
}

export const statsAPI = {
  // 获取统计数据
  getStats() {
    return request.get('/stats/overview')
  },
  
  // 获取图表数据
  getChartData() {
    return request.get('/stats/chart')
  }
}

export const featuredAPI = {
  // 获取推荐课程
  getFeaturedCourses() {
    return request.get('/featured/list')
  },
  
  // 获取热门课程
  getHotCourses() {
    return request.get('/featured/hot')
  }
}

export const qnaAPI = {
  // 获取问答列表
  getQnAList() {
    return request.get('/qna/list')
  },
  
  // 创建问答
  createQnA(qnaData) {
    return request.post('/qna/create', qnaData)
  }
}

export const fileAPI = {
  // 上传文件
  uploadFile(file, type = 'course') {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('type', type)
    return request.post('/file/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}

export { askAI } from './register.js';

const api = {
  enterprise: enterpriseApi,
  // 可继续聚合其他API，如userAPI、courseAPI等
}

export default api

// 保留原有request等命名导出
export { request } 