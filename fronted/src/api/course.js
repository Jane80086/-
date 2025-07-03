import request from './index'
export const getCourseList = (params) => request.get('/api/course/list', { params })
export const getCourseDetail = (id) => request.get(`/api/course/${id}`) 