import request from '@/utils/request'

const statsApi = {
  // 获取统计数据概览
  getStatsOverview() {
    return request({
      url: '/api/stats/overview',
      method: 'get'
    })
  },
  // 获取图表数据
  getChartData() {
    return request({
      url: '/api/stats/chart',
      method: 'get'
    })
  },
  // 获取仪表板数据
  getDashboardStats() {
    return request({
      url: '/api/stats/dashboard',
      method: 'get'
    })
  },
  // 获取系统健康状态
  getSystemHealth() {
    return request({
      url: '/api/stats/health',
      method: 'get'
    })
  },
  // 获取收入统计
  getRevenueStats() {
    return request({
      url: '/api/stats/revenue',
      method: 'get'
    })
  },
  // 获取热门趋势
  getTrends() {
    return request({
      url: '/api/stats/trends',
      method: 'get'
    })
  }
}

export default statsApi 