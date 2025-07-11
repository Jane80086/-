import request from './index'

export const roleAPI = {
  getRoles() {
    return request.get('/api/admin/roles')
  },
  createRole(data) {
    return request.post('/api/admin/roles', data)
  },
  updateRole(id, data) {
    return request.put(`/api/admin/roles/${id}`, data)
  },
  deleteRole(id) {
    return request.delete(`/api/admin/roles/${id}`)
  },
  getRolePermissions(id) {
    return request.get(`/api/admin/roles/${id}/permissions`)
  },
  assignPermissions(id, permissionIds) {
    return request.post(`/api/admin/roles/${id}/permissions`, permissionIds)
  }
} 