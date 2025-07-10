import axios from 'axios'
import { ElMessage } from 'element-plus'

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
    const { data } = response
    // 后端成功状态码是200
    if (data.code === 200) {
      return data
    } else {
      ElMessage.error(data.msg || '请求失败')
      return Promise.reject(new Error(data.msg || '请求失败'))
    }
  },
  error => {
    if (error.response) {
      const { status, data } = error.response
      switch (status) {
        case 401:
          ElMessage.error('未授权，请重新登录')
          localStorage.removeItem('token')
          // 可以在这里跳转到登录页
          break
        case 403:
          ElMessage.error('拒绝访问')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(data?.msg || '网络错误')
      }
    } else {
      ElMessage.error('网络连接失败')
    }
    return Promise.reject(error)
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
    return request.post('/user/login', credentials)
  },
  
  // 用户注册
  register(userData) {
    return request.post('/user/register', userData)
  },
  
  // 获取用户信息
  getUserInfo() {
    return request.get('/user/info')
  },
  
  // 更新用户信息
  updateUserInfo(userData) {
    return request.put('/user/info', userData)
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

export default request 