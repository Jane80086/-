import request from '@/utils/request'

const enterpriseApi = {
  getCourseList() {
    return request.get('/api/enterprise/courses')
  }
}

export default enterpriseApi 