import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const api = axios.create({
  baseURL: '/',
  timeout: 10000
})

// 请求拦截器
api.interceptors.request.use(
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
api.interceptors.response.use(
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

// 参数验证函数
const validateParams = {
  // 验证会议对象
  meeting: (meeting) => {
    if (!meeting) throw new Error('会议数据不能为空');
    if (!meeting.meetingName || meeting.meetingName.trim() === '') {
      throw new Error('会议名称不能为空');
    }
    if (!meeting.startTime) throw new Error('开始时间不能为空');
    if (!meeting.endTime) throw new Error('结束时间不能为空');
    if (!meeting.meetingContent || meeting.meetingContent.trim() === '') {
      throw new Error('会议内容不能为空');
    }
    
    // 验证时间逻辑
    const startTime = new Date(meeting.startTime);
    const endTime = new Date(meeting.endTime);
    if (startTime >= endTime) {
      throw new Error('结束时间必须晚于开始时间');
    }
    if (startTime <= new Date()) {
      throw new Error('开始时间必须是未来时间');
    }
  },
  
  // 验证审核请求
  reviewRequest: (reviewData) => {
    if (!reviewData) throw new Error('审核请求不能为空');
    if (!reviewData.meetingId) throw new Error('会议ID不能为空');
    if (reviewData.status === undefined || reviewData.status === null) {
      throw new Error('审核状态不能为空');
    }
  },
  
  // 验证查询参数
  query: (query) => {
    if (!query) throw new Error('查询参数不能为空');
    if (query.page && (query.page < 1 || !Number.isInteger(query.page))) {
      throw new Error('页码必须是大于0的整数');
    }
    if (query.size && (query.size < 1 || query.size > 100 || !Number.isInteger(query.size))) {
      throw new Error('页面大小必须是1-100之间的整数');
    }
  }
};

// 重试机制
const retryRequest = async (fn, retries = 3, delay = 1000) => {
  try {
    return await fn();
  } catch (error) {
    if (retries > 0 && (error.code === 500 || error.message.includes('网络连接失败'))) {
      await new Promise(resolve => setTimeout(resolve, delay));
      return retryRequest(fn, retries - 1, delay * 2);
    }
    throw error;
  }
};

// 通用请求方法
const makeRequest = async (requestFn, retries = 2) => {
    return await retryRequest(requestFn, retries);
};

export const meetingAPI = {
  // 创建会议
  async createMeeting(meeting) {
    validateParams.meeting(meeting);
    return makeRequest(() => api.post('/api/meeting/create', meeting));
  },

  // 更新会议
  async updateMeeting(meeting) {
    if (!meeting || !meeting.id) {
      throw new Error('会议ID不能为空');
    }
    validateParams.meeting(meeting);
    return makeRequest(() => api.post('/api/meeting/update', meeting));
  },

  // 删除会议
  async deleteMeeting(meetingId, confirmDelete) {
    if (!meetingId) throw new Error('会议ID不能为空');
    return makeRequest(() => api.post('/api/meeting/delete', { 
      meeting_id: meetingId, 
      confirm_delete: !!confirmDelete 
    }));
  },

  // 审核会议
  async reviewMeeting(reviewRequest) {
    validateParams.reviewRequest(reviewRequest);
    return makeRequest(() => api.post('/api/meeting/review', reviewRequest));
  },

  // 查询会议列表
  async getMeetings(query) {
    validateParams.query(query);
    return makeRequest(() => api.post('/api/meeting/list', query));
  },

  // 获取会议详情
  async getMeetingDetail(meetingId) {
    if (!meetingId) throw new Error('会议ID不能为空');
    return makeRequest(() => api.post('/api/meeting/detail', { meeting_id: meetingId }));
  },

  // 获取待审核会议
  async getPendingMeetings() {
    return makeRequest(() => api.post('/api/meeting/pending'));
  },

  // 获取审核记录（审核人视角）
  async getReviewRecordsByReviewer() {
    return makeRequest(() => api.post('/api/meeting/review/records/by-reviewer'));
  },

  // 获取审核记录（创建人视角）
  async getReviewRecordsByCreator() {
    return makeRequest(() => api.post('/api/meeting/review/records/by-creator'));
  },

  // 获取会议审核记录
  async getReviewRecordsByMeeting(meetingId) {
    if (!meetingId) throw new Error('会议ID不能为空');
    return makeRequest(() => api.post('/api/meeting/review/records/by-meeting', { meeting_id: meetingId }));
  },

  // 上传会议图片
  async uploadMeetingImage(file) {
    if (!file) throw new Error('文件不能为空');
    
    const formData = new FormData();
    formData.append('file', file);
    
    return makeRequest(() => api.post('/api/meeting/uploadImage', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    }));
  },

  // 获取预签名URL
  async getPresignedUrl(objectName) {
    if (!objectName) throw new Error('对象名称不能为空');
    return makeRequest(() => api.post('/api/meeting/presigned-url', { objectName }));
  }
};

export default meetingAPI; 