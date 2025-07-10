<template>
  <div class="course-play-container">
    <div v-if="loading" class="loading">
      <el-skeleton :rows="10" animated />
    </div>
    
    <div v-else-if="course" class="play-content">
      <!-- 课程标题和导航 -->
      <div class="play-header">
        <div class="header-left">
          <el-button @click="$router.go(-1)" size="small">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
          <h1 class="course-title">{{ course.title }}</h1>
        </div>
        <div class="header-right">
          <span class="progress-text">学习进度：{{ learningProgress }}%</span>
        </div>
      </div>

      <!-- 视频播放区域 -->
      <div class="video-section">
        <div class="video-player">
          <video
            ref="videoPlayer"
            :src="currentVideoUrl"
            controls
            class="video-element"
            @timeupdate="onTimeUpdate"
            @ended="onVideoEnded"
            @play="onVideoPlay"
            @pause="onVideoPause"
          >
            您的浏览器不支持视频播放
          </video>
        </div>
        
        <div class="video-info">
          <h2>{{ currentChapter?.title || '课程视频' }}</h2>
          <p>{{ currentChapter?.description || course.description }}</p>
        </div>
      </div>

      <!-- 章节列表 -->
      <div class="chapters-section">
        <h3>课程章节</h3>
        <div class="chapters-list">
          <div
            v-for="(chapter, index) in chapters"
            :key="chapter.id"
            class="chapter-item"
            :class="{ 
              'active': currentChapterIndex === index,
              'completed': chapter.completed 
            }"
            @click="selectChapter(index)"
          >
            <div class="chapter-info">
              <div class="chapter-title">
                <span class="chapter-number">{{ index + 1 }}</span>
                <span class="title">{{ chapter.title }}</span>
              </div>
              <div class="chapter-meta">
                <span class="duration">{{ formatDuration(chapter.duration) }}</span>
                <el-icon v-if="chapter.completed" class="completed-icon"><Check /></el-icon>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 功能区 -->
      <div class="features-section">
        <el-tabs v-model="activeTab" class="features-tabs">
          <!-- AI问答 -->
          <el-tab-pane label="AI问答" name="aiqna">
            <div class="aiqna-section">
              <div class="aiqna-ask">
                <el-input 
                  v-model="aiQuestion" 
                  placeholder="向AI提问关于课程内容的问题..." 
                  clearable 
                  @keyup.enter="submitAiQuestion"
                  size="large"
                />
                <el-button type="primary" @click="submitAiQuestion" :loading="aiLoading" size="large">
                  <el-icon><ChatDotRound /></el-icon>
                  提问
                </el-button>
              </div>
              
              <div v-if="aiAnswer" class="aiqna-answer">
                <div class="ai-header">
                  <el-icon><Cpu /></el-icon>
                  <span class="ai-label">AI智能回复</span>
                </div>
                <div class="ai-content">{{ aiAnswer }}</div>
              </div>
              
              <div class="aiqna-history">
                <h4>历史问答</h4>
                <div v-if="aiQnaList.length === 0" class="no-history">
                  <el-empty description="暂无历史问答" />
                </div>
                <div v-else class="history-list">
                  <div v-for="item in aiQnaList" :key="item.id" class="aiqna-item">
                    <div class="question">Q: {{ item.question }}</div>
                    <div class="answer">A: <span class="ai-label">AI</span> {{ item.answer }}</div>
                    <div class="meta">{{ item.userName }} · {{ item.createTime }}</div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <!-- 学习笔记 -->
          <el-tab-pane label="学习笔记" name="notes">
            <div class="notes-section">
              <div class="notes-header">
                <h3>学习笔记</h3>
                <el-button type="primary" @click="showAddNote = true">
                  <el-icon><Edit /></el-icon>
                  添加笔记
                </el-button>
              </div>
              
              <!-- 添加笔记对话框 -->
              <el-dialog v-model="showAddNote" title="添加笔记" width="500px">
                <el-form :model="noteForm" label-width="80px">
                  <el-form-item label="笔记标题">
                    <el-input v-model="noteForm.title" placeholder="请输入笔记标题" />
                  </el-form-item>
                  <el-form-item label="笔记内容">
                    <el-input 
                      v-model="noteForm.content" 
                      type="textarea" 
                      :rows="6"
                      placeholder="请输入笔记内容"
                    />
                  </el-form-item>
                  <el-form-item label="时间点">
                    <el-input v-model="noteForm.timestamp" placeholder="视频时间点（秒）" />
                  </el-form-item>
                </el-form>
                <template #footer>
                  <el-button @click="showAddNote = false">取消</el-button>
                  <el-button type="primary" @click="addNote">保存笔记</el-button>
                </template>
              </el-dialog>
              
              <!-- 笔记列表 -->
              <div class="notes-list">
                <div v-if="notes.length === 0" class="no-notes">
                  <el-empty description="暂无笔记，开始记录您的学习心得吧" />
                </div>
                
                <div v-else class="note-items">
                  <div v-for="note in notes" :key="note.id" class="note-item">
                    <div class="note-header">
                      <h4>{{ note.title }}</h4>
                      <div class="note-actions">
                        <el-button size="small" @click="editNote(note)">编辑</el-button>
                        <el-button size="small" type="danger" @click="deleteNote(note.id)">删除</el-button>
                      </div>
                    </div>
                    <div class="note-content">
                      <p>{{ note.content }}</p>
                    </div>
                    <div class="note-meta">
                      <span class="timestamp">{{ formatTime(note.timestamp) }}</span>
                      <span class="time">{{ formatDate(note.createTime) }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <!-- 评论区 -->
          <el-tab-pane label="评论区" name="comments">
            <div class="comments-section">
              <div class="comments-header">
                <h3>课程评论</h3>
                <span class="comment-count">{{ comments.length }} 条评论</span>
              </div>
              
              <div class="comment-input">
                <el-input 
                  v-model="newComment" 
                  placeholder="写下您的学习感受和评论..." 
                  type="textarea"
                  :rows="3"
                  clearable 
                />
                <el-button type="primary" @click="submitComment" size="large">
                  <el-icon><ChatLineRound /></el-icon>
                  发表评论
                </el-button>
              </div>
              
              <div class="comments-list">
                <div v-if="comments.length === 0" class="no-comments">
                  <el-empty description="暂无评论" />
                </div>
                <div v-else class="comment-items">
                  <div v-for="comment in comments" :key="comment.id" class="comment-item">
                    <div class="comment-header">
                      <span class="user">{{ comment.userName }}</span>
                      <span class="time">{{ formatDate(comment.createTime) }}</span>
                    </div>
                    <div class="comment-content">
                      <p>{{ comment.content }}</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  ArrowLeft, Check, Close, VideoPlay, User, Clock, View, Star, ChatDotRound, Cpu, Edit, ChatLineRound
} from '@element-plus/icons-vue'
import { courseApi } from '@/api/course'

const route = useRoute()
const router = useRouter()

// 响应式数据
const loading = ref(false)
const course = ref(null)
const chapters = ref([])
const currentChapterIndex = ref(0)
const currentVideoUrl = ref('')
const activeTab = ref('info')
const showRejectDialog = ref(false)
const rejectForm = ref({
  reason: ''
})

// 模拟审核记录
const auditRecords = ref([
  {
    id: 1,
    auditor: '管理员A',
    action: 'submit',
    reason: '课程提交审核',
    createTime: '2024-07-01 10:00:00'
  },
  {
    id: 2,
    auditor: '系统',
    action: 'pending',
    reason: '等待审核',
    createTime: '2024-07-01 10:01:00'
  }
])

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
      if (chapters.value.length > 0) {
        currentVideoUrl.value = chapters.value[0].videoUrl || '/default-video.mp4'
      }
    } else {
      chapters.value = [{
        id: 1,
        title: course.value.title,
        description: course.value.description,
        duration: course.value.duration,
        videoUrl: '/default-video.mp4'
      }]
      currentVideoUrl.value = '/default-video.mp4'
    }
  } catch (error) {
    chapters.value = [{
      id: 1,
      title: course.value.title,
      description: course.value.description,
      duration: course.value.duration,
      videoUrl: '/default-video.mp4'
    }]
    currentVideoUrl.value = '/default-video.mp4'
  }
}

const selectChapter = (index) => {
  currentChapterIndex.value = index
  const chapter = chapters.value[index]
  currentVideoUrl.value = chapter.videoUrl || '/default-video.mp4'
}

const onTimeUpdate = () => {
  // 视频播放进度更新
}

const onVideoEnded = () => {
  // 视频播放结束
}

const onVideoPlay = () => {
  // 视频开始播放
}

const onVideoPause = () => {
  // 视频暂停
}

const approveCourse = async () => {
  try {
    await ElMessageBox.confirm(`确定要通过课程"${course.value.title}"吗？`, '确认通过', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })
    
    course.value.status = 'approved'
    auditRecords.value.push({
      id: Date.now(),
      auditor: '当前管理员',
      action: 'approve',
      reason: '审核通过',
      createTime: new Date().toISOString()
    })
    ElMessage.success('课程审核通过')
  } catch {
    // 用户取消操作
  }
}

const rejectCourse = () => {
  showRejectDialog.value = true
}

const confirmReject = async () => {
  if (!rejectForm.value.reason.trim()) {
    ElMessage.warning('请输入驳回理由')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要驳回课程"${course.value.title}"吗？`, '确认驳回', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    course.value.status = 'rejected'
    auditRecords.value.push({
      id: Date.now(),
      auditor: '当前管理员',
      action: 'reject',
      reason: rejectForm.value.reason,
      createTime: new Date().toISOString()
    })
    showRejectDialog.value = false
    rejectForm.value.reason = ''
    ElMessage.success('课程已驳回')
  } catch {
    // 用户取消操作
  }
}

const getStatusType = (status) => {
  switch (status) {
    case 'approved': return 'success'
    case 'pending': return 'warning'
    case 'rejected': return 'danger'
    default: return 'info'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 'approved': return '已通过'
    case 'pending': return '待审核'
    case 'rejected': return '已驳回'
    default: return '未知'
  }
}

const getAuditType = (action) => {
  switch (action) {
    case 'submit': return 'info'
    case 'approve': return 'success'
    case 'reject': return 'danger'
    case 'pending': return 'warning'
    default: return 'info'
  }
}

const getAuditText = (action) => {
  switch (action) {
    case 'submit': return '提交审核'
    case 'approve': return '审核通过'
    case 'reject': return '审核驳回'
    case 'pending': return '等待审核'
    default: return '未知'
  }
}

const formatDuration = (minutes) => {
  if (!minutes) return '0分钟'
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return hours > 0 ? `${hours}小时${mins}分钟` : `${mins}分钟`
}

const formatDate = (date) => {
  if (!date) return '未知'
  return new Date(date).toLocaleString('zh-CN')
}

onMounted(() => {
  console.log('=== Admin CoursePlay 组件挂载 ===')
  loadCourseDetail()
})
</script>

<style scoped src="./CoursePlay.css"></style> 