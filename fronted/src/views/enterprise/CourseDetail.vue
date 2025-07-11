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
              <el-rate v-model="course.rating" disabled show-score text-color="#ff9900" score-template="{value}" />
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
          <el-tab-pane label="课程章节" name="chapters">
            <div class="chapters-section">
              <div class="chapters-list">
                <div v-for="(chapter, index) in chapters" :key="chapter.id" class="chapter-item">
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
          <el-button type="primary" @click="$router.push('/enterprise/courses')">返回课程列表</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { VideoPlay, User, Clock, View, Star, Share } from '@element-plus/icons-vue'
import { courseApi } from '@/api/course'
const route = useRoute()
const router = useRouter()
const loading = ref(false)
const course = ref(null)
const chapters = ref([])
const activeTab = ref('intro')
const isFavorite = ref(false)
const videoError = ref(false)

function onVideoError() {
  videoError.value = true
}
function getVideoPreviewUrl(objectName) {
  return `/api/file/stream?objectName=${encodeURIComponent(objectName)}`
}

const loadCourseDetail = async () => {
  loading.value = true
  try {
    const courseId = route.params.id
    const res = await courseApi.getCourseDetail(courseId)
    console.log('courseId', courseId, res)
    // 新增判断：data 为空对象也视为无效
    if (res.code === 200 && res.data && Object.keys(res.data).length > 0) {
      course.value = res.data
    } else {
      course.value = null
      ElMessage.error(res.message || '获取课程详情失败')
    }
    // 获取章节（如有章节API）
    if (courseId && courseApi.getChapters) {
      try {
        const chapterRes = await courseApi.getChapters(courseId)
        if (chapterRes.code === 200) {
          chapters.value = chapterRes.data || []
        } else {
          chapters.value = []
        }
      } catch (e) {
        chapters.value = []
      }
    }
  } catch (e) {
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}
const goToPlay = () => {
  if (course.value && course.value.id) {
    router.push(`/user/course/${course.value.id}/play`)
  }
}
const toggleFavorite = () => {
  isFavorite.value = !isFavorite.value
  ElMessage.success(isFavorite.value ? '已收藏' : '已取消收藏')
}
const shareCourse = () => {
  ElMessage.success('分享链接已复制')
}
const formatDuration = (minutes) => {
  if (!minutes) return '0分钟'
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return hours > 0 ? `${hours}小时${mins}分钟` : `${mins}分钟`
}
const formatDate = (date) => {
  if (!date) return '未更新'
  return date
}
const getStatusType = (status) => {
  if (status === 'published') return 'success'
  if (status === 'draft') return 'info'
  if (status === 'pending') return 'warning'
  if (status === 'rejected') return 'danger'
  return ''
}
const getStatusText = (status) => {
  if (status === 'published') return '已发布'
  if (status === 'draft') return '草稿'
  if (status === 'pending') return '审核中'
  if (status === 'rejected') return '未通过'
  return ''
}
onMounted(() => {
  loadCourseDetail()
})
</script>
<style src="../user/CourseDetail.css"></style> 