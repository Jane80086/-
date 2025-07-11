import request from '@/utils/request'

const enterpriseApi = {
  // 获取企业列表
  getEnterprises(params) {
    return request.get('/api/enterprise/list', { params })
  },
  // 创建企业
  createEnterprise(data) {
    return request.post('/api/enterprise/create', data)
  },
  // 更新企业
  updateEnterprise(id, data) {
    return request.put(`/api/enterprise/update/${id}`, data)
  },
  // 删除企业
  deleteEnterprise(id) {
    return request.delete(`/api/enterprise/delete/${id}`)
  },
  // 导出企业列表
  exportEnterprises(params) {
    return request.get('/api/enterprise/export', { params, responseType: 'blob' })
  },
  // 同步企业信息（按名称）
  syncEnterpriseInfo(name) {
    return request.post('/api/enterprise/sync', { name })
  },
  // 同步企业信息（按ID）
  syncEnterpriseInfoById(id) {
    return request.post(`/api/enterprise/sync/${id}`)
  },
  // 课程相关（保留原有）
  getCourseList() {
    return request.get('/api/enterprise/courses')
  }
}

export default enterpriseApi 