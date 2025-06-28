import axios from 'axios';

const apiClient = axios.create({  
  baseURL: 'http://localhost:8080/api',
  withCredentials: false,
  timeout: 10000, // 10秒超时
  headers: {
    Accept: 'application/json',
    'Content-Type': 'application/json'
  }
});

// 请求拦截器，自动添加token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    console.error('请求拦截器错误:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器，统一处理错误
apiClient.interceptors.response.use(
  (response) => {
    // 检查业务层面的错误
    if (response.data && response.data.code !== 200) {
      const error = new Error(response.data.message || '请求失败');
      error.response = response;
      error.code = response.data.code;
      return Promise.reject(error);
    }
    return response;
  },
  (error) => {
    console.error('响应拦截器错误:', error);
    
    // 处理不同类型的错误
    if (error.response) {
      // 服务器返回了错误状态码
      const status = error.response.status;
      const message = error.response.data?.message || '服务器错误';
      
      switch (status) {
        case 401:
          // Token过期或无效，清除本地存储并跳转到登录页
          localStorage.removeItem('token');
          localStorage.removeItem('user');
          window.location.href = '/login';
          break;
        case 403:
          error.message = '权限不足';
          break;
        case 404:
          error.message = '请求的资源不存在';
          break;
        case 500:
          error.message = '服务器内部错误';
          break;
        default:
          error.message = message;
      }
    } else if (error.request) {
      // 请求已发出但没有收到响应
      error.message = '网络连接失败，请检查网络设置';
    } else {
      // 请求配置出错
      error.message = '请求配置错误';
    }
    
    return Promise.reject(error);
  }
);

// 重试机制
const retryRequest = async (fn, retries = 3, delay = 1000) => {
  try {
    return await fn();
  } catch (error) {
    if (retries > 0 && (error.code === 500 || error.message.includes('网络连接失败'))) {
      console.log(`请求失败，${delay}ms后重试，剩余重试次数: ${retries - 1}`);
      await new Promise(resolve => setTimeout(resolve, delay));
      return retryRequest(fn, retries - 1, delay * 2);
    }
    throw error;
  }
};

// 通用请求方法
const makeRequest = async (requestFn, retries = 2) => {
  try {
    return await retryRequest(requestFn, retries);
  } catch (error) {
    console.error('API请求失败:', error);
    throw error;
  }
};

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
  
  // 验证登录凭据
  credentials: (credentials) => {
    if (!credentials) throw new Error('登录凭据不能为空');
    if (!credentials.username || credentials.username.trim() === '') {
      throw new Error('用户名不能为空');
    }
    if (!credentials.password || credentials.password.trim() === '') {
      throw new Error('密码不能为空');
    }
  },
  
  // 验证审核请求
  reviewRequest: (request) => {
    if (!request) throw new Error('审核请求不能为空');
    if (!request.meetingId) throw new Error('会议ID不能为空');
    if (request.status === undefined || request.status === null) {
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

export default {
  // 用户登录
  async login(credentials) {
    try {
      validateParams.credentials(credentials);
      return makeRequest(() => apiClient.post('/user/login', credentials));
    } catch (error) {
      console.error('登录参数验证失败:', error);
      throw error;
    }
  },

  // 获取用户信息
  async getUserInfo() {
    return makeRequest(() => apiClient.post('/user/info'));
  },

  // 创建会议
  async createMeeting(meeting) {
    try {
      validateParams.meeting(meeting);
      return makeRequest(() => apiClient.post('/meeting/create', meeting));
    } catch (error) {
      console.error('创建会议参数验证失败:', error);
      throw error;
    }
  },

  // 更新会议
  async updateMeeting(meeting) {
    try {
      if (!meeting || !meeting.id) {
        throw new Error('会议ID不能为空');
      }
      validateParams.meeting(meeting);
      return makeRequest(() => apiClient.post('/meeting/update', meeting));
    } catch (error) {
      console.error('更新会议参数验证失败:', error);
      throw error;
    }
  },

  // 删除会议
  async deleteMeeting(meetingId, confirmDelete) {
    try {
      if (!meetingId) {
        throw new Error('会议ID不能为空');
      }
      if (typeof confirmDelete !== 'boolean') {
        throw new Error('确认删除参数必须是布尔值');
      }
      return makeRequest(() => apiClient.post('/meeting/delete', {
        meeting_id: meetingId,
        confirm_delete: confirmDelete
      }));
    } catch (error) {
      console.error('删除会议参数验证失败:', error);
      throw error;
    }
  },

  // 审核会议
  async reviewMeeting(reviewRequest) {
    try {
      validateParams.reviewRequest(reviewRequest);
      return makeRequest(() => apiClient.post('/meeting/review', reviewRequest));
    } catch (error) {
      console.error('审核会议参数验证失败:', error);
      throw error;
    }
  },

  // 获取会议列表
  async getMeetings(query) {
    try {
      const defaultQuery = {
        page: 1,
        size: 10,
        ...query
      };
      validateParams.query(defaultQuery);
      return makeRequest(() => apiClient.post('/meeting/list', defaultQuery));
    } catch (error) {
      console.error('获取会议列表参数验证失败:', error);
      throw error;
    }
  },

  // 获取会议详情
  async getMeetingDetail(meetingId) {
    try {
      if (!meetingId) {
        throw new Error('会议ID不能为空');
      }
      return makeRequest(() => apiClient.post('/meeting/detail', { meeting_id: meetingId }));
    } catch (error) {
      console.error('获取会议详情参数验证失败:', error);
      throw error;
    }
  },

  // 获取待审核会议
  async getPendingMeetings() {
    return makeRequest(() => apiClient.post('/meeting/pending'));
  },

  // 获取审核记录（按审核人）
  async getReviewRecordsByReviewer() {
    return makeRequest(() => apiClient.post('/meeting/review/records/by-reviewer'));
  },

  // 获取审核记录（按创建人）
  async getReviewRecordsByCreator() {
    return makeRequest(() => apiClient.post('/meeting/review/records/by-creator'));
  },

  // 获取审核记录（按会议ID）
  async getReviewRecordsByMeeting(meetingId) {
    try {
      if (!meetingId) {
        throw new Error('会议ID不能为空');
      }
      return makeRequest(() => apiClient.post('/meeting/review/records/by-meeting', { meeting_id: meetingId }));
    } catch (error) {
      console.error('获取审核记录参数验证失败:', error);
      throw error;
    }
  },

  // 上传会议图片
  async uploadMeetingImage(file) {
    const formData = new FormData();
    formData.append('file', file);
    return apiClient.post('/meeting/uploadImage', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  }
}; 