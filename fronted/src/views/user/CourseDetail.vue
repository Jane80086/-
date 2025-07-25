<template>
  <div class="course-detail-container">
    <div v-if="loading" class="loading">
      <el-skeleton :rows="10" animated />
    </div>
    
    <div v-else-if="course" class="course-content">
      <!-- 课程头部信息 -->
      <div class="course-header">
        <div class="course-banner">
          <img :src="course.imageUrl || '/class.jpg'" :alt="course.title">
          <div class="course-overlay">
            <el-button type="primary" size="large" @click="goToPlay">
              <el-icon><VideoPlay /></el-icon>
              立即学习
            </el-button>
          </div>
        </div>
        
        <div class="course-info">
          <h1 class="course-title">{{ course.title }}</h1>
          <p class="course-description">{{ course.description }}</p>
          
          <div class="course-meta">
            <div class="meta-item">
              <el-icon><User /></el-icon>
              <span>讲师：{{ course.instructorName || '未知讲师' }}</span>
            </div>
            <div class="meta-item">
              <el-icon><Clock /></el-icon>
              <span>时长：{{ formatDuration(course.duration) }}</span>
            </div>
            <div class="meta-item">
              <el-icon><View /></el-icon>
              <span>{{ course.viewCount || 0 }} 次观看</span>
            </div>
            <div class="meta-item">
              <el-icon><Star /></el-icon>
              <el-rate 
                v-model="course.rating" 
                disabled 
                show-score 
                text-color="#ff9900"
                score-template="{value}"
              />
            </div>
          </div>
          
          <div class="course-price-section">
            <div class="price-info">
              <span v-if="course.price > 0" class="price">¥{{ course.price }}</span>
              <span v-else class="free">免费</span>
            </div>
            <div class="action-buttons">
              <el-button type="primary" size="large" @click="goToPlay">
                <el-icon><VideoPlay /></el-icon>
                开始学习
              </el-button>
              <el-button @click="toggleFavorite">
                <el-icon><Star /></el-icon>
                {{ isFavorite ? '取消收藏' : '收藏' }}
              </el-button>
              <el-button @click="shareCourse">
                <el-icon><Share /></el-icon>
                分享
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 课程内容区域 -->
      <div class="course-body">
        <el-tabs v-model="activeTab" class="course-tabs">
          <!-- 课程介绍 -->
          <el-tab-pane label="课程介绍" name="intro">
            <div class="intro-content">
              <div class="course-details">
                <h3>课程详情</h3>
                <div class="detail-item">
                  <span class="label">课程分类：</span>
                  <span>{{ course.category }}</span>
                </div>
                <div class="detail-item">
                  <span class="label">难度等级：</span>
                  <span>{{ course.level }}</span>
                </div>
                <div class="detail-item">
                  <span class="label">更新时间：</span>
                  <span>{{ formatDate(course.updateTime) }}</span>
                </div>
                <div class="detail-item">
                  <span class="label">课程状态：</span>
                  <el-tag :type="getStatusType(course.status)">
                    {{ getStatusText(course.status) }}
                  </el-tag>
                </div>
              </div>
              
              <div class="course-outline">
                <h3>课程大纲</h3>
                <div class="outline-content">
                  <p>{{ course.outline || '暂无课程大纲' }}</p>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <!-- 课程章节 -->
          <el-tab-pane label="课程章节" name="chapters">
            <div class="chapters-section">
              <div class="chapters-list">
                <div
                  v-for="(chapter, index) in chapters"
                  :key="chapter.id"
                  class="chapter-item"
                >
                  <div class="chapter-info">
                    <div class="chapter-title">
                      <span class="chapter-number">{{ index + 1 }}</span>
                      <span class="title">{{ chapter.title }}</span>
                    </div>
                    <div class="chapter-meta">
                      <span class="duration">{{ formatDuration(chapter.duration) }}</span>
                      <span class="description">{{ chapter.description }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <!-- 讲师信息 -->
          <el-tab-pane label="讲师信息" name="instructor">
            <div class="instructor-section">
              <div class="instructor-info">
                <div class="instructor-avatar">
                  <img :src="course.instructorAvatar || '/default-avatar.jpg'" :alt="course.instructorName">
                </div>
                <div class="instructor-details">
                  <h3>{{ course.instructorName || '未知讲师' }}</h3>
                  <p class="instructor-title">{{ course.instructorTitle || '资深讲师' }}</p>
                  <p class="instructor-bio">{{ course.instructorBio || '暂无讲师介绍' }}</p>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- 底部学习按钮 -->
      <div class="course-footer">
        <div class="footer-content">
          <div class="course-summary">
            <h3>准备开始学习了吗？</h3>
            <p>点击下方按钮，立即开始您的学习之旅</p>
          </div>
          <el-button type="primary" size="large" @click="goToPlay">
            <el-icon><VideoPlay /></el-icon>
            立即开始学习
          </el-button>
        </div>
      </div>
    </div>
    
    <div v-else class="not-found">
      <el-result icon="warning" title="课程不存在" sub-title="抱歉，您访问的课程不存在或已被删除">
        <template #extra>
          <el-button type="primary" @click="$router.push('/user/courses')">返回课程列表</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  VideoPlay, User, Clock, View, Star, Share
} from '@element-plus/icons-vue'
import { courseApi } from '@/api/course'

const route = useRoute()
const router = useRouter()

// 响应式数据
const loading = ref(false)
const course = ref(null)
const chapters = ref([])
const activeTab = ref('intro')
const isFavorite = ref(false)

// 方法
const loadCourseDetail = async () => {
  try {
    loading.value = true
    const courseId = route.params.id
    const response = await courseApi.getCourseDetail(courseId)
    
    if (response.code === 200) {
      course.value = response.data
      await loadChapters()
    } else {
      ElMessage.error('获取课程详情失败')
    }
  } catch (error) {
    console.error('加载课程详情失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

const loadChapters = async () => {
  try {
    const courseId = route.params.id
    const response = await courseApi.getChapters(courseId)
    if (response.code === 200) {
      chapters.value = response.data || []
    } else {
      // 如果没有章节，创建默认章节
      chapters.value = [{
        id: 1,
        title: course.value.title,
        description: course.value.description,
        duration: course.value.duration
      }]
    }
  } catch (error) {
    console.error('加载章节失败:', error)
    // 创建默认章节
    chapters.value = [{
      id: 1,
      title: course.value.title,
      description: course.value.description,
      duration: course.value.duration
    }]
  }
}

const goToPlay = () => {
  const courseId = route.params.id
  console.log('goToPlay called, courseId:', courseId)
  console.log('Current route:', route.path)
  console.log('Target path:', `/user/course/${courseId}/play`)
  
  try {
    router.push(`/user/course/${courseId}/play`).then(() => {
      console.log('Navigation successful')
    }).catch((error) => {
      console.error('Navigation failed:', error)
      ElMessage.error('页面跳转失败')
    })
  } catch (error) {
    console.error('Router push error:', error)
    ElMessage.error('页面跳转失败')
  }
}

const toggleFavorite = async () => {
  try {
    const courseId = route.params.id
    const response = await courseApi.toggleFavorite(courseId)
    if (response.code === 200) {
      isFavorite.value = !isFavorite.value
      ElMessage.success(isFavorite.value ? '收藏成功' : '取消收藏成功')
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

const shareCourse = () => {
  const url = window.location.href
  navigator.clipboard.writeText(url).then(() => {
    ElMessage.success('链接已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

const formatDuration = (minutes) => {
  if (!minutes) return '0分钟'
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return hours > 0 ? `${hours}小时${mins}分钟` : `${mins}分钟`
}

const formatDate = (date) => {
  if (!date) return '未知'
  return new Date(date).toLocaleDateString('zh-CN')
}

const getStatusType = (status) => {
  const statusMap = {
    'PUBLISHED': 'success',
    'DRAFT': 'info',
    'REVIEWING': 'warning',
    'REJECTED': 'danger'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status) => {
  const statusMap = {
    'PUBLISHED': '已发布',
    'DRAFT': '草稿',
    'REVIEWING': '审核中',
    'REJECTED': '已拒绝'
  }
  return statusMap[status] || '未知'
}

onMounted(() => {
  loadCourseDetail()
})
</script> 

<style scoped>
.course-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.loading {
  padding: 40px;
}

.course-header {
  display: flex;
  gap: 30px;
  margin-bottom: 40px;
  padding-bottom: 30px;
  border-bottom: 1px solid #eee;
}

.course-banner {
  position: relative;
  width: 400px;
  height: 250px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
}

.course-banner img {
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

.course-banner:hover .course-overlay {
  opacity: 1;
}

.course-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.course-title {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin: 0 0 15px 0;
  line-height: 1.3;
}

.course-description {
  font-size: 16px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 20px;
}

.course-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 25px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 14px;
}

.course-price-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.price-info .price {
  font-size: 24px;
  font-weight: 600;
  color: #e74c3c;
}

.price-info .free {
  font-size: 24px;
  font-weight: 600;
  color: #27ae60;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.course-body {
  margin-bottom: 40px;
}

.course-tabs {
  background: #fff;
  border-radius: 8px;
}

.intro-content {
  padding: 20px 0;
}

.course-details {
  margin-bottom: 30px;
}

.detail-item {
  display: flex;
  margin-bottom: 15px;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.detail-item .label {
  font-weight: 600;
  color: #333;
  width: 100px;
  flex-shrink: 0;
}

.course-outline h3 {
  margin-bottom: 15px;
  color: #333;
}

.outline-content {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  line-height: 1.6;
}

.chapters-section {
  padding: 20px 0;
}

.chapters-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.chapter-item {
  padding: 20px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.chapter-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.chapter-info {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.chapter-title {
  display: flex;
  align-items: center;
  gap: 15px;
  flex: 1;
}

.chapter-number {
  width: 30px;
  height: 30px;
  background: #409eff;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
}

.title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.chapter-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 5px;
}

.duration {
  color: #666;
  font-size: 14px;
}

.description {
  color: #999;
  font-size: 13px;
  max-width: 200px;
  text-align: right;
}

.instructor-section {
  padding: 20px 0;
}

.instructor-info {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

.instructor-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.instructor-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.instructor-details h3 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 20px;
}

.instructor-title {
  color: #666;
  margin-bottom: 10px;
  font-size: 14px;
}

.instructor-bio {
  color: #666;
  line-height: 1.6;
}

.course-footer {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 40px;
  text-align: center;
  color: white;
}

.footer-content {
  max-width: 600px;
  margin: 0 auto;
}

.course-summary h3 {
  font-size: 24px;
  margin-bottom: 10px;
  color: white;
}

.course-summary p {
  font-size: 16px;
  margin-bottom: 25px;
  opacity: 0.9;
}

.not-found {
  text-align: center;
  padding: 60px 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .course-header {
    flex-direction: column;
    gap: 20px;
  }
  
  .course-banner {
    width: 100%;
    height: 200px;
  }
  
  .course-meta {
    flex-direction: column;
    gap: 15px;
  }
  
  .course-price-section {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }
  
  .action-buttons {
    justify-content: center;
  }
  
  .chapter-info {
    flex-direction: column;
    gap: 10px;
  }
  
  .chapter-meta {
    align-items: flex-start;
  }
  
  .instructor-info {
    flex-direction: column;
    text-align: center;
  }
}
</style> 