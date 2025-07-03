<template>
  <div class="learning-progress">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>学习进度</h1>
      <p>跟踪您的学习进度和完成情况</p>
    </div>

    <!-- 进度概览 -->
    <el-row :gutter="20" class="progress-overview">
      <el-col :span="6">
        <el-card class="progress-card">
          <div class="progress-content">
            <div class="progress-icon" style="background: #409eff;">
              <el-icon><VideoPlay /></el-icon>
            </div>
            <div class="progress-info">
              <div class="progress-number">{{ overview.totalCourses }}</div>
              <div class="progress-label">总课程数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="progress-card">
          <div class="progress-content">
            <div class="progress-icon" style="background: #67c23a;">
              <el-icon><Check /></el-icon>
            </div>
            <div class="progress-info">
              <div class="progress-number">{{ overview.completedCourses }}</div>
              <div class="progress-label">已完成</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="progress-card">
          <div class="progress-content">
            <div class="progress-icon" style="background: #e6a23c;">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="progress-info">
              <div class="progress-number">{{ overview.totalHours }}</div>
              <div class="progress-label">学习时长(小时)</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="progress-card">
          <div class="progress-content">
            <div class="progress-icon" style="background: #f56c6c;">
              <el-icon><Trophy /></el-icon>
            </div>
            <div class="progress-info">
              <div class="progress-number">{{ overview.avgScore }}</div>
              <div class="progress-label">平均分数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 学习进度列表 -->
    <el-card class="progress-list-card">
      <template #header>
        <div class="card-header">
          <span>课程学习进度</span>
          <el-button type="primary" @click="refreshProgress">
            <el-icon><Refresh /></el-icon>
            刷新进度
          </el-button>
        </div>
      </template>

      <el-table :data="courseProgress" style="width: 100%" v-loading="loading">
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="instructor" label="讲师" width="120" />
        <el-table-column prop="category" label="分类" width="100">
          <template #default="scope">
            <el-tag :type="getCategoryType(scope.row.category)">
              {{ scope.row.category }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="进度" width="200">
          <template #default="scope">
            <el-progress 
              :percentage="scope.row.progress" 
              :color="getProgressColor(scope.row.progress)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="lastStudyTime" label="最后学习时间" width="160" />
        <el-table-column prop="score" label="得分" width="80">
          <template #default="scope">
            <span :style="{ color: getScoreColor(scope.row.score) }">
              {{ scope.row.score || '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="continueLearning(scope.row)">
              继续学习
            </el-button>
            <el-button size="small" @click="viewDetail(scope.row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 学习统计图表 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>学习时长统计</span>
          </template>
          <div ref="timeChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>完成率趋势</span>
          </template>
          <div ref="completionChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 学习成就 -->
    <el-card style="margin-top: 20px;">
      <template #header>
        <span>学习成就</span>
      </template>
      <div class="achievements">
        <div 
          v-for="achievement in achievements" 
          :key="achievement.id"
          class="achievement-item"
          :class="{ 'achieved': achievement.achieved }"
        >
          <div class="achievement-icon">
            <el-icon :color="achievement.achieved ? '#67c23a' : '#c0c4cc'">
              <component :is="achievement.icon" />
            </el-icon>
          </div>
          <div class="achievement-info">
            <div class="achievement-title">{{ achievement.title }}</div>
            <div class="achievement-desc">{{ achievement.description }}</div>
          </div>
          <div class="achievement-status">
            <el-tag :type="achievement.achieved ? 'success' : 'info'">
              {{ achievement.achieved ? '已获得' : '未获得' }}
            </el-tag>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { 
  VideoPlay, 
  Check, 
  Clock, 
  Trophy,
  Refresh
} from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)

// 进度概览数据
const overview = ref({
  totalCourses: 12,
  completedCourses: 8,
  totalHours: 156,
  avgScore: 8.6
})

// 课程进度列表
const courseProgress = ref([
  {
    id: 1,
    courseName: 'Vue.js 完全指南 2024',
    instructor: '张老师',
    category: 'programming',
    progress: 85,
    lastStudyTime: '2024-07-02 15:30:00',
    score: 9.2
  },
  {
    id: 2,
    courseName: 'Spring Boot 实战开发',
    instructor: '李老师',
    category: 'programming',
    progress: 60,
    lastStudyTime: '2024-07-01 14:20:00',
    score: 8.5
  },
  {
    id: 3,
    courseName: 'UI/UX 设计基础',
    instructor: '王老师',
    category: 'design',
    progress: 100,
    lastStudyTime: '2024-06-30 16:45:00',
    score: 9.0
  },
  {
    id: 4,
    courseName: 'Docker 容器化部署',
    instructor: '赵老师',
    category: 'programming',
    progress: 30,
    lastStudyTime: '2024-06-29 10:15:00',
    score: null
  }
])

// 学习成就
const achievements = ref([
  {
    id: 1,
    title: '学习新手',
    description: '完成第一门课程',
    icon: 'Trophy',
    achieved: true
  },
  {
    id: 2,
    title: '坚持不懈',
    description: '连续学习7天',
    icon: 'Clock',
    achieved: true
  },
  {
    id: 3,
    title: '编程达人',
    description: '完成5门编程课程',
    icon: 'VideoPlay',
    achieved: false
  },
  {
    id: 4,
    title: '高分学员',
    description: '获得3门课程满分',
    icon: 'Check',
    achieved: false
  }
])

// 获取分类类型
const getCategoryType = (category) => {
  const typeMap = {
    programming: 'success',
    design: 'warning',
    business: 'primary',
    language: 'info'
  }
  return typeMap[category] || ''
}

// 获取进度条颜色
const getProgressColor = (progress) => {
  if (progress >= 80) return '#67c23a'
  if (progress >= 60) return '#e6a23c'
  return '#f56c6c'
}

// 获取分数颜色
const getScoreColor = (score) => {
  if (!score) return '#999'
  if (score >= 9) return '#67c23a'
  if (score >= 8) return '#e6a23c'
  return '#f56c6c'
}

// 继续学习
const continueLearning = (course) => {
  ElMessage.success(`继续学习：${course.courseName}`)
  router.push(`/course/${course.id}`)
}

// 查看详情
const viewDetail = (course) => {
  ElMessage.info(`查看详情：${course.courseName}`)
  // 这里可以跳转到课程详情页面
}

// 刷新进度
const refreshProgress = async () => {
  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('进度已刷新')
  } catch (error) {
    ElMessage.error('刷新失败')
  } finally {
    loading.value = false
  }
}

// 图表实例
let timeChartInstance = null
let completionChartInstance = null

// 初始化学习时长图表
const timeChart = ref(null)
const initTimeChart = () => {
  if (timeChart.value) {
    timeChartInstance = echarts.init(timeChart.value)
    
    const option = {
      title: {
        text: '每日学习时长',
        left: 'center'
      },
      tooltip: {
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
      },
      yAxis: {
        type: 'value',
        name: '小时'
      },
      series: [
        {
          name: '学习时长',
          type: 'bar',
          data: [2.5, 3.2, 1.8, 4.1, 2.9, 5.2, 3.8],
          itemStyle: { color: '#409eff' }
        }
      ]
    }
    
    timeChartInstance.setOption(option)
  }
}

// 初始化完成率图表
const completionChart = ref(null)
const initCompletionChart = () => {
  if (completionChart.value) {
    completionChartInstance = echarts.init(completionChart.value)
    
    const option = {
      title: {
        text: '课程完成率趋势',
        left: 'center'
      },
      tooltip: {
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        data: ['1月', '2月', '3月', '4月', '5月', '6月']
      },
      yAxis: {
        type: 'value',
        max: 100,
        name: '完成率(%)'
      },
      series: [
        {
          name: '完成率',
          type: 'line',
          data: [20, 35, 45, 60, 75, 85],
          smooth: true,
          itemStyle: { color: '#67c23a' },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0, y: 0, x2: 0, y2: 1,
              colorStops: [
                { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
                { offset: 1, color: 'rgba(103, 194, 58, 0.1)' }
              ]
            }
          }
        }
      ]
    }
    
    completionChartInstance.setOption(option)
  }
}

// 组件挂载时初始化
onMounted(() => {
  nextTick(() => {
    initTimeChart()
    initCompletionChart()
  })
  
  // 监听窗口大小变化
  window.addEventListener('resize', () => {
    if (timeChartInstance) {
      timeChartInstance.resize()
    }
    if (completionChartInstance) {
      completionChartInstance.resize()
    }
  })
})
</script>

<style scoped>
.learning-progress {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
  text-align: center;
}

.page-header h1 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 28px;
}

.page-header p {
  margin: 0;
  color: #666;
  font-size: 16px;
}

.progress-overview {
  margin-bottom: 20px;
}

.progress-card {
  height: 100px;
}

.progress-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.progress-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.progress-icon .el-icon {
  font-size: 24px;
  color: white;
}

.progress-number {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  line-height: 1;
}

.progress-label {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

.progress-list-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.achievements {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 16px;
}

.achievement-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  transition: all 0.3s;
}

.achievement-item:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.achievement-item.achieved {
  border-color: #67c23a;
  background: rgba(103, 194, 58, 0.05);
}

.achievement-icon {
  margin-right: 16px;
}

.achievement-icon .el-icon {
  font-size: 24px;
}

.achievement-info {
  flex: 1;
}

.achievement-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.achievement-desc {
  font-size: 14px;
  color: #666;
}

.achievement-status {
  margin-left: 16px;
}
</style> 