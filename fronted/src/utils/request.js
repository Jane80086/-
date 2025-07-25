import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

// 创建axios实例
const service = axios.create({
  baseURL: '', // 修正：不要加 /，防止出现 //api/xxx
  timeout: 120000, // 120秒，提升AI问答等慢接口的容忍时间
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 添加token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    // 兼容后端无 code 字段，仅 success 字段
    if (typeof res.code !== 'undefined') {
      // news模块返回字符串'0'，其他模块返回数字200
      if (res.code === 200 || res.code === '0') {
        return res // 成功，返回整个 res 对象
      } else {
        ElMessage({
          message: res.message || res.msg || '请求失败',
          type: 'error',
          duration: 5 * 1000
        })
        return Promise.reject(new Error(res.message || res.msg || '请求失败'))
      }
    } else if (typeof res.success !== 'undefined') {
      if (!res.success) {
        ElMessage({
          message: res.message || '请求失败',
          type: 'error',
          duration: 5 * 1000
        })
        return Promise.reject(new Error(res.message || '请求失败'))
      } else {
        return res
      }
    } else {
      // 兜底
      return res
    }
  },
  error => {
    console.error('响应错误:', error)
    let message = '网络错误'

    if (error.response) {
      const { status, data } = error.response
      switch (status) {
        case 400:
          message = data.message || '请求参数错误'
          break
        case 401:
          message = '未授权，请重新登录'
          localStorage.removeItem('token')
          break
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = '请求的资源不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = data?.message || `请求失败: ${status}`
      }
    } else if (error.request) {
      message = '网络连接失败'
    } else {
      message = error.message
    }

    ElMessage({
      message,
      type: 'error',
      duration: 5 * 1000
    })

    return Promise.reject(error)
  }
)

export default service