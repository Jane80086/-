import request from '@/utils/request'

// 课程相关API - mock数据
export const courseApi = {
  // 获取课程列表
  getCourseList(params = {}) {
    return request({
      url: '/course/list',
      method: 'get',
      params
    })
  },

  // 获取课程详情
  getCourseDetail(courseId) {
    return Promise.resolve({
      code: 200,
      data: {
        id: courseId,
        title: courseId == 1 ? '前端开发入门' : 'Java后端实战',
        description: courseId == 1 ? '学习HTML、CSS、JavaScript基础' : 'Spring Boot+MyBatis企业级开发',
        imageUrl: '/default-course.jpg',
        instructorName: courseId == 1 ? '张老师' : '李老师',
        duration: courseId == 1 ? 120 : 180,
        viewCount: courseId == 1 ? 1234 : 888,
        rating: courseId == 1 ? 4.5 : 4.8,
        price: courseId == 1 ? 0 : 99,
        category: courseId == 1 ? '前端开发' : '后端开发',
        level: courseId == 1 ? '初级' : '中级',
        createTime: '2024-06-01',
        outline: '1. 课程介绍\n2. 基础知识\n3. 实战演练',
        status: 1,
        updateTime: '2024-06-10',
        videoUrl: '/test-video.mp4'
      }
    })
  },

  // 搜索课程
  searchCourses(keyword) {
    return this.getCourseList()
  },

  // 获取热搜关键词
  getHotKeywords() {
    return Promise.resolve({
      code: 200,
      data: ['前端', 'Java', 'AI', '大数据', 'Python']
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

  // 获取课程笔记
  getNotes(courseId) {
    return Promise.resolve({
      code: 200,
      data: [
        {
          id: 1,
          title: 'HTML基础语法',
          content: 'HTML是超文本标记语言，用于构建网页结构。主要标签包括html、head、body、div、span等。',
          timestamp: 120,
          createTime: '2024-06-15 14:30:00'
        },
        {
          id: 2,
          title: 'CSS样式设置',
          content: 'CSS用于设置网页样式，包括颜色、字体、布局等。可以使用内联、内部、外部三种方式引入。',
          timestamp: 300,
          createTime: '2024-06-15 15:20:00'
        },
        {
          id: 3,
          title: 'JavaScript变量声明',
          content: 'JavaScript中可以使用var、let、const声明变量。let和const是ES6新增的块级作用域变量。',
          timestamp: 600,
          createTime: '2024-06-15 16:10:00'
        }
      ]
    })
  },

  // 获取课程章节
  getChapters(courseId) {
    return Promise.resolve({
      code: 200,
      data: [
        { id: 1, title: '课程介绍', description: '本节介绍课程内容', videoUrl: '/test-video.mp4', duration: 10, completed: false },
        { id: 2, title: '基础知识', description: '学习基础知识', videoUrl: '/test-video.mp4', duration: 60, completed: false },
        { id: 3, title: '实战演练', description: '动手实操', videoUrl: '/test-video.mp4', duration: 50, completed: false }
      ]
    })
  },

  // 获取学习进度
  getLearningProgress(courseId) {
    return Promise.resolve({
      code: 200,
      data: {
        progress: 60,
        records: [
          { id: 1, title: '学习了课程介绍', description: '完成第一节', createTime: '2024-06-10 10:00' },
          { id: 2, title: '学习了基础知识', description: '完成第二节', createTime: '2024-06-10 11:00' }
        ],
        watchedTime: 72
      }
    })
  },

  // 获取课程问答
  getQuestions(courseId) {
    return Promise.resolve({
      code: 200,
      data: [
        {
          id: 1,
          title: '课程难吗？',
          content: '零基础可以学吗？',
          userName: '小明',
          userAvatar: '',
          createTime: '2024-06-10 09:00',
          answered: true,
          aiAnswer: '本课程适合零基础同学，内容循序渐进。',
          answers: [
            { id: 1, userName: '张老师', userAvatar: '', content: '完全可以学！', createTime: '2024-06-10 09:30' }
          ],
          likeCount: 3
        }
      ]
    })
  },

  // 提交问题
  submitQuestion() {
    return Promise.resolve({ code: 200 })
  },

  // 点赞问题
  likeQuestion() {
    return Promise.resolve({ code: 200 })
  },

  // 获取笔记
  getNotes(courseId) {
    return Promise.resolve({
      code: 200,
      data: [
        { id: 1, title: '重点笔记', content: '记住HTML标签', timestamp: 30, createTime: '2024-06-10 10:10' }
      ]
    })
  },

  // 添加笔记
  addNote() {
    return Promise.resolve({ code: 200 })
  },

  // 删除笔记
  deleteNote() {
    return Promise.resolve({ code: 200 })
  },

  // 收藏/取消收藏课程
  toggleFavorite() {
    return Promise.resolve({ code: 200 })
  },

  // 获取推荐课程
  getRecommendations(userId) {
    return request({
      url: '/api/recommendation/courses',
      method: 'get',
      params: { userId }
    })
  },

  // 获取热门课程
  getPopularCourses() {
    return request({
      url: '/api/recommendation/popular',
      method: 'get'
    })
  },

  // 课程优化预览
  optimizePreview(params) {
    return request({
      url: '/api/course/optimize-preview',
      method: 'post',
      params
    })
  },

  // AI聊天
  aiChat(data) {
    return request({
      url: '/ai/chat',
      method: 'post',
      data
    })
  },

  // AI优化
  aiOptimize(data) {
    return request({
      url: '/ai/optimize',
      method: 'post',
      data
    })
  },

  // 获取AI状态
  getAiStatus() {
    return request({
      url: '/ai/status',
      method: 'get'
    })
  },

  // 获取相关课程
  getRelatedCourses(courseId) {
    return request({
      url: `/api/search/related/${courseId}`,
      method: 'get'
    })
  },

  // 获取课程评论
  getComments(courseId) {
    return Promise.resolve({
      code: 200,
      data: [
        { id: 1, userName: '小红', content: '课程讲得很棒！', createTime: '2024-06-10 12:00', likeCount: 2 },
        { id: 2, userName: '小李', content: '希望多点实战案例', createTime: '2024-06-10 13:00', likeCount: 1 }
      ]
    })
  },

  // 提交评论
  submitComment(data) {
    return Promise.resolve({ code: 200 })
  },

  // 点赞评论
  likeComment(commentId) {
    return Promise.resolve({ code: 200 })
  },

  // AI问答区mock
  getAiQnaList(courseId) {
    return Promise.resolve({
      code: 200,
      data: [
        { id: 1, question: '本课程适合零基础吗？', answer: 'AI：本课程从基础讲起，零基础同学也能学会。', userName: '小明', createTime: '2024-06-10 10:00' },
        { id: 2, question: '有配套资料吗？', answer: 'AI：课程配有电子讲义和代码示例，可在资料区下载。', userName: '小王', createTime: '2024-06-10 11:00' }
      ]
    })
  },

  // AI提问
  aiAsk(data) {
    return Promise.resolve({ code: 200, data: { answer: 'AI：这是AI的智能回复示例。' } })
  },

  // 获取课程趋势
  getTrends() {
    return request({
      url: '/api/course/trends',
      method: 'get'
    })
  },

  // 获取用户历史
  getUserHistory(userId) {
    return request({
      url: `/api/stats/user/${userId}/history`,
      method: 'get'
    })
  },

  // 获取课程分析
  getCourseAnalytics(courseId) {
    return request({
      url: `/api/stats/course/${courseId}/analytics`,
      method: 'get'
    })
  },

  // 企业端：获取课程列表
  getEnterpriseCourses(params = {}) {
    return request({
      url: '/course/enterprise',
      method: 'get',
      params
    })
  },

  // 企业端：新建课程
  createEnterpriseCourse(data) {
    return request({
      url: '/course/enterprise',
      method: 'post',
      data
    })
  },

  // 企业端：更新课程
  updateEnterpriseCourse(data) {
    return request({
      url: `/course/enterprise/${data.id}`,
      method: 'put',
      data
    })
  },

  // 企业端：删除课程
  deleteEnterpriseCourse(id) {
    return request({
      url: `/course/enterprise/${id}`,
      method: 'delete'
    })
  },

  // 新建课程
  createCourse(data) {
    return request({
      url: '/course/create',
      method: 'post',
      data
    })
  },

  // 更新课程
  updateCourse(id, data) {
    return request({
      url: `/course/${id}`,
      method: 'put',
      data
    })
  },

  // 删除课程
  deleteCourse(id) {
    return request({
      url: `/course/${id}`,
      method: 'delete'
    })
  },

  // 提交课程审核
  submitCourseForReview(id) {
    return request({
      url: `/course/${id}/submit-review`,
      method: 'post'
    })
  },

  // 获取课程发布/审核记录
  getReviewHistory(id) {
    return request({
      url: `/course/${id}/review-history`,
      method: 'get'
    })
  },

  // 下架课程
  unpublishCourse(id) {
    return request({
      url: `/course/${id}/unpublish`,
      method: 'put'
    })
  }
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
export const courseApi = {
  // 获取课程列表
  getCourseList(params = {}) {
    return request({
      url: '/course/list',
      method: 'get',
      params
    })
  },

  // 获取课程详情
  getCourseDetail(courseId) {
    return Promise.resolve({
      code: 200,
      data: {
        id: courseId,
        title: courseId == 1 ? '前端开发入门' : 'Java后端实战',
        description: courseId == 1 ? '学习HTML、CSS、JavaScript基础' : 'Spring Boot+MyBatis企业级开发',
        imageUrl: '/default-course.jpg',
        instructorName: courseId == 1 ? '张老师' : '李老师',
        duration: courseId == 1 ? 120 : 180,
        viewCount: courseId == 1 ? 1234 : 888,
        rating: courseId == 1 ? 4.5 : 4.8,
        price: courseId == 1 ? 0 : 99,
        category: courseId == 1 ? '前端开发' : '后端开发',
        level: courseId == 1 ? '初级' : '中级',
        createTime: '2024-06-01',
        outline: '1. 课程介绍\n2. 基础知识\n3. 实战演练',
        status: 1,
        updateTime: '2024-06-10',
        videoUrl: '/test-video.mp4'
      }
    })
  },

  // 搜索课程
  searchCourses(keyword) {
    return this.getCourseList()
  },

  // 获取热搜关键词
  getHotKeywords() {
    return Promise.resolve({
      code: 200,
      data: ['前端', 'Java', 'AI', '大数据', 'Python']
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

  // 获取课程笔记
  getNotes(courseId) {
    return Promise.resolve({
      code: 200,
      data: [
        {
          id: 1,
          title: 'HTML基础语法',
          content: 'HTML是超文本标记语言，用于构建网页结构。主要标签包括html、head、body、div、span等。',
          timestamp: 120,
          createTime: '2024-06-15 14:30:00'
        },
        {
          id: 2,
          title: 'CSS样式设置',
          content: 'CSS用于设置网页样式，包括颜色、字体、布局等。可以使用内联、内部、外部三种方式引入。',
          timestamp: 300,
          createTime: '2024-06-15 15:20:00'
        },
        {
          id: 3,
          title: 'JavaScript变量声明',
          content: 'JavaScript中可以使用var、let、const声明变量。let和const是ES6新增的块级作用域变量。',
          timestamp: 600,
          createTime: '2024-06-15 16:10:00'
        }
      ]
    })
  },

  // 获取课程章节
  getChapters(courseId) {
    return Promise.resolve({
      code: 200,
      data: [
        { id: 1, title: '课程介绍', description: '本节介绍课程内容', videoUrl: '/test-video.mp4', duration: 10, completed: false },
        { id: 2, title: '基础知识', description: '学习基础知识', videoUrl: '/test-video.mp4', duration: 60, completed: false },
        { id: 3, title: '实战演练', description: '动手实操', videoUrl: '/test-video.mp4', duration: 50, completed: false }
      ]
    })
  },

  // 获取学习进度
  getLearningProgress(courseId) {
    return Promise.resolve({
      code: 200,
      data: {
        progress: 60,
        records: [
          { id: 1, title: '学习了课程介绍', description: '完成第一节', createTime: '2024-06-10 10:00' },
          { id: 2, title: '学习了基础知识', description: '完成第二节', createTime: '2024-06-10 11:00' }
        ],
        watchedTime: 72
      }
    })
  },

  // 获取课程问答
  getQuestions(courseId) {
    return Promise.resolve({
      code: 200,
      data: [
        {
          id: 1,
          title: '课程难吗？',
          content: '零基础可以学吗？',
          userName: '小明',
          userAvatar: '',
          createTime: '2024-06-10 09:00',
          answered: true,
          aiAnswer: '本课程适合零基础同学，内容循序渐进。',
          answers: [
            { id: 1, userName: '张老师', userAvatar: '', content: '完全可以学！', createTime: '2024-06-10 09:30' }
          ],
          likeCount: 3
        }
      ]
    })
  },

  // 提交问题
  submitQuestion() {
    return Promise.resolve({ code: 200 })
  },

  // 点赞问题
  likeQuestion() {
    return Promise.resolve({ code: 200 })
  },

  // 获取笔记
  getNotes(courseId) {
    return Promise.resolve({
      code: 200,
      data: [
        { id: 1, title: '重点笔记', content: '记住HTML标签', timestamp: 30, createTime: '2024-06-10 10:10' }
      ]
    })
  },

  // 添加笔记
  addNote() {
    return Promise.resolve({ code: 200 })
  },

  // 删除笔记
  deleteNote() {
    return Promise.resolve({ code: 200 })
  },

  // 收藏/取消收藏课程
  toggleFavorite() {
    return Promise.resolve({ code: 200 })
  },

  // 获取推荐课程
  getRecommendations(userId) {
    return request({
      url: '/api/recommendation/courses',
      method: 'get',
      params: { userId }
    })
  },

  // 获取热门课程
  getPopularCourses() {
    return request({
      url: '/api/recommendation/popular',
      method: 'get'
    })
  },

  // 课程优化预览
  optimizePreview(params) {
    return request({
      url: '/api/course/optimize-preview',
      method: 'post',
      params
    })
  },

  // AI聊天
  aiChat(data) {
    return request({
      url: '/ai/chat',
      method: 'post',
      data
    })
  },

  // AI优化
  aiOptimize(data) {
    return request({
      url: '/ai/optimize',
      method: 'post',
      data
    })
  },

  // 获取AI状态
  getAiStatus() {
    return request({
      url: '/ai/status',
      method: 'get'
    })
  },

  // 获取相关课程
  getRelatedCourses(courseId) {
    return request({
      url: `/api/search/related/${courseId}`,
      method: 'get'
    })
  },

  // 获取课程评论
  getComments(courseId) {
    return Promise.resolve({
      code: 200,
      data: [
        { id: 1, userName: '小红', content: '课程讲得很棒！', createTime: '2024-06-10 12:00', likeCount: 2 },
        { id: 2, userName: '小李', content: '希望多点实战案例', createTime: '2024-06-10 13:00', likeCount: 1 }
      ]
    })
  },

  // 提交评论
  submitComment(data) {
    return Promise.resolve({ code: 200 })
  },

  // 点赞评论
  likeComment(commentId) {
    return Promise.resolve({ code: 200 })
  },

  // AI问答区mock
  getAiQnaList(courseId) {
    return Promise.resolve({
      code: 200,
      data: [
        { id: 1, question: '本课程适合零基础吗？', answer: 'AI：本课程从基础讲起，零基础同学也能学会。', userName: '小明', createTime: '2024-06-10 10:00' },
        { id: 2, question: '有配套资料吗？', answer: 'AI：课程配有电子讲义和代码示例，可在资料区下载。', userName: '小王', createTime: '2024-06-10 11:00' }
      ]
    })
  },

  // AI提问
  aiAsk(data) {
    return Promise.resolve({ code: 200, data: { answer: 'AI：这是AI的智能回复示例。' } })
  },

  // 获取课程趋势
  getTrends() {
    return request({
      url: '/api/course/trends',
      method: 'get'
    })
  },

  // 获取用户历史
  getUserHistory(userId) {
    return request({
      url: `/api/stats/user/${userId}/history`,
      method: 'get'
    })
  },

  // 获取课程分析
  getCourseAnalytics(courseId) {
    return request({
      url: `/api/stats/course/${courseId}/analytics`,
      method: 'get'
    })
  },

  // 企业端：获取课程列表
  getEnterpriseCourses(params = {}) {
    return request({
      url: '/course/enterprise',
      method: 'get',
      params
    })
  },

  // 企业端：新建课程
  createEnterpriseCourse(data) {
    return request({
      url: '/course/enterprise',
      method: 'post',
      data
    })
  },

  // 企业端：更新课程
  updateEnterpriseCourse(data) {
    return request({
      url: `/course/enterprise/${data.id}`,
      method: 'put',
      data
    })
  },

  // 企业端：删除课程
  deleteEnterpriseCourse(id) {
    return request({
      url: `/course/enterprise/${id}`,
      method: 'delete'
    })
  },

  // 新建课程
  createCourse(data) {
    return request({
      url: '/course/create',
      method: 'post',
      data
    })
  },

  // 更新课程
  updateCourse(id, data) {
    return request({
      url: `/course/${id}`,
      method: 'put',
      data
    })
  },

  // 删除课程
  deleteCourse(id) {
    return request({
      url: `/course/${id}`,
      method: 'delete'
    })
  },

  // 提交课程审核
  submitCourseForReview(id) {
    return request({
      url: `/course/${id}/submit-review`,
      method: 'post'
    })
  },

  // 获取课程发布/审核记录
  getReviewHistory(id) {
    return request({
      url: `/course/${id}/review-history`,
      method: 'get'
    })
  },

  // 下架课程
  unpublishCourse(id) {
    return request({
      url: `/course/${id}/unpublish`,
      method: 'put'
    })
  }
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