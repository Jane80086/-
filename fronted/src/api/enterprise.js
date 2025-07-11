import request from '@/utils/request'

const enterpriseApi = {
  // 获取企业列表
  getEnterprises(params) {
    // 自动补全sortField参数，默认按createTime排序
    const finalParams = { sortField: 'createTime', ...params }
    return request.get('/enterprise/list', { params: finalParams })
  },
  // 创建企业
  createEnterprise(data) {
    return request.post('/enterprise/create', data)
  },
  // 更新企业
  updateEnterprise(id, data) {
    return request.put(`/enterprise/update/${id}`, data)
  },
  // 删除企业
  deleteEnterprise(id) {
    return request.delete(`/enterprise/delete/${id}`)
  },
  // 导出企业列表
  exportEnterprises(params) {
    return request.get('/enterprise/export', { params, responseType: 'blob' })
  },
  // 同步企业信息（按名称）
  syncEnterpriseInfo(name) {
    return request.post('/enterprise/sync', { name })
  },
  // 同步企业信息（按ID）
  syncEnterpriseInfoById(id) {
    return request.post(`/enterprise/sync/${id}`)
  },
  // 课程相关（保留原有）
  getCourseList() {
    return request.get('/enterprise/courses')
  }
}

export default enterpriseApi 