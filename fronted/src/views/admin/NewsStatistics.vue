<script setup>
import { ref, onMounted, watch } from 'vue'
import { getBasicStatistics, getHotNews } from '@/api/news'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { useRouter } from 'vue-router' // 引入 useRouter

const loading = ref(false)
const statistics = ref({
  totalNews: 0,
  publishedNews: 0,
  pendingNews: 0,
  rejectedNews: 0,
  offlineNews: 0,
  totalViews: 0,
  todayViews: 0,
  totalUsers: 0,
  activeUsers: 0
})

// 获取路由实例
const router = useRouter()

let newsStatusChartInstance = null

const chartData = ref({
  newsStatusChart: {
    labels: ['已发布', '待审核', '审核拒绝'], // 已发布, 待审核, 审核拒绝
    data: [0, 0, 0]
  },
})

const hotNewsList = ref([])

const dateRange = ref([])

const loadBasicStatistics = async () => {
  loading.value = true
  try {
    const data = await getBasicStatistics()
    statistics.value = {
      totalNews: data.totalNews || 0,
      publishedNews: data.publishedNews || 0,
      pendingNews: data.pendingNews || 0,
      rejectedNews: data.rejectedNews || 0,
      offlineNews: data.offlineNews || 0,
      totalViews: data.totalViews || 0,
      todayViews: data.todayViews || 0,
      totalUsers: data.totalUsers || 0,
      activeUsers: 0
    }

    chartData.value.newsStatusChart.data = [
      data.publishedNews || 0,
      data.pendingNews || 0,
      data.rejectedNews || 0
    ]
    updateNewsStatusChart()

  } catch (error) {
    ElMessage.error('加载基础统计数据失败') // 加载基础统计数据失败
  } finally {
    loading.value = false
  }
}

const loadHotNews = async () => {
  loading.value = true
  try {
    const response = await getHotNews()
    console.log('getHotNews API 返回的原始数据 (可能是数组):', response); // getHotNews API 返回的原始数据 (可能是数组)

    hotNewsList.value = response || []
  } catch (error) {
    ElMessage.error('加载热门动态失败') // 加载热门动态失败
  } finally {
    loading.value = false
  }
}

// 修改：点击标题查看动态详情的功能，使用 router.push 进行跳转
const goToNewsDetail = (newsId) => {
  console.log('点击了新闻ID:', newsId); // 点击了新闻ID
  // 使用 router.push 进行导航到 NewsDetail 路由
  router.push({ name: 'NewsDetail', params: { id: newsId } });
  // ElMessage.info(`正在查看新闻ID: ${newsId} 的详情`); // 移除提示，因为会跳转页面
}

const initNewsStatusChart = () => {
  const chartDom = document.getElementById('newsStatusChart')
  if (chartDom) {
    newsStatusChartInstance = echarts.init(chartDom)
    updateNewsStatusChart()
  }
}

const updateNewsStatusChart = () => {
  if (!newsStatusChartInstance) {
    initNewsStatusChart();
    return;
  }
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b} : {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: chartData.value.newsStatusChart.labels
    },
    series: [
      {
        name: '动态状态', // 动态状态
        type: 'pie',
        radius: '70%',
        center: ['60%', '50%'],
        data: chartData.value.newsStatusChart.labels.map((label, index) => ({
          value: chartData.value.newsStatusChart.data[index],
          name: label,
          itemStyle: {
            color: index === 0 ? '#67c23a' : // 已发布
                index === 1 ? '#e6a23c' : // 待审核
                    '#f56c6c' // 审核拒绝
          }
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  newsStatusChartInstance.setOption(option)
}

const refreshData = () => {
  const end = new Date();
  const start = new Date();
  start.setDate(start.getDate() - 6);
  dateRange.value = [start, end];

  loadBasicStatistics()
  loadHotNews()
}

const formatDate = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

onMounted(() => {
  refreshData()
  window.addEventListener('resize', () => {
    newsStatusChartInstance && newsStatusChartInstance.resize()
  })
})

</script>

<template>
  <div class="statistics" v-loading="loading">
    <div class="stats-header">
      <h2>数据统计与分析</h2> <!-- 数据统计与分析 -->
      <el-button type="primary" @click="refreshData" :loading="loading">
        <el-icon><Refresh /></el-icon> 刷新数据 <!-- 刷新数据 -->
      </el-button>
    </div>

    <el-row :gutter="20" class="stats-cards">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon news-icon">
              <el-icon size="40"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <h3>{{ statistics.totalNews }}</h3>
              <p>总动态数</p> <!-- 总动态数 -->
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon views-icon">
              <el-icon size="40"><View /></el-icon>
            </div>
            <div class="stat-info">
              <h3>{{ statistics.totalViews }}</h3>
              <p>总浏览量</p> <!-- 总浏览量 -->
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon users-icon">
              <el-icon size="40"><User /></el-icon>
            </div>
            <div class="stat-info">
              <h3>{{ statistics.totalUsers }}</h3>
              <p>总用户数</p> <!-- 总用户数 -->
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>动态状态分布</span> <!-- 动态状态分布 -->
          </template>
          <div class="chart-container">
            <div id="newsStatusChart" class="echart"></div>
            <div v-if="statistics.totalNews === 0" class="empty-chart-overlay">
              <el-empty description="暂无动态数据" /> <!-- 暂无动态数据 -->
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <span>用户活跃度 & 今日概览</span> <!-- 用户活跃度 & 今日概览 -->
          </template>

          <div class="user-stats">
            <el-row>
              <el-col :span="12">
                <div class="user-stat-item">
                  <h3>{{ statistics.activeUsers }}</h3>
                  <p>活跃用户</p> <!-- 活跃用户 -->
                </div>
              </el-col>
              <el-col :span="12">
                <div class="user-stat-item">
                  <h3>{{ statistics.todayViews }}</h3>
                  <p>今日浏览量</p> <!-- 今日浏览量 -->
                </div>
              </el-col>
            </el-row>

            <el-divider />

            <el-row>
              <el-col :span="12">
                <div class="user-stat-item">
                  <h3>{{ Math.round(statistics.totalViews / statistics.totalNews) || 0 }}</h3>
                  <p>平均浏览量</p> <!-- 平均浏览量 -->
                </div>
              </el-col>
              <el-col :span="12">
                <div class="user-stat-item">
                  <h3>{{ statistics.rejectedNews }}</h3>
                  <p>审核拒绝动态</p> <!-- 审核拒绝动态 -->
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>内容热度排行 (前10)</span> <!-- 内容热度排行 (前10) -->
          </template>
          <div class="hot-news-list">
            <el-table :data="hotNewsList" style="width: 100%" v-if="hotNewsList.length > 0">
              <el-table-column type="index" label="排名" width="80"></el-table-column> <!-- 排名 -->
              <el-table-column prop="title" label="动态标题"> <!-- 动态标题 -->
                <template #default="scope">
                  <el-link type="primary" @click="goToNewsDetail(scope.row.id)">
                    {{ scope.row.title }}
                  </el-link>
                </template>
              </el-table-column>
              <el-table-column prop="viewCount" label="浏览量" width="120" sortable></el-table-column> <!-- 浏览量 -->
            </el-table>
            <el-empty v-else description="暂无热门动态" /> <!-- 暂无热门动态 -->
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.statistics {
  padding: 20px;
}

.stats-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.stats-header h2 {
  margin: 0;
  color: #303133;
}

.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  height: 120px;
}

.stat-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.news-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.views-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  color: white;
}

.users-icon {
  background: linear-gradient(135deg, #ffc371 0%, #ff5e62 100%);
  color: white;
}

.stat-info h3 {
  margin: 0 0 5px 0;
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-info p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.chart-container {
  position: relative;
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.echart {
  width: 100%;
  height: 100%;
}

.empty-chart-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
}

.user-stats {
  padding: 20px 0;
}

.user-stat-item {
  text-align: center;
}

.user-stat-item h3 {
  margin: 0 0 5px 0;
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.user-stat-item p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.trend-container {
  min-height: 200px;
}

.card-header-with-filter {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.card-header-with-filter span {
  font-weight: bold;
}

.hot-news-list {
  min-height: 200px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.el-empty {
  --el-empty-image-width: 100px;
  margin: auto;
}
</style>
