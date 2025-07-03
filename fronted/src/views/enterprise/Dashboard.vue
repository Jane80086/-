<template>
  <div class="enterprise-dashboard">
    <!-- 企业信息头部 -->
    <el-card class="enterprise-header">
      <div class="enterprise-info">
        <div class="enterprise-logo">
          <el-avatar :size="80" src="https://via.placeholder.com/80x80" />
        </div>
        <div class="enterprise-details">
          <h2>{{ enterpriseInfo.name }}</h2>
          <p class="enterprise-desc">{{ enterpriseInfo.description }}</p>
          <div class="enterprise-stats">
            <div class="stat-item">
              <div class="stat-number">{{ enterpriseStats.employeeCount }}</div>
              <div class="stat-label">员工数量</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ enterpriseStats.courseCount }}</div>
              <div class="stat-label">课程数量</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ enterpriseStats.completionRate }}%</div>
              <div class="stat-label">完成率</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ enterpriseStats.activeUsers }}</div>
              <div class="stat-label">活跃用户</div>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 数据概览 -->
    <el-row :gutter="20" class="overview-section">
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="overview-content">
            <div class="overview-icon" style="background: #409eff;">
              <el-icon><UserFilled /></el-icon>
            </div>
            <div class="overview-info">
              <div class="overview-number">{{ overviewData.totalEmployees }}</div>
              <div class="overview-label">总员工数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="overview-content">
            <div class="overview-icon" style="background: #67c23a;">
              <el-icon><VideoPlay /></el-icon>
            </div>
            <div class="overview-info">
              <div class="overview-number">{{ overviewData.totalCourses }}</div>
              <div class="overview-label">总课程数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="overview-content">
            <div class="overview-icon" style="background: #e6a23c;">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="overview-info">
              <div class="overview-number">{{ overviewData.totalHours }}</div>
              <div class="overview-label">总学习时长</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="overview-content">
            <div class="overview-icon" style="background: #f56c6c;">
              <el-icon><Trophy /></el-icon>
            </div>
            <div class="overview-info">
              <div class="overview-number">{{ overviewData.avgScore }}</div>
              <div class="overview-label">平均分数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 主要内容区域 -->
    <el-row :gutter="20" class="main-content">
      <!-- 学习进度图表 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>学习进度趋势</span>
          </template>
          <div class="chart-container">
            <div ref="progressChart" style="height: 300px;"></div>
          </div>
        </el-card>
      </el-col>

      <!-- 热门课程 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>热门课程</span>
          </template>
          <div class="hot-courses">
            <div 
              v-for="course in hotCourses" 
              :key="course.id"
              class="hot-course-item"
            >
              <div class="course-rank">{{ course.rank }}</div>
              <div class="course-info">
                <h4>{{ course.title }}</h4>
                <p>{{ course.instructor }}</p>
              </div>
              <div class="course-stats">
                <span class="enrollment">{{ course.enrollment }}人</span>
                <span class="rating">{{ course.rating }}分</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 部门学习情况 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>部门学习情况</span>
          </template>
          <el-table :data="departmentStats" style="width: 100%">
            <el-table-column prop="department" label="部门" />
            <el-table-column prop="employeeCount" label="员工数" />
            <el-table-column prop="enrolledCount" label="已报名" />
            <el-table-column prop="completionRate" label="完成率">
              <template #default="scope">
                <el-progress 
                  :percentage="scope.row.completionRate" 
                  :color="getProgressColor(scope.row.completionRate)"
                />
              </template>
            </el-table-column>
            <el-table-column prop="avgScore" label="平均分数" />
            <el-table-column label="操作">
              <template #default="scope">
                <el-button size="small" @click="viewDepartmentDetail(scope.row)">
                  查看详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近活动 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>最近活动</span>
          </template>
          <div class="activity-list">
            <div 
              v-for="activity in recentActivities" 
              :key="activity.id"
              class="activity-item"
            >
              <div class="activity-icon">
                <el-icon :color="activity.color">
                  <component :is="activity.icon" />
                </el-icon>
              </div>
              <div class="activity-content">
                <div class="activity-title">{{ activity.title }}</div>
                <div class="activity-time">{{ activity.time }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- AI洞察 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>AI洞察</span>
          </template>
          <div class="ai-insights">
            <div 
              v-for="insight in aiInsights" 
              :key="insight.id"
              class="insight-item"
            >
              <div class="insight-icon">
                <el-icon color="#409eff"><Lightbulb /></el-icon>
              </div>
              <div class="insight-content">
                <div class="insight-title">{{ insight.title }}</div>
                <div class="insight-desc">{{ insight.description }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { 
  UserFilled, 
  VideoPlay, 
  Clock, 
  Trophy,
  Lightbulb
} from '@element-plus/icons-vue'

const router = useRouter()

// 企业信息
const enterpriseInfo = ref({
  name: '示例企业有限公司',
  description: '专注于技术培训的企业学习平台'
})

// 企业统计数据
const enterpriseStats = ref({
  employeeCount: 156,
  courseCount: 24,
  completionRate: 78,
  activeUsers: 89
})

// 概览数据
const overviewData = ref({
  totalEmployees: 156,
  totalCourses: 24,
  totalHours: 2847,
  avgScore: 8.6
})

// 热门课程
const hotCourses = ref([
  { id: 1, rank: 1, title: 'Vue.js 企业级开发', instructor: '张老师', enrollment: 45, rating: 9.2 },
  { id: 2, rank: 2, title: 'Spring Boot 实战', instructor: '李老师', enrollment: 38, rating: 8.9 },
  { id: 3, rank: 3, title: 'Docker 容器化部署', instructor: '王老师', enrollment: 32, rating: 8.7 },
  { id: 4, rank: 4, title: '微服务架构设计', instructor: '赵老师', enrollment: 28, rating: 8.5 },
  { id: 5, rank: 5, title: 'React 前端开发', instructor: '孙老师', enrollment: 25, rating: 8.3 }
])

// 部门统计
const departmentStats = ref([
  { department: '技术部', employeeCount: 45, enrolledCount: 42, completionRate: 85, avgScore: 8.8 },
  { department: '产品部', employeeCount: 23, enrolledCount: 20, completionRate: 78, avgScore: 8.2 },
  { department: '运营部', employeeCount: 18, enrolledCount: 15, completionRate: 72, avgScore: 7.9 },
  { department: '市场部', employeeCount: 15, enrolledCount: 12, completionRate: 68, avgScore: 7.6 },
  { department: '人事部', employeeCount: 8, enrolledCount: 6, completionRate: 75, avgScore: 8.1 }
])

// 最近活动
const recentActivities = ref([
  { id: 1, title: '张三完成了Vue.js课程', time: '2小时前', icon: 'VideoPlay', color: '#67c23a' },
  { id: 2, title: '李四报名了Spring Boot课程', time: '4小时前', icon: 'UserFilled', color: '#409eff' },
  { id: 3, title: '王五获得了Docker认证', time: '6小时前', icon: 'Trophy', color: '#e6a23c' },
  { id: 4, title: '赵六完成了微服务课程', time: '8小时前', icon: 'VideoPlay', color: '#67c23a' },
  { id: 5, title: '新增了React课程', time: '1天前', icon: 'Plus', color: '#f56c6c' }
])

// AI洞察
const aiInsights = ref([
  { 
    id: 1, 
    title: '学习效率提升建议', 
    description: '技术部员工在Vue.js课程上表现优秀，建议增加相关进阶课程' 
  },
  { 
    id: 2, 
    title: '课程优化建议', 
    description: 'Spring Boot课程完成率较低，建议调整课程难度和时长' 
  },
  { 
    id: 3, 
    title: '员工参与度分析', 
    description: '运营部员工参与度有待提升，建议增加激励机制' 
  }
])

// 获取进度条颜色
const getProgressColor = (percentage) => {
  if (percentage >= 80) return '#67c23a'
  if (percentage >= 60) return '#e6a23c'
  return '#f56c6c'
}

// 查看部门详情
const viewDepartmentDetail = (department) => {
  ElMessage.info(`查看${department.department}详情`)
  // 这里可以跳转到部门详情页面
}

// 初始化图表
const progressChart = ref(null)
let chartInstance = null

const initChart = () => {
  if (progressChart.value) {
    chartInstance = echarts.init(progressChart.value)
    
    const option = {
      title: {
        text: '学习进度趋势',
        left: 'center'
      },
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['完成率', '参与度'],
        bottom: 10
      },
      xAxis: {
        type: 'category',
        data: ['1月', '2月', '3月', '4月', '5月', '6月']
      },
      yAxis: {
        type: 'value',
        max: 100
      },
      series: [
        {
          name: '完成率',
          type: 'line',
          data: [65, 72, 78, 82, 85, 88],
          smooth: true,
          itemStyle: { color: '#409eff' }
        },
        {
          name: '参与度',
          type: 'line',
          data: [45, 58, 67, 75, 82, 89],
          smooth: true,
          itemStyle: { color: '#67c23a' }
        }
      ]
    }
    
    chartInstance.setOption(option)
  }
}

// 组件挂载时初始化
onMounted(() => {
  nextTick(() => {
    initChart()
  })
  
  // 监听窗口大小变化
  window.addEventListener('resize', () => {
    if (chartInstance) {
      chartInstance.resize()
    }
  })
})
</script>

<style scoped>
.enterprise-dashboard {
  padding: 20px;
}

.enterprise-header {
  margin-bottom: 20px;
}

.enterprise-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.enterprise-details {
  flex: 1;
}

.enterprise-details h2 {
  margin: 0 0 8px 0;
  color: #333;
}

.enterprise-desc {
  margin: 0 0 16px 0;
  color: #666;
}

.enterprise-stats {
  display: flex;
  gap: 30px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}

.overview-section {
  margin-bottom: 20px;
}

.overview-card {
  height: 100px;
}

.overview-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.overview-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.overview-icon .el-icon {
  font-size: 24px;
  color: white;
}

.overview-number {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  line-height: 1;
}

.overview-label {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

.main-content {
  margin-bottom: 20px;
}

.chart-container {
  height: 300px;
}

.hot-courses {
  max-height: 300px;
  overflow-y: auto;
}

.hot-course-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.hot-course-item:last-child {
  border-bottom: none;
}

.course-rank {
  width: 30px;
  height: 30px;
  background: #409eff;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin-right: 12px;
}

.course-info {
  flex: 1;
}

.course-info h4 {
  margin: 0 0 4px 0;
  font-size: 14px;
  color: #333;
}

.course-info p {
  margin: 0;
  font-size: 12px;
  color: #666;
}

.course-stats {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.enrollment {
  font-size: 12px;
  color: #409eff;
}

.rating {
  font-size: 12px;
  color: #e6a23c;
}

.activity-list {
  max-height: 300px;
  overflow-y: auto;
}

.activity-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-icon {
  margin-right: 12px;
}

.activity-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}

.activity-time {
  font-size: 12px;
  color: #999;
}

.ai-insights {
  max-height: 300px;
  overflow-y: auto;
}

.insight-item {
  display: flex;
  align-items: flex-start;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.insight-item:last-child {
  border-bottom: none;
}

.insight-icon {
  margin-right: 12px;
  margin-top: 2px;
}

.insight-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
  font-weight: 500;
}

.insight-desc {
  font-size: 12px;
  color: #666;
  line-height: 1.4;
}
</style> 