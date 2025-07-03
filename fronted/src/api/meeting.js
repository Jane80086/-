import request from './index'
export const getMeetingList = (params) => request.get('/api/meeting/list', { params })
export const getMeetingDetail = (id) => request.get(`/api/meeting/${id}`) 