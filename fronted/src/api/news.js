import request from '@/utils/request'

// 普通用户API
export const searchNews = (params) => {
    return request({
        url: '/api/news/search',
        method: 'get',
        params
    })
}

export const getNewsDetail = (newsId) => {
    return request({
        url: `/api/news/${newsId}`,
        method: 'get'
    })
}

export const getPopularNews = (limit = 10) => {
    return request({
        url: '/api/news/popular',
        method: 'get',
        params: { limit }
    })
}

// 企业用户API
export const publishNews = (data) => {
    return request({
        url: '/api/enterprise/news',
        method: 'post',
        data
    })
}

export const getMyNewsList = (params) => {
    return request({
        url: '/api/enterprise/news/my',
        method: 'get',
        params
    })
}

export const editNews = (newsId, data) => {
    return request({
        url: `/api/enterprise/news/${newsId}`,
        method: 'put',
        data
    })
}

export const deleteNews = (newsId) => {
    return request({
        url: `/api/enterprise/news/${newsId}`,
        method: 'delete'
    })
}

/**
 * 调用后端AI服务对内容进行润色
 * @param {string} content - 待润色的文本内容
 * @returns {Promise<Object>} 后端返回的Result对象
 */
export const refineContent = (content) => {
    return request({
        url: '/api/enterprise/news/refine-content', // 确保这个URL与你的后端控制器路径一致
        method: 'post',
        data: { content } // 对应后端 RefineRequest 的 content 字段
    })
}

// 管理员API
export const adminPublishNews = (data) => {
    return request({
        url: '/api/admin/news/publish', // This matches the new backend endpoint
        method: 'post',
        data
    })
}

export const getPendingNewsList = (params) => {
    return request({
        url: '/api/admin/news/pending',
        method: 'get',
        params
    })
}

export const auditNews = (newsId, data) => {
    return request({
        url: `/api/admin/news/${newsId}/audit`,
        method: 'post',
        data
    })
}

export const adminEditNews = (newsId, data) => {
    return request({
        url: `/api/admin/news/${newsId}`,
        method: 'put',
        data
    })
}

export const getAllNewsList = (params) => {
    return request({
        url: '/api/admin/news/all',
        method: 'get',
        params
    })
}

export const adminDeleteNews = (newsId) => {
    return request({
        url: `/api/admin/news/${newsId}`,
        method: 'delete'
    })
}

export const getBasicStatistics = () => {
    return request({
        url: '/api/admin/news/statistics/basic',
        method: 'get'
    })
}

export const getViewTrend = (params) => {
    return request({
        url: '/api/admin/news/statistics/view-trend',
        method: 'get',
        params
    })
}

export const getHotNews = (params) => {
    return request({
        url: '/api/admin/news/statistics/hot-news',
        method: 'get',
        params
    })
}

export const batchAuditNews = (newsIds, auditData) => {
    return request({
        url: '/api/admin/news/batch-audit',
        method: 'post',
        data: { newsIds, ...auditData }
    })
}