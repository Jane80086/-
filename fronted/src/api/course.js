import request from '@/utils/request'

// 课程相关API
export const courseApi = {
  getCourseList(params) {
    return request.get('/course/list', { params })
  },
  getCourseDetail(id) {
    return request.get(`/api/course/${id}`)
  },
  createCourse(data) {
    return request.post('/course/create', data)
  },
  updateCourse(id, data) {
    return request.put(`/api/course/${id}`, data)
  },
  deleteCourse(id) {
    return request.delete(`/api/course/${id}`)
  },
  getChapters(id) {
    return request.get(`/api/course/${id}/chapters`)
  },

  // 获取课程笔记
  getNotes(courseId) {
    return request({
      url: `/course/${courseId}/notes`,
      method: 'get'
    })
  },

  // 添加笔记
  addNote(courseId, data) {
    return request({
      url: `/course/${courseId}/notes`,
      method: 'post',
      data
    })
  },

  // 删除笔记
  deleteNote(noteId) {
    return request({
      url: `/notes/${noteId}`,
      method: 'delete'
    })
  },

  // 获取课程问答
  getQuestions(courseId) {
    return request({
      url: `/course/${courseId}/questions`,
      method: 'get'
    })
  },

  // 提交问题
  submitQuestion(courseId, data) {
    return request({
      url: `/course/${courseId}/questions`,
      method: 'post',
      data
    })
  },

  // 点赞问题
  likeQuestion(questionId) {
    return request({
      url: `/questions/${questionId}/like`,
      method: 'post'
    })
  },

  // 收藏/取消收藏课程
  toggleFavorite(courseId) {
    return request({
      url: `/course/${courseId}/favorite`,
      method: 'post'
    })
  },

  // 获取推荐课程
  getRecommendations(userId) {
    return request({
      url: '/recommendation/courses',
      method: 'get',
      params: { userId }
    })
  },

  // 获取热门课程
  getPopularCourses() {
    return request({
      url: '/recommendation/popular',
      method: 'get'
    })
  },

  // 搜索课程
  searchCourses(keyword) {
    return request({
      url: '/course/search',
      method: 'get',
      params: { keyword }
    })
  },

  // 获取热搜关键词
  getHotKeywords() {
    return request({
      url: '/search/hot-keywords',
      method: 'get'
    })
  },

  // 获取我的课程
  getMyCourses(params = {}) {
    return request({
      url: '/course/my',
      method: 'get',
      params
    })
  },

  // 获取我的课程统计
  getMyCourseStats() {
    return request({
      url: '/course/my/stats',
      method: 'get'
    })
  },

  // 获取学习进度
  getLearningProgress(courseId) {
    return request({
      url: `/course/${courseId}/progress`,
      method: 'get'
    })
  },

  // 获取课程评论
  getComments(courseId) {
    return request({
      url: `/course/${courseId}/comments`,
      method: 'get'
    })
  },
  // 获取AI问答历史
  getAiQnaList(courseId) {
    return request({
      url: `/course/${courseId}/ai-qna`,
      method: 'get'
    })
  },
}

// 文件相关API
export const fileApi = {
  // 上传文件
  uploadFile(file, folder = 'general') {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('folder', folder)
    
    return request({
      url: '/api/file/upload',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 上传课程封面
  uploadCourseCover(file) {
    const formData = new FormData()
    formData.append('file', file)
    
    return request({
      url: '/api/file/upload/course-cover',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 上传课程视频
  uploadCourseVideo(file) {
    const formData = new FormData()
    formData.append('file', file)
    
    return request({
      url: '/api/file/upload/course-video',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 上传用户头像
  uploadUserAvatar(file) {
    const formData = new FormData()
    formData.append('file', file)
    
    return request({
      url: '/api/file/upload/user-avatar',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 获取文件列表
  getFileList(folder = '') {
    return request({
      url: '/api/file/list',
      method: 'get',
      params: { folder }
    })
  },

  // 删除文件
  deleteFile(objectName) {
    return request({
      url: '/api/file/delete',
      method: 'delete',
      params: { objectName }
    })
  },

  // 获取预签名URL
  getPresignedUrl(objectName, expirySeconds = 3600) {
    return request({
      url: '/api/file/presigned-url',
      method: 'get',
      params: { objectName, expirySeconds }
    })
  },

  // 检查文件是否存在
  fileExists(objectName) {
    return request({
      url: '/api/file/exists',
      method: 'get',
      params: { objectName }
    })
  }
}

// 统计相关API
export const statsApi = {
  // 获取统计数据概览
  getStatsOverview() {
    return request({
      url: '/api/stats/overview',
      method: 'get'
    })
  },

  // 获取图表数据
  getChartData() {
    return request({
      url: '/api/stats/chart',
      method: 'get'
    })
  },

  // 获取仪表板数据
  getDashboardStats() {
    return request({
      url: '/api/stats/dashboard',
      method: 'get'
    })
  },

  // 获取系统健康状态
  getSystemHealth() {
    return request({
      url: '/api/stats/health',
      method: 'get'
    })
  },

  // 获取收入统计
  getRevenueStats() {
    return request({
      url: '/api/stats/revenue',
      method: 'get'
    })
  },

  // 获取热门趋势
  getTrends() {
    return request({
      url: '/api/stats/trends',
      method: 'get'
    })
  }
}

// 管理员相关API
export const adminApi = {
  // 获取用户列表
  getUsers(params) {
    return request({
      url: '/api/admin/users',
      method: 'get',
      params
    })
  },

  // 获取所有用户
  getAllUsers() {
    return request({
      url: '/api/admin/users/all',
      method: 'get'
    })
  },

  // 根据类型获取用户
  getUsersByType(userType) {
    return request({
      url: `/api/admin/users/type/${userType}`,
      method: 'get'
    })
  },

  // 获取用户详情
  getUserById(userId) {
    return request({
      url: `/api/admin/user/${userId}`,
      method: 'get'
    })
  },

  // 封禁用户
  banUser(userId) {
    return request({
      url: `/api/admin/user/${userId}/ban`,
      method: 'post'
    })
  },

  // 解封用户
  unbanUser(userId) {
    return request({
      url: `/api/admin/user/${userId}/unban`,
      method: 'post'
    })
  },

  // 获取课程列表
  getCourses(params) {
    return request({
      url: '/api/admin/courses',
      method: 'get',
      params
    })
  },

  // 获取待审核课程
  getPendingCourses() {
    return request({
      url: '/api/admin/courses/pending',
      method: 'get'
    })
  },

  // 课程审核
  reviewCourse(courseId, status, reason = '') {
    return request({
      url: `/api/admin/course/${courseId}/review`,
      method: 'post',
      params: { status, reason }
    })
  },

  // 评论审核
  reviewComment(commentId, status, reason = '') {
    return request({
      url: `/api/admin/comment/${commentId}/review`,
      method: 'post',
      params: { status, reason }
    })
  },

  // 问答审核
  reviewQnA(qnaId, status, reason = '') {
    return request({
      url: `/api/admin/qna/${qnaId}/review`,
      method: 'post',
      params: { status, reason }
    })
  },

  // 获取管理员仪表板
  getDashboard() {
    return request({
      url: '/api/admin/dashboard',
      method: 'get'
    })
  }
} 