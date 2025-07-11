<template>
  <div class="course-detail-container">
    <div v-if="loading" class="loading">
      <el-skeleton :rows="10" animated />
    </div>
    
    <div v-else-if="course" class="course-content">
      <!-- 课程头部信息 -->
      <div class="course-header">
        <div class="course-banner">
          <img :src="course.imageUrl || '/default-course.jpg'" :alt="course.title">
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

          <!-- 审核操作区 -->
          <el-tab-pane label="审核操作" name="audit">
            <div class="audit-section">
              <div class="audit-header">
                <h3>审核操作</h3>
                <p>请仔细查看课程内容后，选择审核结果</p>
              </div>
              <div class="action-buttons">
                <el-button type="success" size="large" @click="approveCourse">
                  <el-icon><Check /></el-icon>
                  通过审核
                </el-button>
                <el-button type="danger" size="large" @click="showRejectDialog = true">
                  <el-icon><Close /></el-icon>
                  驳回课程
                </el-button>
              </div>
              <el-dialog v-model="showRejectDialog" title="驳回原因" width="400px">
                <el-input v-model="rejectReason" type="textarea" placeholder="请输入驳回原因" :rows="4" />
                <template #footer>
                  <el-button @click="showRejectDialog = false">取消</el-button>
                  <el-button type="danger" @click="rejectCourse">确认驳回</el-button>
                </template>
              </el-dialog>
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
          <el-button type="primary" @click="$router.push('/admin/course')">返回课程列表</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { VideoPlay, User, Clock, View, Star, Check, Close } from '@element-plus/icons-vue'
import { courseApi } from '@/api/course'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const course = ref(null)
const chapters = ref([])
const activeTab = ref('intro')
const showRejectDialog = ref(false)
const rejectReason = ref('')

const loadCourseDetail = async () => {
  try {
    loading.value = true
    const courseId = route.params.id
    const response = await courseApi.getCourseDetail(courseId)
    if (response.code === 200) {
      course.value = response.data
      await loadChapters()
    } else {
      ElMessage.error(`获取课程详情失败，ID=${courseId}，后端返回：${response.message || response.code}`)
    }
  } catch (error) {
    const courseId = route.params.id
    ElMessage.error(`网络错误或404，课程ID=${courseId}，${error?.message || error}`)
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
      chapters.value = [{
        id: 1,
        title: course.value.title,
        description: course.value.description,
        duration: course.value.duration
      }]
    }
  } catch (error) {
    chapters.value = [{
      id: 1,
      title: course.value?.title || '',
      description: course.value?.description || '',
      duration: course.value?.duration || 0
    }]
  }
}
const goToPlay = () => {
  if (!course.value?.id) {
    ElMessage.error('课程ID无效，无法跳转到播放页')
    return
  }
  router.push(`/admin/course/${course.value.id}/play`)
}
const formatDuration = (min) => {
  if (!min) return '0分钟'
  const h = Math.floor(min / 60)
  const m = min % 60
  return h ? `${h}小时${m}分钟` : `${m}分钟`
}
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}-${d.getMonth() + 1}-${d.getDate()}`
}
const getStatusType = (status) => {
  switch (status) {
    case 'pending': return 'warning'
    case 'approved': return 'success'
    case 'rejected': return 'danger'
    default: return 'info'
  }
}
const getStatusText = (status) => {
  switch (status) {
    case 'pending': return '待审核'
    case 'approved': return '已通过'
    case 'rejected': return '已驳回'
    default: return '未知'
  }
}
const approveCourse = async () => {
  // TODO: 调用后端审核通过接口
  ElMessage.success('审核通过')
}
const rejectCourse = async () => {
  // TODO: 调用后端驳回接口，带上rejectReason.value
  showRejectDialog.value = false
  ElMessage.success('已驳回')
}
onMounted(() => {
  loadCourseDetail()
})
</script>

<style scoped src="./CourseDetail.css"></style> 