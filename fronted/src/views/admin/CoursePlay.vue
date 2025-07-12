<template>
  <div class="course-play-container">
    <div v-if="loading" class="loading">
      <el-skeleton :rows="10" animated />
    </div>
    <div v-else-if="course" class="play-content">
      <!-- 课程信息区 -->
      <div class="course-info-card">
        <el-button @click="goBack" size="small" class="back-btn">
          <el-icon><ArrowLeft /></el-icon> 返回
        </el-button>
        <span class="course-title">{{ course.title }}</span>
        <el-progress :percentage="learningProgress" status="success" :stroke-width="4" class="progress-bar-inline"/>
      </div>
      <div class="main-content-vertical">
        <!-- 视频区 -->
        <div class="video-feature-card">
          <div class="video-section">
            <div class="video-player-wrap">
              <template v-if="currentVideoUrl">
                <video
                  ref="videoPlayer"
                  :src="currentVideoUrl"
                  controls
                  class="video-element"
                  @timeupdate="onTimeUpdate"
                  @ended="onVideoEnded"
                  @play="onVideoPlay"
                  @pause="onVideoPause"
                >您的浏览器不支持视频播放</video>
              </template>
              <template v-else>
                <div class="video-placeholder">
                  <el-icon style="font-size:64px;color:#bbb;"><VideoPlay /></el-icon>
                  <div style="color:#888;margin-top:12px;">暂无可播放视频</div>
                </div>
              </template>
            </div>
            <div class="video-info">
              <h2>{{ course?.title || '课程视频' }}</h2>
              <p>{{ course?.description || '' }}</p>
            </div>
          </div>
        </div>
        <!-- 功能区tab -->
        <div class="features-section">
          <el-tabs v-if="tabNames.length > 0" v-model="activeTab" class="features-tabs" type="border-card" stretch>
            <el-tab-pane label="AI问答" name="aiqna">
              <div class="feature-card">
                <div class="aiqna-ask-row">
                  <el-input 
                    v-model="aiQuestion" 
                    placeholder="向AI提问关于课程内容的问题..." 
                    clearable 
                    @keyup.enter="submitAiQuestion"
                    size="large"
                  />
                  <el-button type="primary" @click="submitAiQuestion" :loading="aiLoading" size="large">
                    <el-icon><ChatDotRound /></el-icon> 提问
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
                  <div v-if="Array.isArray(aiQnaList) && aiQnaList.length === 0" class="no-history">
                    <el-empty description="暂无历史问答" />
                  </div>
                  <div v-else-if="Array.isArray(aiQnaList) && aiQnaList.length > 0" class="history-list">
                    <div v-for="item in aiQnaList" :key="item.id" class="aiqna-item">
                      <div class="question">Q: {{ item.question }}</div>
                      <div class="answer">A: <span class="ai-label">AI</span> {{ item.answer }}</div>
                      <div class="meta">{{ item.userName }} · {{ item.createTime }}</div>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>
            <el-tab-pane label="学习笔记" name="notes">
              <div class="feature-card">
                <div class="notes-header-row">
                  <h3>学习笔记</h3>
                  <el-button type="primary" @click="showAddNote = true">
                    <el-icon><Edit /></el-icon> 添加笔记
                  </el-button>
                </div>
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
                <div class="notes-list">
                  <div v-if="Array.isArray(notes) && notes.length === 0" class="no-notes">
                    <el-empty description="暂无笔记，开始记录您的学习心得吧" />
                  </div>
                  <div v-else-if="Array.isArray(notes) && notes.length > 0" class="note-items">
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
            <el-tab-pane label="评论区" name="comments">
              <div class="feature-card">
                <div class="comments-header-row">
                  <h3>课程评论</h3>
                  <span class="comment-count">{{ Array.isArray(comments) ? comments.length : 0 }} 条评论</span>
                </div>
                <div class="comment-input-row">
                  <el-input 
                    v-model="newComment" 
                    placeholder="写下您的学习感受和评论..." 
                    type="textarea"
                    :rows="3"
                    clearable 
                  />
                  <el-button type="primary" @click="submitComment" size="large">
                    <el-icon><ChatLineRound /></el-icon> 发表评论
                  </el-button>
                </div>
                <div class="comments-list">
                  <div v-if="Array.isArray(comments) && comments.length === 0" class="no-comments">
                    <el-empty description="暂无评论" />
                  </div>
                  <div v-else-if="Array.isArray(comments) && comments.length > 0" class="comment-items">
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
    </div>
    <div v-else class="not-found">
      <el-result icon="warning" title="课程不存在" sub-title="抱歉，您访问的课程不存在或已被删除">
        <template #extra>
          <el-button type="primary" @click="goToCourseList">返回课程列表</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<style scoped>
.course-play-container {
  padding: 32px 0;
  background: #f6f8fa;
  min-height: 100vh;
}
.play-content {
  max-width: 1100px;
  margin: 0 auto;
}
.course-info-card {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 18px;
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  padding: 10px 24px;
  margin-bottom: 18px;
  min-height: 36px;
  height: 36px;
}
.back-btn {
  margin-bottom: 0;
}
.course-title {
  font-size: 1.15rem;
  font-weight: 600;
  color: #222;
  margin-right: 8px;
  white-space: nowrap;
}
.progress-bar-inline {
  width: 56px;
  height: 4px;
  margin-left: 0;
}
:deep(.el-progress__text) {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  font-size: 0.75rem;
  color: #409eff;
  font-weight: 600;
  margin: 0;
  padding: 0;
  line-height: 1;
}
:deep(.el-progress-bar) {
  position: relative;
}
.main-content-vertical {
  display: flex;
  flex-direction: column;
  gap: 18px;
}
.video-feature-card {
  background: none;
  box-shadow: none;
  padding: 0;
}
.video-section {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  padding: 14px 14px 8px 14px;
  margin-bottom: 0;
  min-width: 720px;
  max-width: 100%;
}
.video-player-wrap {
  width: 100%;
  aspect-ratio: 16/9;
  background: #f5f7fa;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
  overflow: hidden;
}
.video-element {
  width: 100%;
  height: 100%;
  border-radius: 10px;
  background: #000;
}
.video-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #bbb;
}
.video-info {
  margin-top: 4px;
}
.video-info h2 {
  font-size: 1.1rem;
  font-weight: 600;
  color: #222;
}
.video-info p {
  color: #666;
  margin-top: 2px;
  font-size: 0.98rem;
}
.chapters-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  padding: 10px 8px;
  width: 100%;
  min-width: 720px;
  max-width: 100%;
  max-height: 180px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}
.chapters-card-below {
  margin-top: 0;
}
.chapters-list-scroll {
  flex: 1;
  overflow-y: auto;
  margin-top: 6px;
  scrollbar-width: thin;
  scrollbar-color: #e0e7ef #fff;
}
.chapters-list-scroll::-webkit-scrollbar {
  width: 4px;
  background: #fff;
}
.chapters-list-scroll::-webkit-scrollbar-thumb {
  background: #e0e7ef;
  border-radius: 4px;
}
.chapter-item {
  padding: 7px 8px;
  border-radius: 7px;
  margin-bottom: 4px;
  cursor: pointer;
  transition: background 0.15s, box-shadow 0.15s;
  border: 2px solid transparent;
  font-size: 0.98rem;
}
.chapter-item.active {
  background: #e8f4fd;
  border-color: #409eff;
  box-shadow: 0 1px 4px rgba(64,158,255,0.06);
}
.chapter-item.completed .chapter-number {
  color: #67c23a;
}
.chapter-item:hover {
  background: #f0f7ff;
}
.chapter-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}
.chapter-number {
  background: #409eff22;
  color: #409eff;
  border-radius: 6px;
  padding: 2px 8px;
  font-size: 0.98rem;
  margin-right: 4px;
}
.completed-icon {
  color: #67c23a;
  margin-left: 4px;
}
.chapter-meta-row {
  color: #888;
  font-size: 0.93rem;
  margin-top: 2px;
}
.features-section {
  margin-top: 0;
}
.features-tabs {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  padding: 0 0 8px 0;
}
.feature-card {
  padding: 14px 12px 4px 12px;
  background: #f9fbfd;
  border-radius: 10px;
  min-height: 140px;
}
.aiqna-ask-row, .comment-input-row, .notes-header-row, .comments-header-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}
.aiqna-ask-row .el-input, .comment-input-row .el-input {
  flex: 1;
}
.aiqna-answer, .aiqna-history, .notes-list, .comments-list {
  margin-top: 8px;
}
.aiqna-item, .note-item, .comment-item {
  background: #fff;
  border-radius: 7px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.03);
  padding: 8px 10px;
  margin-bottom: 6px;
}
.no-history, .no-notes, .no-comments {
  padding: 12px 0;
}
.el-empty {
  margin: 0 auto;
}
@media (max-width: 1100px) {
  .play-content {
    max-width: 100vw;
    padding: 0 8px;
  }
  .video-section, .chapters-card {
    min-width: 0;
    width: 100%;
  }
}
@media (max-width: 800px) {
  .course-info-card {
    flex-direction: column;
    align-items: flex-start;
    padding: 10px 4px 6px 4px;
  }
  .main-content-vertical {
    gap: 8px;
  }
  .video-section, .chapters-card {
    padding: 4px 1px;
    border-radius: 7px;
    min-width: 0;
  }
  .feature-card {
    padding: 6px 2px 2px 2px;
    border-radius: 7px;
  }
}
</style>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Check, VideoPlay, ChatDotRound, Cpu, Edit, ChatLineRound } from '@element-plus/icons-vue'
import { courseApi } from '@/api/course'
import { askAI } from '@/api/register'
import { adminApi } from '@/api/course'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()

// 响应式数据
const loading = ref(false)
const course = ref(null)
const notes = ref([])
const comments = ref([])
const aiQnaList = ref([])
// 1. 移除章节相关的响应式数据、方法和UI
// 删除 chapters、currentChapterIndex、selectChapter、loadChapters、currentChapter、章节区相关模板
// 2. 直接用 course.value?.videoUrl 作为 currentVideoUrl
const currentVideoUrl = computed(() => getStreamUrl(course.value?.videoUrl || ''))
const activeTab = ref('aiqna')
const showAddNote = ref(false)
const noteForm = ref({ title: '', content: '', timestamp: '' })
const aiQuestion = ref('')
const aiAnswer = ref('')
const aiLoading = ref(false)
const newComment = ref('')
const learningProgress = ref(0)

// tabs 固定，保证健壮性
const tabNames = computed(() => ['aiqna', 'notes', 'comments'])

// 课程不存在/无效id时自动跳转
const goToCourseList = () => {
  router.push('/admin/course')
}
const goBack = () => {
  router.back()
}

// 课程详情加载
const loadCourseDetail = async () => {
  const courseId = route.params.id
  if (!courseId || isNaN(Number(courseId)) || Number(courseId) <= 0) {
    ElMessage.error(`课程ID无效：${courseId}`)
    goToCourseList()
    return
  }
  try {
    loading.value = true
    const response = await courseApi.getCourseDetail(courseId)
    if (response.code === 200 && response.data) {
      course.value = response.data
    } else {
      course.value = null
      ElMessage.error(`课程不存在或已被删除，ID=${courseId}`)
      setTimeout(goToCourseList, 1200)
    }
  } catch (error) {
    course.value = null
    ElMessage.error('网络错误，请稍后重试')
    setTimeout(goToCourseList, 1200)
  } finally {
    loading.value = false
  }
}

const getStreamUrl = (videoUrl) => {
  if (!videoUrl) return ''
  // 只取对象名部分（如 course-videos/xxx.mp4）
  const match = videoUrl.match(/course-videos\/[^/]+\.mp4$/)
  if (match) {
    return `/api/file/stream?objectName=${encodeURIComponent(match[0])}`
  }
  // 兼容其它格式
  return videoUrl
}

// 修改 loadCourseDetail 只加载课程详情，不加载章节
// const loadChapters = async () => {
//   const courseId = route.params.id
//   try {
//     const response = await courseApi.getChapters(courseId)
//     chapters.value = response.code === 200 ? (response.data || []) : []
//     if (Array.isArray(chapters.value) && chapters.value.length > 0) {
//       currentVideoUrl.value = getStreamUrl(chapters.value[0].videoUrl || '')
//     } else {
//       currentVideoUrl.value = ''
//     }
//   } catch {
//     chapters.value = []
//     currentVideoUrl.value = ''
//   }
// }

// 在模板中，视频播放区直接用 <video :src="currentVideoUrl" ...>，不再依赖章节
// const selectChapter = (index) => {
//   if (!Array.isArray(chapters.value) || !chapters.value[index]) return
//   currentChapterIndex.value = index
//   const chapter = chapters.value[index]
//   currentVideoUrl.value = getStreamUrl(chapter.videoUrl || '')
// }

// tabs健壮性 watch
watch([course, tabNames], () => {
  if (!tabNames.value.includes(activeTab.value)) {
    activeTab.value = tabNames.value[0]
  }
})

// 课程不存在时重置 activeTab
watch(course, (val) => {
  if (val === null) {
    activeTab.value = tabNames.value[0]
  }
})

// 其它功能方法（略，保持原有健壮性和提示）
const onTimeUpdate = () => {}
const onVideoEnded = () => {}
const onVideoPlay = () => {}
const onVideoPause = () => {}
const submitAiQuestion = async () => {
  if (!aiQuestion.value.trim()) {
    ElMessage.warning('请输入问题')
    return
  }
  aiLoading.value = true
  aiAnswer.value = ''
  try {
    const res = await askAI(aiQuestion.value)
    aiAnswer.value = res.answer || 'AI无回复'
    aiQnaList.value.unshift({
      id: Date.now(),
      question: aiQuestion.value,
      answer: aiAnswer.value,
      userName: '你',
      createTime: new Date().toLocaleString()
    })
    aiQuestion.value = ''
  } catch (e) {
    aiAnswer.value = 'AI服务暂时不可用'
  }
  aiLoading.value = false
}
const addNote = () => { ElMessage.info('添加笔记功能待接入') }
const editNote = (note) => { ElMessage.info('编辑笔记功能待接入') }
const deleteNote = (id) => { ElMessage.info('删除笔记功能待接入') }
const submitComment = () => { ElMessage.info('评论功能待接入') }

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
const formatTime = (s) => {
  if (!s) return ''
  const sec = Number(s)
  if (isNaN(sec)) return ''
  const m = Math.floor(sec / 60)
  const ss = sec % 60
  return m ? `${m}分${ss}秒` : `${ss}秒`
}

// 在审核操作前打印参数，便于排查
const userStore = useUserStore()
const approveCourse = async () => {
  console.log('审核通过参数:', course.value?.id, 'approved', '')
  try {
    const reviewerId = userStore.user?.id || ''
    const res = await adminApi.reviewCourse(course.value?.id, 'approved', '', reviewerId)
    console.log('[审核] 审核通过响应:', res)
    if (res.code === 200) {
      ElMessage.success('审核通过')
      loadCourseDetail()
    } else {
      ElMessage.error(res.message || '审核失败')
    }
  } catch (e) {
    ElMessage.error('审核请求异常')
  }
}

const rejectCourse = async (reason) => {
  console.log('审核驳回参数:', course.value?.id, 'rejected', reason)
  try {
    const reviewerId = userStore.user?.id || ''
    const res = await adminApi.reviewCourse(course.value?.id, 'rejected', reason, reviewerId)
    console.log('[审核] 审核驳回响应:', res)
    if (res.code === 200) {
      ElMessage.success('已驳回')
      loadCourseDetail()
    } else {
      ElMessage.error(res.message || '审核失败')
    }
  } catch (e) {
    ElMessage.error('审核请求异常')
  }
}

onMounted(() => {
  loadCourseDetail()
})
</script> 