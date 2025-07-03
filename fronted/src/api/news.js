import request from './index'
export const getNewsList = (params) => request.get('/api/news/list', { params })
export const getNewsDetail = (id) => request.get(`/api/news/${id}`) 