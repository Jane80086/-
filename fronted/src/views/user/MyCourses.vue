<template>
  <div class="my-courses-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1>我的课程</h1>
        <p class="subtitle">管理您的学习进度和课程收藏</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="$router.push('/user/course')">
          <el-icon><Plus /></el-icon>
          浏览更多课程
        </el-button>
      </div>
    </div>

    <!-- 学习统计 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon primary">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.totalCourses }}</div>
              <div class="stat-label">总课程数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon success">
              <el-icon><Medal /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.certificates }}</div>
              <div class="stat-label">获得证书</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon warning">
              <el-icon><Refresh /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.totalTime }}</div>
              <div class="stat-label">学习时长</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon info">
              <el-icon><Plus /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.avgProgress }}%</div>
              <div class="stat-label">平均进度</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 筛选和排序 -->
    <div class="filter-section">
      <el-row :gutter="20">
        <el-col :span="4">
          <el-select v-model="selectedStatus" placeholder="学习状态" @change="filterCourses" clearable>
            <el-option label="全部状态" value=""></el-option>
            <el-option label="学习中" value="learning"></el-option>
            <el-option label="已完成" value="completed"></el-option>
            <el-option label="未开始" value="not-started"></el-option>
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="selectedCategory" placeholder="课程分类" @change="filterCourses" clearable>
            <el-option label="全部分类" value=""></el-option>
            <el-option label="前端开发" value="前端开发"></el-option>
            <el-option label="后端开发" value="后端开发"></el-option>
            <el-option label="数据分析" value="数据分析"></el-option>
            <el-option label="移动开发" value="移动开发"></el-option>
            <el-option label="数据库" value="数据库"></el-option>
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="selectedType" placeholder="课程类型" @change="filterCourses" clearable>
            <el-option label="全部类型" value=""></el-option>
            <el-option label="免费课程" value="free"></el-option>
            <el-option label="付费课程" value="paid"></el-option>
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="sortBy" placeholder="排序方式" @change="sortCourses">
            <el-option label="最近学习" value="recent"></el-option>
            <el-option label="学习进度" value="progress"></el-option>
            <el-option label="购买时间" value="purchase"></el-option>
            <el-option label="课程名称" value="name"></el-option>
            <el-option label="学习时长" value="watchTime"></el-option>
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-input v-model="searchKeyword" placeholder="搜索课程..." @input="filterCourses" clearable>
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-button @click="resetFilters" type="info">
            <el-icon><Refresh /></el-icon>
            重置筛选
          </el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 课程列表 -->
    <div class="courses-section">
      <div v-if="loading" class="loading">
        <el-skeleton :rows="6" animated />
      </div>
      
      <div v-else-if="displayCourses.length === 0" class="no-courses">
        <el-empty description="暂无课程">
          <template #extra>
            <el-button type="primary" @click="$router.push('/user/course')">
              去浏览课程
            </el-button>
          </template>
        </el-empty>
      </div>
      
      <div v-else class="courses-grid">
        <el-row :gutter="20">
          <el-col 
            v-for="course in displayCourses" 
            :key="course.id" 
            :xs="24" 
            :sm="12" 
            :md="8" 
            :lg="6"
          >
            <div class="course-card" @click="viewCourseDetail(course.id)">
              <div class="course-image">
                <img :src="course.imageUrl || '/default-course.jpg'" :alt="course.title">
                <div class="course-overlay">
                  <el-button type="primary" size="small" @click.stop="continueLearning(course.id)">
                    继续学习
                  </el-button>
                </div>
                <div class="progress-badge">
                  <el-progress 
                    :percentage="course.progress || 0" 
                    :stroke-width="4"
                    :show-text="false"
                    :color="getProgressColor(course.progress)"
                  />
                  <span class="progress-text">{{ course.progress || 0 }}%</span>
                </div>
                <div class="course-type-badge">
                  <el-tag :type="course.isFree ? 'success' : 'warning'" size="small">
                    {{ course.isFree ? '免费' : '付费' }}
                  </el-tag>
                </div>
                <div v-if="course.certificate" class="certificate-badge">
                  <el-icon><Medal /></el-icon>
                </div>
              </div>
              
              <div class="course-info">
                <h3 class="course-title">{{ course.title }}</h3>
                <p class="course-description">{{ course.description }}</p>
                
                <div class="course-meta">
                  <span class="instructor">{{ course.instructorName || '未知讲师' }}</span>
                  <span class="duration">{{ formatDuration(course.duration) }}</span>
                </div>
                
                <div class="course-progress">
                  <div class="progress-info">
                    <span>章节进度：{{ course.completedChapters }}/{{ course.totalChapters }}</span>
                    <span>观看时长：{{ formatDuration(course.totalWatchTime) }}</span>
                  </div>
                </div>
                
                <div class="course-stats">
                  <span class="last-learn">
                    上次学习：{{ formatDate(course.lastLearnTime) }}
                  </span>
                  <span class="status">
                    <el-tag 
                      :type="getStatusType(course.progress)" 
                      size="small"
                    >
                      {{ getStatusText(course.progress) }}
                    </el-tag>
                  </span>
                </div>
                
                <div class="course-actions">
                  <el-button type="primary" size="small" @click.stop="continueLearning(course.id)">
                    继续学习
                  </el-button>
                  <el-button size="small" @click.stop="viewCourseDetail(course.id)">
                    查看详情
                  </el-button>
                  <el-button size="small" @click.stop="viewNotes(course.id)">
                    我的笔记
                  </el-button>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-section">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[12, 24, 36, 48]"
        :total="totalCourses"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 笔记对话框 -->
    <el-dialog v-model="showNotes" title="我的笔记" width="800px">
      <div v-if="currentNotes.length === 0" class="no-notes">
        <el-empty description="暂无笔记" />
      </div>
      <div v-else class="notes-list">
        <div v-for="note in currentNotes" :key="note.id" class="note-item">
          <div class="note-header">
            <h4>{{ note.title }}</h4>
            <span class="note-time">{{ formatDate(note.createTime) }}</span>
          </div>
          <div class="note-content">
            <p>{{ note.content }}</p>
          </div>
          <div class="note-meta">
            <span class="timestamp">时间点：{{ formatTime(note.timestamp) }}</span>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Search, Refresh, Calendar, Medal } from '@element-plus/icons-vue'
import { courseApi } from '@/api/course'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const courses = ref([])
const stats = ref({
  totalCourses: 0,
  totalTime: '0分钟',
  completedCourses: 0,
  avgProgress: 0,
  totalSpent: 0,
  certificates: 0,
  learningDays: 0
})
const selectedStatus = ref('')
const selectedCategory = ref('')
const selectedType = ref('')
const sortBy = ref('recent')
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(12)
const totalCourses = ref(0)
const showNotes = ref(false)
const currentNotes = ref([])

// 计算属性
const displayCourses = computed(() => {
  let filtered = courses.value

  // 关键词搜索
  if (searchKeyword.value) {
    filtered = filtered.filter(course => 
      course.title.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
      course.description.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
      course.instructorName.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }

  // 状态筛选
  if (selectedStatus.value) {
    filtered = filtered.filter(course => {
      const progress = course.progress || 0
      switch (selectedStatus.value) {
        case 'learning':
          return progress > 0 && progress < 100
        case 'completed':
          return progress >= 100
        case 'not-started':
          return progress === 0
        default:
          return true
      }
    })
  }

  // 分类筛选
  if (selectedCategory.value) {
    filtered = filtered.filter(course => course.category === selectedCategory.value)
  }

  // 类型筛选
  if (selectedType.value) {
    filtered = filtered.filter(course => {
      if (selectedType.value === 'free') return course.isFree
      if (selectedType.value === 'paid') return !course.isFree
      return true
    })
  }

  // 排序
  switch (sortBy.value) {
    case 'recent':
      filtered.sort((a, b) => new Date(b.lastLearnTime || 0) - new Date(a.lastLearnTime || 0))
      break
    case 'progress':
      filtered.sort((a, b) => (b.progress || 0) - (a.progress || 0))
      break
    case 'purchase':
      filtered.sort((a, b) => new Date(b.purchaseTime) - new Date(a.purchaseTime))
      break
    case 'name':
      filtered.sort((a, b) => a.title.localeCompare(b.title))
      break
    case 'watchTime':
      filtered.sort((a, b) => (b.totalWatchTime || 0) - (a.totalWatchTime || 0))
      break
  }

  return filtered
})

// 方法
const loadMyCourses = async () => {
  try {
    loading.value = true
    const response = await courseApi.getMyCourses({
      page: currentPage.value - 1,
      size: pageSize.value
    })
    
    if (response.code === 200) {
      courses.value = response.data.content || response.data || []
      totalCourses.value = response.data.totalElements || courses.value.length
      await loadStats()
    } else {
      ElMessage.error('获取我的课程失败')
    }
  } catch (error) {
    console.error('加载我的课程失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

const loadStats = async () => {
  try {
    const response = await courseApi.getMyCourseStats()
    if (response.code === 200) {
      stats.value = response.data || {
        totalCourses: courses.value.length,
        totalTime: '0分钟',
        completedCourses: 0,
        avgProgress: 0,
        totalSpent: 0,
        certificates: 0,
        learningDays: 0
      }
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
    // 计算基础统计
    const totalCourses = courses.value.length
    const completedCourses = courses.value.filter(c => (c.progress || 0) >= 100).length
    const avgProgress = totalCourses > 0 
      ? Math.round(courses.value.reduce((sum, c) => sum + (c.progress || 0), 0) / totalCourses)
      : 0
    const certificates = courses.value.filter(c => c.certificate).length
    const totalSpent = courses.value.reduce((sum, c) => sum + (c.price || 0), 0)
    
    stats.value = {
      totalCourses,
      totalTime: '0分钟',
      completedCourses,
      avgProgress,
      totalSpent,
      certificates,
      learningDays: 15
    }
  }
}

const filterCourses = () => {
  // 筛选逻辑已在计算属性中处理
}

const sortCourses = () => {
  // 排序逻辑已在计算属性中处理
}

const resetFilters = () => {
  selectedStatus.value = ''
  selectedCategory.value = ''
  selectedType.value = ''
  sortBy.value = 'recent'
  searchKeyword.value = ''
  currentPage.value = 1
  loadMyCourses()
}

const continueLearning = (courseId) => {
  router.push(`/user/course/${courseId}/play`)
}

const viewCourseDetail = (courseId) => {
  router.push(`/user/course/${courseId}`)
}

const viewNotes = async (courseId) => {
  try {
    const response = await courseApi.getNotes(courseId)
    if (response.code === 200) {
      currentNotes.value = response.data || []
      showNotes.value = true
    } else {
      ElMessage.error('获取笔记失败')
    }
  } catch (error) {
    console.error('获取笔记失败:', error)
    ElMessage.error('获取笔记失败，请稍后重试')
  }
}

const formatDuration = (minutes) => {
  if (!minutes) return '0分钟'
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return hours > 0 ? `${hours}小时${mins}分钟` : `${mins}分钟`
}

const formatDate = (date) => {
  if (!date) return '从未学习'
  return new Date(date).toLocaleDateString()
}

const formatTime = (seconds) => {
  if (!seconds) return '00:00'
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const getStatusType = (progress) => {
  if (progress >= 100) return 'success'
  if (progress > 0) return 'warning'
  return 'info'
}

const getStatusText = (progress) => {
  if (progress >= 100) return '已完成'
  if (progress > 0) return '学习中'
  return '未开始'
}

const getProgressColor = (progress) => {
  if (progress >= 100) return '#67c23a'
  if (progress >= 60) return '#e6a23c'
  if (progress >= 30) return '#f56c6c'
  return '#909399'
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadMyCourses()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadMyCourses()
}

// 生命周期
onMounted(() => {
  loadMyCourses()
})
</script>

<style scoped>
.my-courses-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.header-left h1 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 28px;
  font-weight: 600;
}

.subtitle {
  margin: 0;
  color: #666;
  font-size: 16px;
}

.stats-section {
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 15px;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.stat-icon.primary { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.stat-icon.success { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.stat-icon.warning { background: linear-gradient(135deg, #fa709a 0%, #fee140 100%); }
.stat-icon.info { background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%); }
.stat-icon.purple { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.stat-icon.orange { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.filter-section {
  margin-bottom: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
}

.courses-section {
  margin-bottom: 30px;
}

.loading {
  padding: 40px;
  text-align: center;
}

.no-courses {
  padding: 60px;
  text-align: center;
}

.courses-grid {
  margin-bottom: 30px;
}

.course-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s;
  cursor: pointer;
  margin-bottom: 20px;
  position: relative;
}

.course-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.course-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.course-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.course-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.course-card:hover .course-overlay {
  opacity: 1;
}

.progress-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 5px 10px;
  border-radius: 15px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 5px;
  min-width: 60px;
}

.progress-text {
  font-weight: 600;
}

.course-type-badge {
  position: absolute;
  top: 10px;
  left: 10px;
}

.certificate-badge {
  position: absolute;
  bottom: 10px;
  right: 10px;
  background: #ffd700;
  color: #333;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.course-info {
  padding: 20px;
}

.course-title {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  line-height: 1.4;
  height: 44px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.course-description {
  margin: 0 0 15px 0;
  font-size: 14px;
  color: #666;
  line-height: 1.4;
  height: 40px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.course-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 12px;
  color: #999;
}

.course-progress {
  margin-bottom: 15px;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #666;
  margin-bottom: 8px;
}

.course-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.last-learn {
  font-size: 12px;
  color: #999;
}

.course-actions {
  display: flex;
  gap: 8px;
}

.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.notes-list {
  max-height: 400px;
  overflow-y: auto;
}

.note-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  margin-bottom: 15px;
  padding: 15px;
}

.note-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.note-header h4 {
  margin: 0;
  color: #333;
}

.note-time {
  font-size: 12px;
  color: #999;
}

.note-content {
  margin-bottom: 10px;
}

.note-content p {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

.note-meta {
  font-size: 12px;
  color: #999;
}

.no-notes {
  padding: 40px;
  text-align: center;
}

@media (max-width: 768px) {
  .my-courses-container {
    padding: 10px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  .filter-section {
    padding: 15px;
  }
  
  .course-card {
    margin-bottom: 15px;
  }
  
  .course-actions {
    flex-direction: column;
  }
  
  .stat-card {
    padding: 15px;
  }
  
  .stat-icon {
    width: 40px;
    height: 40px;
    font-size: 20px;
  }
}
</style> 
  <div class="my-courses-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1>我的课程</h1>
        <p class="subtitle">管理您的学习进度和课程收藏</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="$router.push('/user/course')">
          <el-icon><Plus /></el-icon>
          浏览更多课程
        </el-button>
      </div>
    </div>

    <!-- 学习统计 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon primary">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.totalCourses }}</div>
              <div class="stat-label">总课程数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon success">
              <el-icon><Medal /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.certificates }}</div>
              <div class="stat-label">获得证书</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon warning">
              <el-icon><Refresh /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.totalTime }}</div>
              <div class="stat-label">学习时长</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon info">
              <el-icon><Plus /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.avgProgress }}%</div>
              <div class="stat-label">平均进度</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 筛选和排序 -->
    <div class="filter-section">
      <el-row :gutter="20">
        <el-col :span="4">
          <el-select v-model="selectedStatus" placeholder="学习状态" @change="filterCourses" clearable>
            <el-option label="全部状态" value=""></el-option>
            <el-option label="学习中" value="learning"></el-option>
            <el-option label="已完成" value="completed"></el-option>
            <el-option label="未开始" value="not-started"></el-option>
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="selectedCategory" placeholder="课程分类" @change="filterCourses" clearable>
            <el-option label="全部分类" value=""></el-option>
            <el-option label="前端开发" value="前端开发"></el-option>
            <el-option label="后端开发" value="后端开发"></el-option>
            <el-option label="数据分析" value="数据分析"></el-option>
            <el-option label="移动开发" value="移动开发"></el-option>
            <el-option label="数据库" value="数据库"></el-option>
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="selectedType" placeholder="课程类型" @change="filterCourses" clearable>
            <el-option label="全部类型" value=""></el-option>
            <el-option label="免费课程" value="free"></el-option>
            <el-option label="付费课程" value="paid"></el-option>
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="sortBy" placeholder="排序方式" @change="sortCourses">
            <el-option label="最近学习" value="recent"></el-option>
            <el-option label="学习进度" value="progress"></el-option>
            <el-option label="购买时间" value="purchase"></el-option>
            <el-option label="课程名称" value="name"></el-option>
            <el-option label="学习时长" value="watchTime"></el-option>
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-input v-model="searchKeyword" placeholder="搜索课程..." @input="filterCourses" clearable>
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-button @click="resetFilters" type="info">
            <el-icon><Refresh /></el-icon>
            重置筛选
          </el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 课程列表 -->
    <div class="courses-section">
      <div v-if="loading" class="loading">
        <el-skeleton :rows="6" animated />
      </div>
      
      <div v-else-if="displayCourses.length === 0" class="no-courses">
        <el-empty description="暂无课程">
          <template #extra>
            <el-button type="primary" @click="$router.push('/user/course')">
              去浏览课程
            </el-button>
          </template>
        </el-empty>
      </div>
      
      <div v-else class="courses-grid">
        <el-row :gutter="20">
          <el-col 
            v-for="course in displayCourses" 
            :key="course.id" 
            :xs="24" 
            :sm="12" 
            :md="8" 
            :lg="6"
          >
            <div class="course-card" @click="viewCourseDetail(course.id)">
              <div class="course-image">
                <img :src="course.imageUrl || '/default-course.jpg'" :alt="course.title">
                <div class="course-overlay">
                  <el-button type="primary" size="small" @click.stop="continueLearning(course.id)">
                    继续学习
                  </el-button>
                </div>
                <div class="progress-badge">
                  <el-progress 
                    :percentage="course.progress || 0" 
                    :stroke-width="4"
                    :show-text="false"
                    :color="getProgressColor(course.progress)"
                  />
                  <span class="progress-text">{{ course.progress || 0 }}%</span>
                </div>
                <div class="course-type-badge">
                  <el-tag :type="course.isFree ? 'success' : 'warning'" size="small">
                    {{ course.isFree ? '免费' : '付费' }}
                  </el-tag>
                </div>
                <div v-if="course.certificate" class="certificate-badge">
                  <el-icon><Medal /></el-icon>
                </div>
              </div>
              
              <div class="course-info">
                <h3 class="course-title">{{ course.title }}</h3>
                <p class="course-description">{{ course.description }}</p>
                
                <div class="course-meta">
                  <span class="instructor">{{ course.instructorName || '未知讲师' }}</span>
                  <span class="duration">{{ formatDuration(course.duration) }}</span>
                </div>
                
                <div class="course-progress">
                  <div class="progress-info">
                    <span>章节进度：{{ course.completedChapters }}/{{ course.totalChapters }}</span>
                    <span>观看时长：{{ formatDuration(course.totalWatchTime) }}</span>
                  </div>
                </div>
                
                <div class="course-stats">
                  <span class="last-learn">
                    上次学习：{{ formatDate(course.lastLearnTime) }}
                  </span>
                  <span class="status">
                    <el-tag 
                      :type="getStatusType(course.progress)" 
                      size="small"
                    >
                      {{ getStatusText(course.progress) }}
                    </el-tag>
                  </span>
                </div>
                
                <div class="course-actions">
                  <el-button type="primary" size="small" @click.stop="continueLearning(course.id)">
                    继续学习
                  </el-button>
                  <el-button size="small" @click.stop="viewCourseDetail(course.id)">
                    查看详情
                  </el-button>
                  <el-button size="small" @click.stop="viewNotes(course.id)">
                    我的笔记
                  </el-button>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-section">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[12, 24, 36, 48]"
        :total="totalCourses"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 笔记对话框 -->
    <el-dialog v-model="showNotes" title="我的笔记" width="800px">
      <div v-if="currentNotes.length === 0" class="no-notes">
        <el-empty description="暂无笔记" />
      </div>
      <div v-else class="notes-list">
        <div v-for="note in currentNotes" :key="note.id" class="note-item">
          <div class="note-header">
            <h4>{{ note.title }}</h4>
            <span class="note-time">{{ formatDate(note.createTime) }}</span>
          </div>
          <div class="note-content">
            <p>{{ note.content }}</p>
          </div>
          <div class="note-meta">
            <span class="timestamp">时间点：{{ formatTime(note.timestamp) }}</span>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Search, Refresh, Calendar, Medal } from '@element-plus/icons-vue'
import { courseApi } from '@/api/course'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const courses = ref([])
const stats = ref({
  totalCourses: 0,
  totalTime: '0分钟',
  completedCourses: 0,
  avgProgress: 0,
  totalSpent: 0,
  certificates: 0,
  learningDays: 0
})
const selectedStatus = ref('')
const selectedCategory = ref('')
const selectedType = ref('')
const sortBy = ref('recent')
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(12)
const totalCourses = ref(0)
const showNotes = ref(false)
const currentNotes = ref([])

// 计算属性
const displayCourses = computed(() => {
  let filtered = courses.value

  // 关键词搜索
  if (searchKeyword.value) {
    filtered = filtered.filter(course => 
      course.title.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
      course.description.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
      course.instructorName.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }

  // 状态筛选
  if (selectedStatus.value) {
    filtered = filtered.filter(course => {
      const progress = course.progress || 0
      switch (selectedStatus.value) {
        case 'learning':
          return progress > 0 && progress < 100
        case 'completed':
          return progress >= 100
        case 'not-started':
          return progress === 0
        default:
          return true
      }
    })
  }

  // 分类筛选
  if (selectedCategory.value) {
    filtered = filtered.filter(course => course.category === selectedCategory.value)
  }

  // 类型筛选
  if (selectedType.value) {
    filtered = filtered.filter(course => {
      if (selectedType.value === 'free') return course.isFree
      if (selectedType.value === 'paid') return !course.isFree
      return true
    })
  }

  // 排序
  switch (sortBy.value) {
    case 'recent':
      filtered.sort((a, b) => new Date(b.lastLearnTime || 0) - new Date(a.lastLearnTime || 0))
      break
    case 'progress':
      filtered.sort((a, b) => (b.progress || 0) - (a.progress || 0))
      break
    case 'purchase':
      filtered.sort((a, b) => new Date(b.purchaseTime) - new Date(a.purchaseTime))
      break
    case 'name':
      filtered.sort((a, b) => a.title.localeCompare(b.title))
      break
    case 'watchTime':
      filtered.sort((a, b) => (b.totalWatchTime || 0) - (a.totalWatchTime || 0))
      break
  }

  return filtered
})

// 方法
const loadMyCourses = async () => {
  try {
    loading.value = true
    const response = await courseApi.getMyCourses({
      page: currentPage.value - 1,
      size: pageSize.value
    })
    
    if (response.code === 200) {
      courses.value = response.data.content || response.data || []
      totalCourses.value = response.data.totalElements || courses.value.length
      await loadStats()
    } else {
      ElMessage.error('获取我的课程失败')
    }
  } catch (error) {
    console.error('加载我的课程失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

const loadStats = async () => {
  try {
    const response = await courseApi.getMyCourseStats()
    if (response.code === 200) {
      stats.value = response.data || {
        totalCourses: courses.value.length,
        totalTime: '0分钟',
        completedCourses: 0,
        avgProgress: 0,
        totalSpent: 0,
        certificates: 0,
        learningDays: 0
      }
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
    // 计算基础统计
    const totalCourses = courses.value.length
    const completedCourses = courses.value.filter(c => (c.progress || 0) >= 100).length
    const avgProgress = totalCourses > 0 
      ? Math.round(courses.value.reduce((sum, c) => sum + (c.progress || 0), 0) / totalCourses)
      : 0
    const certificates = courses.value.filter(c => c.certificate).length
    const totalSpent = courses.value.reduce((sum, c) => sum + (c.price || 0), 0)
    
    stats.value = {
      totalCourses,
      totalTime: '0分钟',
      completedCourses,
      avgProgress,
      totalSpent,
      certificates,
      learningDays: 15
    }
  }
}

const filterCourses = () => {
  // 筛选逻辑已在计算属性中处理
}

const sortCourses = () => {
  // 排序逻辑已在计算属性中处理
}

const resetFilters = () => {
  selectedStatus.value = ''
  selectedCategory.value = ''
  selectedType.value = ''
  sortBy.value = 'recent'
  searchKeyword.value = ''
  currentPage.value = 1
  loadMyCourses()
}

const continueLearning = (courseId) => {
  router.push(`/user/course/${courseId}/play`)
}

const viewCourseDetail = (courseId) => {
  router.push(`/user/course/${courseId}`)
}

const viewNotes = async (courseId) => {
  try {
    const response = await courseApi.getNotes(courseId)
    if (response.code === 200) {
      currentNotes.value = response.data || []
      showNotes.value = true
    } else {
      ElMessage.error('获取笔记失败')
    }
  } catch (error) {
    console.error('获取笔记失败:', error)
    ElMessage.error('获取笔记失败，请稍后重试')
  }
}

const formatDuration = (minutes) => {
  if (!minutes) return '0分钟'
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return hours > 0 ? `${hours}小时${mins}分钟` : `${mins}分钟`
}

const formatDate = (date) => {
  if (!date) return '从未学习'
  return new Date(date).toLocaleDateString()
}

const formatTime = (seconds) => {
  if (!seconds) return '00:00'
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const getStatusType = (progress) => {
  if (progress >= 100) return 'success'
  if (progress > 0) return 'warning'
  return 'info'
}

const getStatusText = (progress) => {
  if (progress >= 100) return '已完成'
  if (progress > 0) return '学习中'
  return '未开始'
}

const getProgressColor = (progress) => {
  if (progress >= 100) return '#67c23a'
  if (progress >= 60) return '#e6a23c'
  if (progress >= 30) return '#f56c6c'
  return '#909399'
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadMyCourses()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadMyCourses()
}

// 生命周期
onMounted(() => {
  loadMyCourses()
})
</script>

<style scoped>
.my-courses-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.header-left h1 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 28px;
  font-weight: 600;
}

.subtitle {
  margin: 0;
  color: #666;
  font-size: 16px;
}

.stats-section {
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 15px;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.stat-icon.primary { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.stat-icon.success { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.stat-icon.warning { background: linear-gradient(135deg, #fa709a 0%, #fee140 100%); }
.stat-icon.info { background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%); }
.stat-icon.purple { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.stat-icon.orange { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.filter-section {
  margin-bottom: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
}

.courses-section {
  margin-bottom: 30px;
}

.loading {
  padding: 40px;
  text-align: center;
}

.no-courses {
  padding: 60px;
  text-align: center;
}

.courses-grid {
  margin-bottom: 30px;
}

.course-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s;
  cursor: pointer;
  margin-bottom: 20px;
  position: relative;
}

.course-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.course-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.course-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.course-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.course-card:hover .course-overlay {
  opacity: 1;
}

.progress-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 5px 10px;
  border-radius: 15px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 5px;
  min-width: 60px;
}

.progress-text {
  font-weight: 600;
}

.course-type-badge {
  position: absolute;
  top: 10px;
  left: 10px;
}

.certificate-badge {
  position: absolute;
  bottom: 10px;
  right: 10px;
  background: #ffd700;
  color: #333;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.course-info {
  padding: 20px;
}

.course-title {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  line-height: 1.4;
  height: 44px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.course-description {
  margin: 0 0 15px 0;
  font-size: 14px;
  color: #666;
  line-height: 1.4;
  height: 40px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.course-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 12px;
  color: #999;
}

.course-progress {
  margin-bottom: 15px;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #666;
  margin-bottom: 8px;
}

.course-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.last-learn {
  font-size: 12px;
  color: #999;
}

.course-actions {
  display: flex;
  gap: 8px;
}

.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.notes-list {
  max-height: 400px;
  overflow-y: auto;
}

.note-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  margin-bottom: 15px;
  padding: 15px;
}

.note-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.note-header h4 {
  margin: 0;
  color: #333;
}

.note-time {
  font-size: 12px;
  color: #999;
}

.note-content {
  margin-bottom: 10px;
}

.note-content p {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

.note-meta {
  font-size: 12px;
  color: #999;
}

.no-notes {
  padding: 40px;
  text-align: center;
}

@media (max-width: 768px) {
  .my-courses-container {
    padding: 10px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  .filter-section {
    padding: 15px;
  }
  
  .course-card {
    margin-bottom: 15px;
  }
  
  .course-actions {
    flex-direction: column;
  }
  
  .stat-card {
    padding: 15px;
  }
  
  .stat-icon {
    width: 40px;
    height: 40px;
    font-size: 20px;
  }
}
</style> 