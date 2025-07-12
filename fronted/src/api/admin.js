import request from '@/utils/request'

const adminApi = {
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
  reviewCourse(courseId, status, reason = '', reviewerId) {
    const url = `/api/admin/course/${courseId}/review`
    const data = { status, reason, reviewerId }
    console.log('[审核请求] POST', url, data)
    return request({
      url,
      method: 'post',
      data
    }).then(res => {
      console.log('[审核响应]', res)
      return res
    }).catch(err => {
      console.error('[审核异常]', err)
      throw err
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

export default adminApi 