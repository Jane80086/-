import request from './index'

export const userRoleAPI = {
  getUserRoles(userId) {
    return request.get(`/api/admin/users/${userId}/roles`)
  },
  assignRoles(userId, roleIds) {
    return request.post(`/api/admin/users/${userId}/roles`, roleIds)
  }
} 