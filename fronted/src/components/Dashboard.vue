<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon" style="background: #409eff;">
              <el-icon><List /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ statsData.totalCourses || 0 }}</div>
              <div class="stats-label">总课程数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon" style="background: #67c23a;">
              <el-icon><User /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ statsData.totalUsers || 0 }}</div>
              <div class="stats-label">注册用户</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon" style="background: #e6a23c;">
              <el-icon><View /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ statsData.totalViews || 0 }}</div>
              <div class="stats-label">总浏览量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon" style="background: #f56c6c;">
              <el-icon><Star /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ statsData.avgRating || 0 }}</div>
              <div class="stats-label">平均评分</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 课程列表 -->
    <el-row :gutter="20" class="content-row">
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最新课程</span>
              <el-button type="primary" size="small" @click="handleCreateCourse">
                创建课程
              </el-button>
            </div>
          </template>
          
          <el-table 
            :data="courseData" 
            v-loading="loading"
            style="width: 100%"
            @row-click="handleCourseClick"
          >
            <el-table-column prop="title" label="课程名称" min-width="200">
              <template #default="{ row }">
                <div class="course-title">
                  <el-avatar 
                    :src="row.coverImage" 
                    :size="40"
                    style="margin-right: 12px;"
                  />
                  <div>
                    <div class="course-name">{{ row.title }}</div>
                    <div class="course-instructor">{{ row.instructorName }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="category" label="分类" width="120" />
            <el-table-column prop="price" label="价格" width="100">
              <template #default="{ row }">
                <span v-if="row.price > 0" class="price">¥{{ row.price }}</span>
                <span v-else class="free">免费</span>
              </template>
            </el-table-column>
            <el-table-column prop="rating" label="评分" width="120">
              <template #default="{ row }">
                <el-rate 
                  v-model="row.rating" 
                  disabled 
                  show-score 
                  text-color="#ff9900"
                  score-template="{value}"
                />
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag 
                  :type="getStatusType(row.status)"
                  size="small"
                >
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click.stop="handleEdit(row)">
                  编辑
                </el-button>
                <el-button type="danger" size="small" @click.stop="handleDelete(row)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>热门课程</span>
          </template>
          <div class="hot-courses">
            <div 
              v-for="(course, index) in hotCourses" 
              :key="course.id"
              class="hot-course-item"
              @click="handleCourseClick(course)"
            >
              <div class="hot-course-rank">{{ index + 1 }}</div>
              <div class="hot-course-info">
                <div class="hot-course-title">{{ course.title }}</div>
                <div class="hot-course-stats">
                  <span>{{ course.viewCount }} 次观看</span>
                  <el-rate v-model="course.rating" disabled size="small" />
                </div>
              </div>
            </div>
          </div>
        </el-card>
        
        <el-card style="margin-top: 20px;">
          <template #header>
            <span>快速操作</span>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="handleCreateCourse" style="width: 100%; margin-bottom: 10px;">
              <el-icon><Plus /></el-icon>
              创建新课程
            </el-button>
            <el-button @click="handleImportCourse" style="width: 100%; margin-bottom: 10px;">
              <el-icon><Upload /></el-icon>
              导入课程
            </el-button>
            <el-button @click="handleExportData" style="width: 100%;">
              <el-icon><Download /></el-icon>
              导出数据
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  List, 
  User, 
  View, 
  Star, 
  Plus, 
  Upload, 
  Download 
} from '@element-plus/icons-vue'
import { courseAPI } from '../api'

const props = defineProps({
  courseData: {
    type: Array,
    default: () => []
  },
  statsData: {
    type: Object,
    default: () => ({})
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['refresh'])

// 热门课程（模拟数据）
const hotCourses = ref([
  {
    id: 1,
    title: 'Vue.js 完全指南',
    viewCount: 1250,
    rating: 4.8
  },
  {
    id: 2,
    title: 'Spring Boot 实战',
    viewCount: 980,
    rating: 4.6
  },
  {
    id: 3,
    title: 'Python 数据分析',
    viewCount: 756,
    rating: 4.7
  },
  {
    id: 4,
    title: 'React 高级教程',
    viewCount: 632,
    rating: 4.5
  },
  {
    id: 5,
    title: 'Docker 容器化部署',
    viewCount: 589,
    rating: 4.4
  }
])

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    'draft': 'info',
    'published': 'success',
    'pending': 'warning',
    'rejected': 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    'draft': '草稿',
    'published': '已发布',
    'pending': '审核中',
    'rejected': '已拒绝'
  }
  return statusMap[status] || '未知'
}

// 处理课程点击
const handleCourseClick = (course) => {
  ElMessage.info(`点击了课程: ${course.title}`)
  // 这里可以跳转到课程详情页
}

// 处理创建课程
const handleCreateCourse = () => {
  ElMessage.info('跳转到创建课程页面')
  // 这里可以跳转到创建课程页面
}

// 处理编辑课程
const handleEdit = (course) => {
  ElMessage.info(`编辑课程: ${course.title}`)
  // 这里可以跳转到编辑页面
}

// 处理删除课程
const handleDelete = async (course) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除课程 "${course.title}" 吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await courseAPI.deleteCourse(course.id)
    ElMessage.success('删除成功')
    emit('refresh')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 处理导入课程
const handleImportCourse = () => {
  ElMessage.info('导入课程功能')
}

// 处理导出数据
const handleExportData = () => {
  ElMessage.info('导出数据功能')
}
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  height: 120px;
}

.stats-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.stats-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  color: white;
  font-size: 24px;
}

.stats-info {
  flex: 1;
}

.stats-number {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  line-height: 1;
}

.stats-label {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

.content-row {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.course-title {
  display: flex;
  align-items: center;
}

.course-name {
  font-weight: 500;
  color: #333;
}

.course-instructor {
  font-size: 12px;
  color: #666;
  margin-top: 2px;
}

.price {
  color: #f56c6c;
  font-weight: 500;
}

.free {
  color: #67c23a;
  font-weight: 500;
}

.hot-courses {
  max-height: 400px;
  overflow-y: auto;
}

.hot-course-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.3s;
}

.hot-course-item:hover {
  background-color: #f5f7fa;
}

.hot-course-item:last-child {
  border-bottom: none;
}

.hot-course-rank {
  width: 24px;
  height: 24px;
  background: #409eff;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  margin-right: 12px;
}

.hot-course-info {
  flex: 1;
}

.hot-course-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
  line-height: 1.4;
}

.hot-course-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #666;
}

.quick-actions {
  display: flex;
  flex-direction: column;
}
</style> 