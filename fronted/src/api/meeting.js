import request from './index'

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
  // 用户登录
  async login(credentials) {
    validateParams.credentials(credentials);
    const res = await makeRequest(() => request.post('/api/auth/login', credentials));
    // 登录成功后保存token和用户信息
    if (res.data && res.code === 200 && res.data) {
      localStorage.setItem('token', res.data.token);
      if (res.data.user) {
        localStorage.setItem('user', JSON.stringify(res.data.user));
      }
    }
    return res;
  },

  // 获取用户信息
  async getUserInfo() {
    return makeRequest(() => request.get('/api/auth/profile'));
  },

  // 创建会议
  async createMeeting(meeting) {
    validateParams.meeting(meeting);
    return makeRequest(() => request.post('/api/meeting/create', meeting));
  },

  // 更新会议
  async updateMeeting(meeting) {
    if (!meeting || !meeting.id) {
      throw new Error('会议ID不能为空');
    }
    validateParams.meeting(meeting);
    return makeRequest(() => request.post('/api/meeting/update', meeting));
  },

  // 删除会议
  async deleteMeeting(meetingId, confirmDelete) {
    if (!meetingId) throw new Error('会议ID不能为空');
    return makeRequest(() => request.post('/api/meeting/delete', { 
      meeting_id: meetingId, 
      confirm_delete: !!confirmDelete 
    }));
  },

  // 审核会议
  async reviewMeeting(reviewRequest) {
    validateParams.reviewRequest(reviewRequest);
    return makeRequest(() => request.post('/api/meeting/review', reviewRequest));
  },

  // 查询会议列表
  async getMeetings(query) {
    validateParams.query(query);
    return makeRequest(() => request.post('/api/meeting/list', query));
  },

  // 获取会议详情
  async getMeetingDetail(meetingId) {
    if (!meetingId) throw new Error('会议ID不能为空');
    return makeRequest(() => request.post('/api/meeting/detail', { meeting_id: meetingId }));
  },

  // 获取待审核会议
  async getPendingMeetings() {
    return makeRequest(() => request.post('/api/meeting/pending'));
  },

  // 获取审核记录（审核人视角）
  async getReviewRecordsByReviewer() {
    return makeRequest(() => request.post('/api/meeting/review/records/by-reviewer'));
  },

  // 获取审核记录（创建人视角）
  async getReviewRecordsByCreator() {
    return makeRequest(() => request.post('/api/meeting/review/records/by-creator'));
  },

  // 获取会议审核记录
  async getReviewRecordsByMeeting(meetingId) {
    if (!meetingId) throw new Error('会议ID不能为空');
    return makeRequest(() => request.post('/api/meeting/review/records/by-meeting', { meeting_id: meetingId }));
  },

  // 上传会议图片
  async uploadMeetingImage(file) {
    if (!file) throw new Error('文件不能为空');
    
    const formData = new FormData();
    formData.append('file', file);
    
    return makeRequest(() => request.post('/api/meeting/uploadImage', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    }));
  }
};

export default meetingAPI; 