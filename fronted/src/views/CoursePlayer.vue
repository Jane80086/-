<template>
  <div class="course-player">
    <!-- 播放器区域 -->
    <div class="player-container">
      <div class="video-player">
        <video
          ref="videoPlayer"
          :src="course.videoUrl"
          controls
          autoplay
          @timeupdate="onTimeUpdate"
          @ended="onVideoEnded"
          @play="onVideoPlay"
          @pause="onVideoPause"
          class="main-video"
        >
          您的浏览器不支持视频播放
        </video>
        
        <!-- 自定义播放控制 -->
        <div class="custom-controls" v-if="showCustomControls">
          <div class="progress-bar">
            <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
            <div class="progress-handle" :style="{ left: progressPercent + '%' }"></div>
          </div>
          <div class="control-buttons">
            <el-button @click="togglePlay" :icon="isPlaying ? 'VideoPause' : 'VideoPlay'">
              {{ isPlaying ? '暂停' : '播放' }}
            </el-button>
            <el-button @click="skipBackward" icon="Back">后退10秒</el-button>
            <el-button @click="skipForward" icon="Right">前进10秒</el-button>
            <el-button @click="toggleFullscreen" icon="FullScreen">全屏</el-button>
          </div>
        </div>
      </div>
      
      <!-- 课程信息 -->
      <div class="course-info">
        <h1>{{ course.title }}</h1>
        <div class="meta-info">
          <span class="instructor">讲师：{{ course.instructorName || '未知' }}</span>
          <span class="duration">时长：{{ formatDuration(course.duration) }}</span>
          <span class="views">观看：{{ course.viewCount || 0 }}次</span>
        </div>
        <div class="description">{{ course.description }}</div>
        
        <!-- 学习进度 -->
        <div class="learning-progress">
          <h3>学习进度</h3>
          <el-progress 
            :percentage="learningProgress" 
            :color="getProgressColor(learningProgress)"
            :stroke-width="8"
          />
          <div class="progress-text">
            已完成 {{ Math.round(learningProgress) }}% 
            ({{ formatTime(currentTime) }} / {{ formatTime(course.duration) }})
          </div>
        </div>
      </div>
    </div>
    
    <!-- 侧边栏 -->
    <div class="sidebar">
      <!-- 课程大纲 -->
      <el-card class="outline-card">
        <template #header>
          <span>课程大纲</span>
        </template>
        <div class="outline-list">
          <div 
            v-for="(chapter, index) in courseOutline" 
            :key="index"
            class="outline-item"
            :class="{ active: currentChapter === index }"
            @click="playChapter(index)"
          >
            <div class="chapter-info">
              <span class="chapter-title">{{ chapter.title }}</span>
              <span class="chapter-duration">{{ formatDuration(chapter.duration) }}</span>
            </div>
            <div class="chapter-progress">
              <el-progress 
                :percentage="chapter.progress" 
                :stroke-width="4"
                :show-text="false"
              />
            </div>
          </div>
        </div>
      </el-card>
      
      <!-- 学习笔记 -->
      <el-card class="notes-card">
        <template #header>
          <span>学习笔记</span>
          <el-button size="small" @click="addNote" type="primary">添加笔记</el-button>
        </template>
        <div class="notes-list">
          <div v-for="note in notes" :key="note.id" class="note-item">
            <div class="note-time">{{ formatTime(note.timestamp) }}</div>
            <div class="note-content">{{ note.content }}</div>
            <div class="note-actions">
              <el-button size="small" @click="editNote(note)">编辑</el-button>
              <el-button size="small" type="danger" @click="deleteNote(note.id)">删除</el-button>
            </div>
          </div>
        </div>
      </el-card>
      
      <!-- 相关课程 -->
      <el-card class="related-card">
        <template #header>
          <span>相关课程</span>
        </template>
        <div class="related-list">
          <div 
            v-for="relatedCourse in relatedCourses" 
            :key="relatedCourse.id"
            class="related-item"
            @click="goToCourse(relatedCourse.id)"
          >
            <img :src="relatedCourse.coverImage" :alt="relatedCourse.title" />
            <div class="related-info">
              <div class="related-title">{{ relatedCourse.title }}</div>
              <div class="related-instructor">{{ relatedCourse.instructorName }}</div>
            </div>
          </div>
        </div>
      </el-card>
    </div>
    
    <!-- 笔记编辑对话框 -->
    <el-dialog v-model="noteDialogVisible" title="编辑笔记" width="500px">
      <el-form :model="noteForm" label-width="80px">
        <el-form-item label="时间点">
          <el-input v-model="noteForm.timestamp" type="number" placeholder="秒数" />
        </el-form-item>
        <el-form-item label="笔记内容">
          <el-input 
            v-model="noteForm.content" 
            type="textarea" 
            :rows="4"
            placeholder="请输入笔记内容..."
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="noteDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveNote">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const route = useRoute()
const router = useRouter()

// 响应式数据
const course = ref({})
const videoPlayer = ref(null)
const isPlaying = ref(false)
const currentTime = ref(0)
const learningProgress = ref(0)
const currentChapter = ref(0)
const showCustomControls = ref(false)
const notes = ref([])
const noteDialogVisible = ref(false)
const noteForm = ref({ timestamp: 0, content: '' })
const editingNoteId = ref(null)

// 课程大纲（模拟数据）
const courseOutline = ref([
  { title: '课程介绍', duration: 300, progress: 0 },
  { title: '基础知识', duration: 1200, progress: 0 },
  { title: '实战演练', duration: 1800, progress: 0 },
  { title: '总结回顾', duration: 600, progress: 0 }
])

// 相关课程（模拟数据）
const relatedCourses = ref([
  {
    id: 1,
    title: 'Vue.js 进阶教程',
    instructorName: '张老师',
    coverImage: 'https://via.placeholder.com/120x80'
  },
  {
    id: 2,
    title: 'React 实战开发',
    instructorName: '李老师',
    coverImage: 'https://via.placeholder.com/120x80'
  }
])

// 计算属性
const progressPercent = computed(() => {
  if (!course.value.duration) return 0
  return (currentTime.value / course.value.duration) * 100
})

// 获取课程信息
const fetchCourse = async () => {
  try {
    const response = await axios.get(`/api/course/${route.params.id}`)
    course.value = response.data.data || {}
    
    // 获取学习进度
    const progressResponse = await axios.get(`/api/course/${route.params.id}/progress`)
    learningProgress.value = progressResponse.data.data?.progress || 0
    currentTime.value = progressResponse.data.data?.currentTime || 0
    
    // 设置视频播放位置
    if (videoPlayer.value && currentTime.value > 0) {
      videoPlayer.value.currentTime = currentTime.value
    }
  } catch (error) {
    ElMessage.error('获取课程信息失败')
  }
}

// 获取笔记
const fetchNotes = async () => {
  try {
    const response = await axios.get(`/api/course/${route.params.id}/notes`)
    notes.value = response.data.data || []
  } catch (error) {
    console.error('获取笔记失败:', error)
  }
}

// 视频事件处理
const onTimeUpdate = () => {
  if (videoPlayer.value) {
    currentTime.value = videoPlayer.value.currentTime
    learningProgress.value = (currentTime.value / course.value.duration) * 100
    
    // 更新当前章节
    updateCurrentChapter()
    
    // 定期保存进度
    if (Math.floor(currentTime.value) % 10 === 0) {
      saveProgress()
    }
  }
}

const onVideoPlay = () => {
  isPlaying.value = true
  // 记录播放行为
  recordPlayAction('play')
}

const onVideoPause = () => {
  isPlaying.value = false
  // 记录播放行为
  recordPlayAction('pause')
}

const onVideoEnded = () => {
  isPlaying.value = false
  learningProgress.value = 100
  // 记录完成行为
  recordPlayAction('complete')
  ElMessage.success('恭喜您完成课程学习！')
}

// 播放控制
const togglePlay = () => {
  if (videoPlayer.value) {
    if (isPlaying.value) {
      videoPlayer.value.pause()
    } else {
      videoPlayer.value.play()
    }
  }
}

const skipBackward = () => {
  if (videoPlayer.value) {
    videoPlayer.value.currentTime = Math.max(0, videoPlayer.value.currentTime - 10)
  }
}

const skipForward = () => {
  if (videoPlayer.value) {
    videoPlayer.value.currentTime = Math.min(
      course.value.duration,
      videoPlayer.value.currentTime + 10
    )
  }
}

const toggleFullscreen = () => {
  if (videoPlayer.value) {
    if (document.fullscreenElement) {
      document.exitFullscreen()
    } else {
      videoPlayer.value.requestFullscreen()
    }
  }
}

// 章节播放
const playChapter = (chapterIndex) => {
  currentChapter.value = chapterIndex
  // 计算章节开始时间
  let startTime = 0
  for (let i = 0; i < chapterIndex; i++) {
    startTime += courseOutline.value[i].duration
  }
  
  if (videoPlayer.value) {
    videoPlayer.value.currentTime = startTime
    videoPlayer.value.play()
  }
}

// 更新当前章节
const updateCurrentChapter = () => {
  let totalTime = 0
  for (let i = 0; i < courseOutline.value.length; i++) {
    totalTime += courseOutline.value[i].duration
    if (currentTime.value <= totalTime) {
      currentChapter.value = i
      break
    }
  }
}

// 笔记功能
const addNote = () => {
  noteForm.value = {
    timestamp: Math.floor(currentTime.value),
    content: ''
  }
  editingNoteId.value = null
  noteDialogVisible.value = true
}

const editNote = (note) => {
  noteForm.value = { ...note }
  editingNoteId.value = note.id
  noteDialogVisible.value = true
}

const saveNote = async () => {
  try {
    if (editingNoteId.value) {
      // 更新笔记
      await axios.put(`/api/course/${route.params.id}/notes/${editingNoteId.value}`, noteForm.value)
    } else {
      // 添加笔记
      await axios.post(`/api/course/${route.params.id}/notes`, noteForm.value)
    }
    
    noteDialogVisible.value = false
    await fetchNotes()
    ElMessage.success('笔记保存成功')
  } catch (error) {
    ElMessage.error('保存笔记失败')
  }
}

const deleteNote = async (noteId) => {
  try {
    await axios.delete(`/api/course/${route.params.id}/notes/${noteId}`)
    await fetchNotes()
    ElMessage.success('笔记删除成功')
  } catch (error) {
    ElMessage.error('删除笔记失败')
  }
}

// 保存学习进度
const saveProgress = async () => {
  try {
    await axios.post(`/api/course/${route.params.id}/progress`, {
      currentTime: currentTime.value,
      progress: learningProgress.value
    })
  } catch (error) {
    console.error('保存进度失败:', error)
  }
}

// 记录播放行为
const recordPlayAction = async (action) => {
  try {
    await axios.post(`/api/course/${route.params.id}/record`, {
      action,
      currentTime: currentTime.value,
      timestamp: Date.now()
    })
  } catch (error) {
    console.error('记录播放行为失败:', error)
  }
}

// 跳转到其他课程
const goToCourse = (courseId) => {
  router.push(`/course/${courseId}`)
}

// 工具函数
const formatDuration = (seconds) => {
  if (!seconds) return '00:00'
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = Math.floor(seconds % 60)
  
  if (hours > 0) {
    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
  }
  return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const formatTime = (seconds) => {
  return formatDuration(seconds)
}

const getProgressColor = (progress) => {
  if (progress >= 80) return '#67c23a'
  if (progress >= 60) return '#e6a23c'
  return '#f56c6c'
}

// 生命周期
onMounted(() => {
  fetchCourse()
  fetchNotes()
  
  // 键盘快捷键
  const handleKeydown = (e) => {
    if (e.target.tagName === 'INPUT' || e.target.tagName === 'TEXTAREA') return
    
    switch (e.code) {
      case 'Space':
        e.preventDefault()
        togglePlay()
        break
      case 'ArrowLeft':
        e.preventDefault()
        skipBackward()
        break
      case 'ArrowRight':
        e.preventDefault()
        skipForward()
        break
      case 'KeyF':
        e.preventDefault()
        toggleFullscreen()
        break
    }
  }
  
  document.addEventListener('keydown', handleKeydown)
  
  // 清理函数
  onUnmounted(() => {
    document.removeEventListener('keydown', handleKeydown)
    saveProgress()
  })
})
</script>

<style scoped>
.course-player {
  display: flex;
  height: 100vh;
  background: #f5f5f5;
}

.player-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.video-player {
  position: relative;
  background: #000;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 20px;
}

.main-video {
  width: 100%;
  height: 400px;
  object-fit: contain;
}

.custom-controls {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.8);
  padding: 10px;
  color: white;
}

.progress-bar {
  position: relative;
  height: 4px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 2px;
  margin-bottom: 10px;
  cursor: pointer;
}

.progress-fill {
  height: 100%;
  background: #409eff;
  border-radius: 2px;
  transition: width 0.1s;
}

.progress-handle {
  position: absolute;
  top: -4px;
  width: 12px;
  height: 12px;
  background: #409eff;
  border-radius: 50%;
  transform: translateX(-50%);
  cursor: pointer;
}

.control-buttons {
  display: flex;
  gap: 10px;
  justify-content: center;
}

.course-info {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.course-info h1 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 24px;
}

.meta-info {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
  color: #666;
  font-size: 14px;
}

.description {
  color: #666;
  line-height: 1.6;
  margin-bottom: 20px;
}

.learning-progress {
  margin-top: 20px;
}

.learning-progress h3 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 16px;
}

.progress-text {
  margin-top: 8px;
  color: #666;
  font-size: 14px;
}

.sidebar {
  width: 350px;
  padding: 20px;
  overflow-y: auto;
}

.outline-card,
.notes-card,
.related-card {
  margin-bottom: 20px;
}

.outline-list {
  max-height: 300px;
  overflow-y: auto;
}

.outline-item {
  padding: 10px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
  margin-bottom: 8px;
}

.outline-item:hover {
  background: #f5f7fa;
}

.outline-item.active {
  background: #e6f7ff;
  border-left: 3px solid #409eff;
}

.chapter-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.chapter-title {
  font-weight: 500;
  color: #333;
}

.chapter-duration {
  color: #666;
  font-size: 12px;
}

.chapter-progress {
  margin-top: 5px;
}

.notes-list {
  max-height: 300px;
  overflow-y: auto;
}

.note-item {
  padding: 10px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  margin-bottom: 10px;
}

.note-time {
  color: #409eff;
  font-size: 12px;
  margin-bottom: 5px;
}

.note-content {
  color: #333;
  line-height: 1.4;
  margin-bottom: 8px;
}

.note-actions {
  display: flex;
  gap: 5px;
}

.related-list {
  max-height: 300px;
  overflow-y: auto;
}

.related-item {
  display: flex;
  gap: 10px;
  padding: 10px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
  margin-bottom: 8px;
}

.related-item:hover {
  background: #f5f7fa;
}

.related-item img {
  width: 60px;
  height: 40px;
  object-fit: cover;
  border-radius: 4px;
}

.related-info {
  flex: 1;
}

.related-title {
  font-weight: 500;
  color: #333;
  font-size: 14px;
  margin-bottom: 2px;
}

.related-instructor {
  color: #666;
  font-size: 12px;
}
</style> 