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
            v-for="(chapter, index) in chapters || []"
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

      <!-- 功能区域 -->
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
                  <div v-for="item in aiQnaList || []" :key="item.id" class="aiqna-item">
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
                  <div v-for="note in notes || []" :key="note.id" class="note-item">
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
              
              <div class="comment-list">
                <div v-if="comments.length === 0" class="no-comments">
                  <el-empty description="暂无评论，快来发表第一条评论吧" />
                </div>
                
                <div v-else class="comment-items">
                  <div v-for="comment in comments || []" :key="comment.id" class="comment-item">
                    <div class="comment-user">
                      <img :src="comment.userAvatar || '/default-avatar.jpg'" :alt="comment.userName" class="user-avatar">
                      <div class="user-info">
                        <span class="username">{{ comment.userName }}</span>
                        <span class="time">{{ comment.createTime }}</span>
                      </div>
                    </div>
                    <div class="comment-content">
                      {{ comment.content }}
                    </div>
                    <div class="comment-actions">
                      <el-button size="small" @click="likeComment(comment.id)">
                        <el-icon><Pointer /></el-icon>
                        {{ comment.likeCount || 0 }}
                      </el-button>
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
          <el-button type="primary" @click="$router.push('/user/courses')">返回课程列表</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Check, ArrowLeft, ChatDotRound, Cpu, Edit, ChatLineRound, Pointer
} from '@element-plus/icons-vue'
import { courseApi } from '@/api/course'

const route = useRoute()
const router = useRouter()

// 响应式数据
const loading = ref(false)
const course = ref(null)
const chapters = ref([])
const currentChapterIndex = ref(0)
const activeTab = ref('aiqna')
const showAddNote = ref(false)
const notes = ref([])
const learningProgress = ref(0)
const learningRecords = ref([])
const watchedTime = ref(0)

// 视频播放器引用
const videoPlayer = ref(null)

// 笔记表单
const noteForm = ref({
  title: '',
  content: '',
  timestamp: ''
})

// AI问答区
const aiQnaList = ref([])
const aiQuestion = ref('')
const aiAnswer = ref('')
const aiLoading = ref(false)

// 评论区
const comments = ref([])
const newComment = ref('')

// 计算属性
const currentChapter = computed(() => {
  return chapters.value[currentChapterIndex.value] || null
})

const currentVideoUrl = computed(() => {
  const courseId = route.params.id
  if (currentChapter.value?.videoUrl || course.value?.videoUrl) {
    // 使用视频流接口
    return `/api/course/${courseId}/video`
  }
  return ''
})

// 方法
const loadCourseDetail = async () => {
  try {
    loading.value = true
    const courseId = route.params.id
    console.log('CoursePlay - Loading course detail for ID:', courseId)
    const response = await courseApi.getCourseDetail(courseId)
    
    if (response.code === 200) {
      course.value = response.data
      await loadChapters()
      await loadNotes()
      await loadLearningProgress()
      await loadComments()
      await loadAiQnaList()
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
        videoUrl: course.value.videoUrl,
        duration: course.value.duration,
        completed: false
      }]
    }
  } catch (error) {
    console.error('加载章节失败:', error)
    // 创建默认章节
    chapters.value = [{
      id: 1,
      title: course.value.title,
      description: course.value.description,
      videoUrl: course.value.videoUrl,
      duration: course.value.duration,
      completed: false
    }]
  }
}

const loadNotes = async () => {
  try {
    const courseId = route.params.id
    const response = await courseApi.getNotes(courseId)
    if (response.code === 200) {
      notes.value = response.data || []
    }
  } catch (error) {
    console.error('加载笔记失败:', error)
  }
}

const loadLearningProgress = async () => {
  try {
    const courseId = route.params.id
    const response = await courseApi.getLearningProgress(courseId)
    if (response.code === 200) {
      const data = response.data
      learningProgress.value = data.progress || 0
      learningRecords.value = data.records || []
      watchedTime.value = data.watchedTime || 0
    }
  } catch (error) {
    console.error('加载学习进度失败:', error)
  }
}

const loadComments = async () => {
  try {
    const courseId = route.params.id
    const response = await courseApi.getComments(courseId)
    if (response.code === 200) {
      comments.value = response.data || []
    }
  } catch (error) {
    console.error('加载评论失败:', error)
  }
}

const loadAiQnaList = async () => {
  try {
    const courseId = route.params.id
    const response = await courseApi.getAiQnaList(courseId)
    if (response.code === 200) {
      aiQnaList.value = response.data || []
    }
  } catch (error) {
    console.error('加载AI问答失败:', error)
  }
}

const selectChapter = (index) => {
  currentChapterIndex.value = index
  // 重置视频播放器
  if (videoPlayer.value) {
    videoPlayer.value.currentTime = 0
  }
}

const onTimeUpdate = () => {
  // 记录观看时间
  if (videoPlayer.value) {
    watchedTime.value = Math.floor(videoPlayer.value.currentTime)
  }
}

const onVideoEnded = () => {
  // 标记当前章节为已完成
  if (chapters.value[currentChapterIndex.value]) {
    chapters.value[currentChapterIndex.value].completed = true
  }
  ElMessage.success('当前章节学习完成！')
}

const onVideoPlay = () => {
  // 视频开始播放
}

const onVideoPause = () => {
  // 视频暂停
}

const addNote = async () => {
  if (!noteForm.value.title.trim() || !noteForm.value.content.trim()) {
    ElMessage.warning('请填写完整的笔记信息')
    return
  }

  try {
    const courseId = route.params.id
    const response = await courseApi.addNote({
      courseId,
      ...noteForm.value
    })
    
    if (response.code === 200) {
      ElMessage.success('笔记添加成功')
      showAddNote.value = false
      noteForm.value = { title: '', content: '', timestamp: '' }
      await loadNotes()
    } else {
      ElMessage.error('笔记添加失败')
    }
  } catch (error) {
    console.error('添加笔记失败:', error)
    ElMessage.error('添加失败，请稍后重试')
  }
}

const editNote = (note) => {
  noteForm.value = { ...note }
  showAddNote.value = true
}

const deleteNote = async (noteId) => {
  try {
    const response = await courseApi.deleteNote(noteId)
    if (response.code === 200) {
      ElMessage.success('笔记删除成功')
      await loadNotes()
    } else {
      ElMessage.error('笔记删除失败')
    }
  } catch (error) {
    console.error('删除笔记失败:', error)
    ElMessage.error('删除失败，请稍后重试')
  }
}

const submitAiQuestion = async () => {
  if (!aiQuestion.value.trim()) {
    ElMessage.warning('请输入问题')
    return
  }
  
  try {
    aiLoading.value = true
    const courseId = route.params.id
    const response = await courseApi.aiAsk({ 
      courseId, 
      question: aiQuestion.value 
    })
    
    if (response.code === 200) {
      aiAnswer.value = response.data.answer
      aiQuestion.value = ''
      await loadAiQnaList()
      ElMessage.success('AI回复已生成')
    } else {
      ElMessage.error('AI回复生成失败')
    }
  } catch (error) {
    console.error('AI问答失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    aiLoading.value = false
  }
}

const submitComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  
  try {
    const courseId = route.params.id
    const response = await courseApi.submitComment({ 
      courseId, 
      content: newComment.value 
    })
    
    if (response.code === 200) {
      ElMessage.success('评论发表成功')
      newComment.value = ''
      await loadComments()
    } else {
      ElMessage.error('评论发表失败')
    }
  } catch (error) {
    console.error('发表评论失败:', error)
    ElMessage.error('发表失败，请稍后重试')
  }
}

const likeComment = async (commentId) => {
  try {
    const response = await courseApi.likeComment(commentId)
    if (response.code === 200) {
      ElMessage.success('点赞成功')
      await loadComments()
    } else {
      ElMessage.error('点赞失败')
    }
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

const formatDuration = (minutes) => {
  if (!minutes) return '0分钟'
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return hours > 0 ? `${hours}小时${mins}分钟` : `${mins}分钟`
}

const formatTime = (seconds) => {
  if (!seconds) return '00:00'
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const formatDate = (date) => {
  if (!date) return '未知'
  return new Date(date).toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadCourseDetail()
})
</script>

<style scoped>
.course-play-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.loading {
  padding: 40px;
}

.play-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.course-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.progress-text {
  color: #409eff;
  font-weight: 500;
}

.video-section {
  margin-bottom: 30px;
}

.video-player {
  width: 100%;
  background: #000;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 20px;
}

.video-element {
  width: 100%;
  height: 500px;
  background: #000;
}

.video-info {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.video-info h2 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 18px;
}

.video-info p {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

.chapters-section {
  margin-bottom: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.chapters-section h3 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 18px;
}

.chapters-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.chapter-item {
  padding: 15px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  background: white;
}

.chapter-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.chapter-item.active {
  border-color: #409eff;
  background: #f0f9ff;
}

.chapter-item.completed {
  border-color: #67c23a;
  background: #f0f9ff;
}

.chapter-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chapter-title {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.chapter-number {
  width: 24px;
  height: 24px;
  background: #409eff;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 12px;
}

.title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.chapter-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.duration {
  color: #666;
  font-size: 12px;
}

.completed-icon {
  color: #67c23a;
  font-size: 16px;
}

.features-section {
  background: #fff;
  border-radius: 8px;
}

.features-tabs {
  background: #fff;
}

/* AI问答区样式 */
.aiqna-section {
  padding: 20px 0;
}

.aiqna-ask {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.aiqna-answer {
  background: #f0f9ff;
  border: 1px solid #b3d8ff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 20px;
}

.ai-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  color: #409eff;
  font-weight: 600;
}

.ai-content {
  color: #333;
  line-height: 1.6;
}

.aiqna-history h4 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 16px;
}

.no-history {
  text-align: center;
  padding: 40px 0;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.aiqna-item {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 15px;
  border-left: 4px solid #409eff;
}

.aiqna-item .question {
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.aiqna-item .answer {
  color: #666;
  line-height: 1.5;
}

.aiqna-item .meta {
  color: #999;
  font-size: 12px;
  margin-top: 8px;
}

.ai-label {
  color: #409eff;
  font-weight: 600;
}

/* 学习笔记样式 */
.notes-section {
  padding: 20px 0;
}

.notes-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.notes-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.no-notes {
  text-align: center;
  padding: 40px 0;
}

.note-items {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.note-item {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e0e0e0;
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
  font-size: 16px;
}

.note-actions {
  display: flex;
  gap: 8px;
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
  display: flex;
  justify-content: space-between;
  color: #999;
  font-size: 12px;
}

/* 评论区样式 */
.comments-section {
  padding: 20px 0;
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.comments-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.comment-count {
  color: #666;
  font-size: 14px;
}

.comment-input {
  margin-bottom: 20px;
}

.comment-input .el-button {
  margin-top: 10px;
}

.no-comments {
  text-align: center;
  padding: 40px 0;
}

.comment-items {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.comment-item {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e0e0e0;
}

.comment-user {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.username {
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.time {
  color: #999;
  font-size: 12px;
}

.comment-content {
  color: #333;
  line-height: 1.6;
  margin-bottom: 10px;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
}

.not-found {
  text-align: center;
  padding: 60px 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .play-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  .video-element {
    height: 300px;
  }
  
  .aiqna-ask {
    flex-direction: column;
  }
  
  .note-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
  
  .note-meta {
    flex-direction: column;
    gap: 5px;
  }
  
  .comment-user {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style> 